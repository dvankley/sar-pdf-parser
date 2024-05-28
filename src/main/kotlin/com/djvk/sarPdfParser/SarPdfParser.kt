package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.*
import com.djvk.sarPdfParser.constants.sections.FileSection
import com.djvk.sarPdfParser.constants.sections.Section
import com.djvk.sarPdfParser.exceptions.FileProcessingException
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File
import java.util.*

class SarPdfParser {

    init {
        // PDFBox said to use this for JDK8 and later
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider")
    }

    /**
     * Top level entry point to process an entire PDF file
     */
    suspend fun processFile(file: File): Map<String, String> {
        println("Processing file ${file.name}")
        PDDocument.load(file).use { document ->
            val text = getLayoutText(document)
            if (text.isEmpty() || text.isBlank()) {
                throw FileProcessingException(file.name, RuntimeException("No content parsed from file"))
            }
            try {
                return mapToCSVMap(processText(text))
            } catch (e: Exception) {
                throw FileProcessingException(file.name, e)
            }
        }
    }

    /**
     * Parses relevant fields out of the text version of a PDF file
     */
    suspend fun processText(text: String): Map<CsvHeaders.Fields, CsvOutputValue> {
        val sanitizedText = sanitizeText(text)

        val fileSections = identifyFileSections(sanitizedText, FileSection.values().toList())

        val output = HashMap<CsvHeaders.Fields, CsvOutputValue>()
        for (fileSection in fileSections) {
            output += processSection(fileSection)
        }

        val allRequiredFieldsFilled = CsvHeaders.requiredTableFields.all { requiredField ->
            val value = output[requiredField] ?: return@all false
            value.isNotBlank()
        }
        if (!allRequiredFieldsFilled) {
            throw Exception("File did not contain student's first name, last name, DOB, and last 4 of SSN")
        }

        // Subtract the table fields we've found from all possible table fields
        val fieldsToReview = CsvHeaders.Fields.values()
            .filter { it.tableMatchPatterns != null }
            .minus(output.keys)
        output[CsvHeaders.Fields.FIELDS_TO_REVIEW] = fieldsToReview.joinToString(", ") { it.csvFieldName }

        return output
    }

    /**
     * Cleans up known artifacts in the text to improve reliability of the parsing code.
     */
    fun sanitizeText(input: String): String {
        val footerRegex1 = """https://studentaid\.gov/fafsa-apply.*$\n.*\n.*""".toRegex(RegexOption.MULTILINE)
        val out = footerRegex1.replace(input, "")

        return out
    }

