package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.*
import com.djvk.sarPdfParser.constants.fields.FfelLoanFields
import com.djvk.sarPdfParser.constants.fields.FilterablePdfTableField
import com.djvk.sarPdfParser.constants.fields.PerkinsLoanFields
import com.djvk.sarPdfParser.constants.sections.FileSection
import com.djvk.sarPdfParser.exceptions.FileProcessingException
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File
import java.util.*

class SarPdfParser {
    private val dateRegex = """\d{2}/\d{2}/\d{4}"""
    val applicationReceiptPrefix = """Application[$spaces]*Receipt"""
    val processedPrefix = """Processed"""

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
    suspend fun processText(text: String): Map<CsvHeaders.Fields, String> {
        val fileSections = identifyFileSections(text)

        val parsedValues = HashMap<CsvHeaders.Fields, String>()
        validateReportYear(text)
        // Header
        // "Estimated Federal Student Aid section"
        // Answers
            // Student Section
                // Personal Identifiers



        parseLoansTablesFields(text, parsedValues)
        parseGlobalFields(text, parsedValues)
        parseGeneralTableFields(text, parsedValues)

        return parsedValues
    }

    data class SectionMatch(
        val headerMatch: MatchResult,
        val fullSectionIndices: IntRange,
    )

    fun identifyFileSections(fullText: String): Map<FileSection, SectionMatch> {
        val sectionHeaders = mutableListOf<Pair<FileSection, MatchResult>>()

        var lastPosition = 0
        // Iterate over section definitions to find header locations
        for (section in FileSection.values()) {
            // Each match should be the next match of the search pattern after the last match
            val match = section.searchPattern.find(fullText, lastPosition)
            if (match != null) {
                sectionHeaders.add(Pair(section, match))
                lastPosition = match.range.last
            }
        }

        val fileSections = mutableMapOf<FileSection, SectionMatch>()
        // Iterate over header locations to consolidate full section ranges
        for (i in 0 until sectionHeaders.size) {
            val (section, match) = sectionHeaders[i]
            val start = if (i == 0) {
                0
            } else {
                match.range.last
            }
            val end = if (i == sectionHeaders.size - 1) {
                fullText.length - 1
            } else {
                val (_, nextMatch) = sectionHeaders[i + 1]
                nextMatch.range.first
            }

            fileSections[section] = SectionMatch(match, start..end)
        }

        for (fileSection in FileSection.values()) {
            if (!fileSections.containsKey(fileSection)) {
                throw IllegalArgumentException("Failed to find document section '${fileSection.name}'")
            }
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

    private fun parseGlobalFields(
        text: String,
        parsedValues: MutableMap<CsvHeaders.Fields, String>
    ) {
        val efc = getEFCNumber(text)
//        parsedValues[CsvHeaders.Fields.EFC_NUMBER] = efc.number ?: ""
//        parsedValues[CsvHeaders.Fields.IS_EFC_STARRED] = if (efc.isStarred) "1" else "0"
//        parsedValues[CsvHeaders.Fields.HAS_EFC_C_SUFFIX] = if (efc.hasCSuffix) "1" else "0"
//        parsedValues[CsvHeaders.Fields.HAS_EFC_H_SUFFIX] = if (efc.hasHSuffix) "1" else "0"
        val headerData = getHeaderTableDataAfter2022(text)
        parsedValues[CsvHeaders.Fields.RECEIVED_DATE] = headerData.receivedDate
        parsedValues[CsvHeaders.Fields.PROCESSED_DATE] = headerData.processedDate
        val (startYear, endYear) = getReportYears(text)
        parsedValues[CsvHeaders.Fields.YEAR] = "$startYear-$endYear"
    }

    fun getDate(pdfContent: String, prefixString: String): String {
        val regex = """$prefixString[$spaces]*Date:[$spaces]*($dateRegex)""".toRegex(RegexOption.IGNORE_CASE)
        val rawDate = regex.find(pdfContent, 0)

        return rawDate?.groupValues?.get(1) ?: throw RuntimeException("Failed to find $prefixString date")
    }

    data class HeaderTableData(
        val receivedDate: String,
        val processedDate: String,
        val DRN: Int,
    )

    data class HeaderPattern(
        val pattern: String,
        val parser: (MatchResult) -> HeaderTableData,
    )

    fun getHeaderTableDataAfter2022(pdfContent: String): HeaderTableData {
        val patterns = listOf(
            HeaderPattern(
                """Application[$spaces]*Receipt[$spaces]*Date:[$spaces]*Processed[$spaces]*Date:[$spaces]*""" +
                        """Data[$spaces]*Release[$spaces]*Number[$spaces]*\(DRN\)[$spaces\s]*""" +
                        """($dateRegex)[$spaces]*($dateRegex)[$spaces]*(\d+)"""
            ) { match ->
                HeaderTableData(
                    match.groupValues[1],
                    match.groupValues[2],
                    match.groupValues[3].toInt(),
                )
            },
            HeaderPattern(
                """Processed[$swn]*Date:[$swn]*($dateRegex)[$swn]*""" +
                        """Data[$swn]*Release[$swn]*Number[$swn]*\(DRN\)[$swn]*(\d+)[$swn]*""" +
                        """Application[$swn]*Receipt[$swn]*Date:[$swn]*($dateRegex)"""
            ) { match ->
                HeaderTableData(
                    match.groupValues[3],
                    match.groupValues[1],
                    match.groupValues[2].toInt(),
                )
            },
        )

        for ((pattern, parser) in patterns) {
            val regex = pattern.toRegex(RegexOption.IGNORE_CASE)
            val matches = regex.find(pdfContent, 0) ?: continue
            return parser(matches)
        }

        throw RuntimeException("Failed to find header table dates")
    }

    fun getEFCNumber(pdfContent: String): EfcData {
        val regex =
            """Expected[$spaces]*Family[$spaces]*Contribution:[$spaces]*(\d+)[$spaces]*(\*)?[$spaces]*([ch])?""".toRegex(
                RegexOption.IGNORE_CASE
            )
        val rawEfc = regex.find(pdfContent, 0)

        return EfcData(
            rawEfc?.groupValues?.get(1),
            rawEfc?.groups?.get(2) != null,
            rawEfc?.groupValues?.get(3)?.lowercase(Locale.getDefault()) == "c",
            rawEfc?.groupValues?.get(3)?.lowercase(Locale.getDefault()) == "h"
        )
    }

    data class EfcData(
        val number: String?,
        val isStarred: Boolean,
        val hasCSuffix: Boolean,
        val hasHSuffix: Boolean
    )

    private fun parseGeneralTableFields(
        text: String,
        parsedValues: MutableMap<CsvHeaders.Fields, String>
    ) {
        val spaces = PdfNormalizer.groupByAsciiForRegex(' ')
        // For post-2022, include the line number in the capture because (we hope) the line number formatting is
        //  more consistent, and we need the line number to increase accuracy for cases like "parent's adjusted
        //  gross income" where the search string is not globally unique
        val regex = Regex(
            """^[$spaces]*(\d{1,3}\w?\.[$spaces]*.+?)[:?]([$spaces\w].+?)${'$'}""",
            setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL)
        )

        val matchResults = regex.findAll(text)
        val fieldsFound = HashSet<CsvHeaders.Fields>()

        for (matchResult in matchResults) {
            if (matchResult.groups.size != 3) continue

            val (fieldString, response) = getFieldAndResponse(matchResult)
            val field = CsvHeaders.fieldsByNormalizedPdfName[PdfNormalizer.normalizeField(fieldString)] ?: continue
            fieldsFound.add(field)
            parsedValues[field] = getFieldValue(field, response, parsedValues)
        }

        val allRequiredFieldsFilled = CsvHeaders.requiredTableFields.all { requiredField ->
            val value = parsedValues[requiredField] ?: return@all false
            value.isNotBlank()
        }
        if (!allRequiredFieldsFilled) {
            throw Exception("File did not contain student's first name, last name, DOB, and last 4 of SSN")
        }

        // Subtract the table fields we've found from all possible table fields
        val fieldsToReview = CsvHeaders.Fields.values()
            .filter { it.pdfTableFieldNames != null }
            .minus(fieldsFound)

        parsedValues[CsvHeaders.Fields.FIELDS_TO_REVIEW] = fieldsToReview.joinToString(", ") { it.csvFieldName }
    }

    private fun getFieldAndResponse(matchResult: MatchResult): Pair<String, String> {
        val groups = matchResult.groups
        val rawLabel = groups[1]!!.value.trim()
        // Check if YES/NO response is interleaved in label because life is suffering
        val labelRegex = Regex("""[\s$spaces](No|Yes)[\s$spaces]""")
        val valueInLabelMatch = labelRegex.find(rawLabel)
        val valueInLabel = valueInLabelMatch?.groups?.get(1)?.value
        // If YES/NO response is not interleaved in label
        return if (valueInLabel == null) {
            Pair(rawLabel, groups[2]!!.value.trim())
        } else {
            // Otherwise response is interleaved in label and we need to pull it out
            Pair(rawLabel.replace(valueInLabel, ""), valueInLabel)
        }
    }

    enum class ResponseValues(val outputValue: String) {
        EMPTY(""),
        YES("YES"),
        NO("NO");

        companion object {
            fun fromString(inputValue: String?): ResponseValues {
                if (inputValue == null) return EMPTY
                return try {
                    valueOf(inputValue.toUpperCase().trim())
                } catch (e: IllegalArgumentException) {
                    EMPTY
                }
            }
        }
    }

    private fun getFieldValue(
        field: CsvHeaders.Fields,
        rawValue: String,
        parsedValues: Map<CsvHeaders.Fields, String>
    ): String {
//        if (field == CsvHeaders.Fields.UNACCOMPANIED_HOMELESS_YOUTH) {
//            val newResponse = ResponseValues.fromString(rawValue)
//            val oldResponseString = parsedValues[CsvHeaders.Fields.UNACCOMPANIED_HOMELESS_YOUTH]?.toUpperCase()?.trim()
//            val oldResponse = ResponseValues.fromString(oldResponseString)
//
//            // YES takes precedence
//            return if (newResponse == ResponseValues.YES || oldResponse == ResponseValues.YES) {
//                ResponseValues.YES.outputValue
//            } else if (newResponse == ResponseValues.NO || oldResponse == ResponseValues.NO) {
//                // If no YES but there are any NOs, let's call it NO
//                ResponseValues.NO.outputValue
//            } else {
//                // If no YES or NO, call it EMPTY
//                ResponseValues.EMPTY.outputValue
//            }
//        }
        return rawValue
    }

    fun parseLoansTablesFields(text: String, parsedValues: MutableMap<CsvHeaders.Fields, String>) {
        val ffelHeaderRegex = Regex(
            """^[$spaces\n]*FFEL[$spaces\n]*Program[$spaces\n]*Loans[$spaces\n]*and/or[$spaces\n]*Direct[$spaces\n]*Loans""",
            setOf(RegexOption.MULTILINE)
        )
        val ffelHeaderMatch = ffelHeaderRegex.find(text, 0) ?: return

        val perkinsHeaderRegex = Regex(
            """^[$spaces\n]*Federal[$spaces\n]*Perkins[$spaces\n]*Loan[$spaces\n]*Amounts""",
            setOf(RegexOption.MULTILINE)
        )
        val perkinsHeaderMatch = perkinsHeaderRegex.find(text, ffelHeaderMatch.range.last) ?: return

        val teachHeaderRegex = Regex(
            """^[$spaces\n]*TEACH[$spaces\n]*Grants[$spaces\n]*Converted[$spaces\n]*to[$spaces\n]*Direct[$spaces\n]*Unsubsidized[$spaces\n]*Loans""",
            setOf(RegexOption.MULTILINE)
        )
        val teachHeaderMatch = teachHeaderRegex.find(text, perkinsHeaderMatch.range.last) ?: return

        val ffelText = text.substring(ffelHeaderMatch.range.last + 1, perkinsHeaderMatch.range.first)
        val perkinsText = text.substring(perkinsHeaderMatch.range.last + 1, teachHeaderMatch.range.first)

        val loanValueRegex = Regex(
            """((?:\${'$'}\d+,\d{3})|(?:\${'$'}0)|(?:N/A))""",
            setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE)
        )

        // Iterate over each FFEL field and try to find its data in the search text
        val ffelLabelMatches = findTableLabelPositions(ffelText, FfelLoanFields.values().toList())
        val ffelValueMatches = findTableInterleavedValues(ffelText, loanValueRegex, ffelLabelMatches)

        ffelValueMatches.forEachIndexed { ffelIndex, ffelValueMatch ->
            val loanField = FfelLoanFields.values()[ffelIndex]
            if (ffelValueMatch.size != 3) {
                throw IllegalArgumentException("Expected 3 FFEL value matches, found ${ffelValueMatch.size}")
            }
            parsedValues[loanField.balanceField] = ffelValueMatch[0].groupValues[1]
            parsedValues[loanField.remainingField] = ffelValueMatch[1].groupValues[1]
            parsedValues[loanField.totalField] = ffelValueMatch[2].groupValues[1]
        }

        // And again for Perkins
        val perkinsLabelMatches = findTableLabelPositions(perkinsText, PerkinsLoanFields.values().toList())
        val perkinsValueMatches = findTableInterleavedValues(perkinsText, loanValueRegex, perkinsLabelMatches)

        perkinsValueMatches.forEachIndexed { perkinsIndex, perkinsValueMatch ->
            val loanField = PerkinsLoanFields.values()[perkinsIndex]
            if (perkinsValueMatch.size != 1) {
                throw IllegalArgumentException("Expected 1 Perkins value matches, found ${perkinsValueMatch.size}")
            }
            parsedValues[loanField.field] = perkinsValueMatch[0].groupValues[1]
        }
    }