    fun processSection(sectionMatch: SectionMatch): Map<CsvHeaders.Fields, CsvOutputValue> {
        val fields = CsvHeaders.fieldsBySection[sectionMatch.section]
            ?: listOf()
        val output = mutableMapOf<CsvHeaders.Fields, CsvOutputValue>()
        // Iterate over all field definitions that should be in this section
        fieldIteration@ for (field in fields) {
            if (field.tableMatchPatterns?.isNotEmpty() == true) {
                var haveAnyMatched = false
                // Table field
                // Iterate over all possible patterns for this field
                for (tablePattern in field.tableMatchPatterns) {
                    val matchValue = if (field.handleInterleaved) {
                        extractInterleavedValues(sectionMatch.text, tablePattern)
                    } else {
                        extractStandardTableValues(sectionMatch.text, tablePattern)
                    }
                    if (matchValue != null) {
                        haveAnyMatched = true
                        if (field.matchAnyYes) {
                            // Special case; logical OR.
                            if (matchValue.lowercase(Locale.getDefault()) == "yes") {
                                output[field] = "Yes"
                                continue@fieldIteration
                            }
                        } else {
                            // Default behavior, use the value from the first match
                            output[field] = postProcessValue(matchValue)
                            continue@fieldIteration
                        }
                    }
                }
                if (field.matchAnyYes && haveAnyMatched) {
                    /**
                     * In this case, we're doing the logical OR special case, we've matched at least
                     *  one label, but we haven't found any "yes"es.
                     * This is fine, just return "No."
                     */

                    output[field] = "No"
                    continue@fieldIteration
                }
                // Otherwise we haven't found any valid matches, so that's a paddlin'
                throw RuntimeException("Unable to find table field ${field.name}")
            } else {
                // Fields that need custom handling
                when (field) {
                    CsvHeaders.Fields.YEAR -> {
                        val yearRegex = """FORM[${spaces}]+(\d{4})[${dashes}][${spaces}]*\d{2}""".toRegex()
                        val match = yearRegex.find(sectionMatch.text)
                            ?: throw RuntimeException("Unable to parse form year")
                        output[field] = postProcessValue(match.groupValues[1])
                    }

                    CsvHeaders.Fields.RECEIVED_DATE -> {
                        val matches = textDateRegex.findAll(sectionMatch.text).toList()
                        if (matches.size != 2) {
                            throw RuntimeException("Unable to parse received and processed date")
                        }
                        output[field] = postProcessValue(matches[0].groupValues[0])
                    }

                    CsvHeaders.Fields.PROCESSED_DATE -> {
                        // This is a bit wasteful as it duplicates the previous case, but meh
                        val matches = textDateRegex.findAll(sectionMatch.text).toList()
                        if (matches.size != 2) {
                            throw RuntimeException("Unable to parse received and processed date")
                        }
                        output[field] = postProcessValue(matches[1].groupValues[0])
                    }

                    CsvHeaders.Fields.SAI -> {
                        val variants1And2Regex =
                            """correct[${spaces}]+your[${spaces}]+FAFSA[${spaces}]+information\.[${spaces}]+(-?\d{1,4})""".toRegex()
                        val variant3Regex =
                            """by[${spaces}]+your[${spaces}]+school[${spaces}]+to[${spaces}]+determine[${spaces}]+(-?\d{1,4})""".toRegex()
                        val match = variants1And2Regex.find(sectionMatch.text)
                            ?: variant3Regex.find(sectionMatch.text)
                            ?: throw RuntimeException("Unable to parse SAI")
                        output[field] = postProcessValue(match.groupValues[1])
                    }

                    else -> throw IllegalArgumentException("Unexpected non-table field $field")
                }
            }
        }

        // Integrate output from child sections
        for (child in sectionMatch.children) {
            output += processSection(child)
        }

        return output
    }

    private fun postProcessValue(value: String): String {
        return value
            .trim()
            // Replace repeated spaces with a single space
            .replace("""[${spaces}]{2,}""".toRegex(), " ")
    }

    data class SectionMatch(
        val section: Section,
        val headerMatch: MatchResult,
        val text: String,
        /**
         * These are the indices of this section within the text of its containing section.
         *
         * Note that these indices are NOT global and are relative to the start of the
         *  containing text.
         */
        val fullSectionIndices: IntRange,
        val children: List<SectionMatch>,
    )

    fun identifyFileSections(
        containingText: String,
        sections: List<Section>,
    ): List<SectionMatch> {
        val sectionHeaders = mutableListOf<Pair<Section, MatchResult>>()

        var lastPosition = 0
        // Iterate over section definitions to find header locations
        for (section in sections) {
            // Each match should be the next match of the search pattern after the last match
            val match = section.searchPattern.find(containingText, lastPosition)
            if (match != null) {
                sectionHeaders.add(Pair(section, match))
                lastPosition = match.range.last
            } else {
                if (section.required) {
                    throw IllegalArgumentException("Missing expected section ${section::class.java.simpleName}.${section.name}")
                }
            }
        }

        val fileSections = mutableListOf<SectionMatch>()
        // Iterate over header locations to consolidate full section ranges
        for (i in 0 until sectionHeaders.size) {
            val (section, match) = sectionHeaders[i]
            val start = if (i == 0) {
                0
            } else {
                match.range.last
            }
            val end = if (i == sectionHeaders.size - 1) {
                containingText.length - 1
            } else {
                val (_, nextMatch) = sectionHeaders[i + 1]
                nextMatch.range.first
            }

            val children = section.children ?: listOf()

            val childMatches = if (children.isNotEmpty()) {
                // If the section has children
                identifyFileSections(
                    containingText.substring(start, end),
                    children,
                )
            } else {
                // If the section's children are null or empty
                listOf()
            }

            fileSections.add(
                SectionMatch(
                    section,
                    match,
                    containingText.substring(start, end),
                    start..end,
                    childMatches,
                )
            )
        }

        return fileSections
    }

    fun getReportYears(text: String): Pair<Int, Int> {
        val reportYearRegex1 =
            """(\d{4})[$spaces\n]*[$dashes][$spaces\n]*(\d{2})[$spaces\n]*(?:Electronic)?[$spaces\n]*Student[$spaces\n]*Aid[$spaces\n]*Report"""
                .toRegex(RegexOption.IGNORE_CASE)
        var reportYearMatch = reportYearRegex1.find(text, 0)

        // If the first regex didn't work, try again with an older format
        if (reportYearMatch == null) {
            val reportYearRegex2 =
                """(\d{4})[$spaces]*[$dashes][$spaces]*(\d{4})[$spaces]*Free[$spaces]*Application[$spaces]*for[$spaces]*Federal[$spaces]*Student[$spaces]*Aid"""
                    .toRegex(RegexOption.IGNORE_CASE)
            reportYearMatch = reportYearRegex2.find(text, 0)
        }

        return Pair(
            reportYearMatch?.groupValues?.get(1)?.toInt()
                ?: throw RuntimeException("Failed to find SAR start year in PDF"),
            // This'll be a problem in 70 years!
            reportYearMatch.groupValues[2].toInt() + 2000,
        )
    }

    private fun validateReportYear(text: String) {
        val (startYear, _) = getReportYears(text)
        if (startYear < 2024) {
            throw IllegalArgumentException("This version of the software only operates on 2024 and later FSS files. Found start year of $startYear")
        }
    }

    fun extractStandardTableValues(text: String, labelPatterns: List<RegexPattern>): String? {
        val labelPattern = labelPatterns.joinToString("[$spaces]+")
        val regex = (labelPattern + standardTableValuePattern)
            .toRegex(RegexOption.MULTILINE)
        val matchGroupValues = regex.find(text)?.groupValues
            ?: return null
        return (matchGroupValues[1].trim() + " " + matchGroupValues[2].trim()).trim()
    }


    fun extractInterleavedValues(text: String, labelPatterns: List<RegexPattern>): String {
        val workingLabelPatterns = labelPatterns.toMutableList()

        while (workingLabelPatterns.isNotEmpty()) {
            val pattern = (workingLabelPatterns.joinToString("""[$spaces]+""") + interleavedTableValueRegex)
                .toRegex(RegexOption.MULTILINE)
            val matches = pattern.findAll(text).toList()
            if (matches.isEmpty()) {
                // No match for this set of label terms, keep trying
                workingLabelPatterns.removeLast()
                continue
            } else if (matches.size > 1) {
                throw RuntimeException("Failed to find unique match while extracting interleaved values for label $labelPatterns")
            }
            // If we got here, we did find a unique match for our value regex
            return matches
                .first()
                .groupValues[1]
                .trim()
        }
        throw RuntimeException("Failed to extract interleaved values for label $labelPatterns")
    }

    private fun getLayoutText(document: PDDocument): String {
        val stripper = PDFLayoutTextStripper()
        return stripper.getText(document)
    }

    private fun mapToCSVMap(m: Map<CsvHeaders.Fields, String>): Map<String, String> {
        val csvMap = HashMap<String, String>()
        for (header in CsvHeaders.Fields.values()) {
            val fieldValue = m[header]
            if (fieldValue != null) {
                csvMap[PdfNormalizer.normalizeField(header.csvFieldName)] = PdfNormalizer.normalizeValue(fieldValue)
            }
        }

        return csvMap
    }
}