    /**
     * Finds the position of an ordered sequence of labels given in [labelPatterns] with [text].
     * Each of [labelPatterns] will be split by whitespace and will still match even if there are non-matching
     *  characters between. This is intended to more effectively parse interleaved values.
     *
     * TODO: consider implementing this approach for all table fields, not just loan fields as it is currently.
     *
     * This output of this function is intended to be used with [findTableInterleavedValues]
     */
    fun findTableLabelPositions(text: String, labelPatterns: List<FilterablePdfTableField>): List<MatchResult> {
        val labelMatches = mutableListOf<MatchResult>()
        for (label in labelPatterns) {
            val searchStartIndex = if (labelMatches.isEmpty()) 0 else labelMatches.last().range.last + 1
            labelMatches.add(
                label.pdfLabelPattern
                    .toRegex(setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))
                    .find(text, searchStartIndex)
                    ?: throw IllegalArgumentException("Failed to find expected table label $label")
            )
        }
        return labelMatches
    }

    /**
     * @param labelMatches This is intended to be supplied by [findTableLabelPositions]
     */
    fun findTableInterleavedValues(
        text: String,
        valueRegex: Regex,
        labelMatches: List<MatchResult>
    ): List<List<MatchResult>> {
        val valueMatches = mutableListOf<List<MatchResult>>()
        labelMatches.forEachIndexed { index, matchResult ->
            val searchText = if (index == labelMatches.size - 1) {
                /** If this is the last label match, search from the start of this label to the end of [text] */
                text.substring(matchResult.range.first)
            } else {
                /** If this is not the last label match, search from the start of this label to just before the start
                 *  of the next label */
                text.substring(matchResult.range.first, labelMatches[index + 1].range.first - 1)
            }

            /**
             * Filter out all the label content so we can search for only values.
             * Any text in a capture group in [FilterablePdfTableField.pdfLabelPattern] is considered to be label text,
             *  and will be removed.
             * The remaining text will be searched for values.
             * This is done to tolerate (as best we can) values that are interleaved with labels.
             */
            val filteredSearchText = matchResult.groupValues
                // Omit group 0 because that has the entire capture
                .subList(1, matchResult.groupValues.size - 1)
                .fold(searchText) { acc, element -> acc.replace(element, "") }

            valueMatches.add(valueRegex.findAll(filteredSearchText).toList())
        }
        return valueMatches
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
