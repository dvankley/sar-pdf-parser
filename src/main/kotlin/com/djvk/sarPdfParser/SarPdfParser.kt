package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.SarFormat
import com.djvk.sarPdfParser.exceptions.FileProcessingException
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File
import java.util.*

class SarPdfParser {
    private val dashes = PdfNormalizer.groupByAsciiForRegex('-')
    private val spaces = PdfNormalizer.groupByAsciiForRegex(' ')
    val applicationReceiptPrefix = """Application[$spaces]*Receipt"""
    val processedPrefix = """Processed"""

    init {
        // PDFBox said to use this for JDK8 and later
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider")
    }

    suspend fun processFile(file: File): Map<String, String> {
        println("Processing file ${file.name}")
        PDDocument.load(file).use { document ->
            val text = getLayoutText(document)
            if (text.isEmpty() || text.isBlank()) {
                throw FileProcessingException(file.name, RuntimeException("No content parsed from file"))
            }
            val parsedValues = HashMap<CsvHeaders.Fields, String>()
            try {
                val format = determineReportFormat(text)
                parseGlobalFields(format, text, parsedValues)
                parseTableFields(format, text, parsedValues)
            } catch (e: Exception) {
                throw FileProcessingException(file.name, e)
            }

            return mapToCSVMap(parsedValues)
        }
    }

    fun getReportYears(text: String): Pair<Int, Int> {
        val reportYearRegex =
            """(\d{4})[$spaces]*[$dashes][$spaces]*(\d{4})[$spaces]*(?:Electronic)?[$spaces]*Student[$spaces]*Aid[$spaces]*Report"""
                .toRegex(RegexOption.IGNORE_CASE)
        val reportYearMatch = reportYearRegex.find(text, 0)

        return Pair(
            reportYearMatch?.groupValues?.get(1)?.toInt()
                ?: throw RuntimeException("Failed to find SAR start year in PDF"),
            reportYearMatch.groupValues[2].toInt(),
        )
    }

    private fun determineReportFormat(text: String): SarFormat {
        val (startYear, _) = getReportYears(text)
        return SarFormat.getFormatFromStartYear(startYear)
    }

    private fun parseGlobalFields(
        format: SarFormat,
        text: String,
        parsedValues: MutableMap<CsvHeaders.Fields, String>
    ) {
        val efc = getEFCNumber(format, text)
        parsedValues[CsvHeaders.Fields.EFC_NUMBER] = efc.number ?: ""
        parsedValues[CsvHeaders.Fields.IS_EFC_STARRED] = if (efc.isStarred) "1" else "0"
        parsedValues[CsvHeaders.Fields.HAS_EFC_C_SUFFIX] = if (efc.hasCSuffix) "1" else "0"
        parsedValues[CsvHeaders.Fields.HAS_EFC_H_SUFFIX] = if (efc.hasHSuffix) "1" else "0"
        parsedValues[CsvHeaders.Fields.RECEIVED_DATE] = getDate(text, applicationReceiptPrefix)
        parsedValues[CsvHeaders.Fields.PROCESSED_DATE] = getDate(text, processedPrefix)
        val (startYear, endYear) = getReportYears(text)
        parsedValues[CsvHeaders.Fields.YEAR] = "$startYear-$endYear"
    }

    fun getDate(pdfContent: String, prefixString: String): String {
        val dateRegex = """\d{2}/\d{2}/\d{4}"""
        val regex = """$prefixString[$spaces]*Date:[$spaces]*($dateRegex)""".toRegex(RegexOption.IGNORE_CASE)
        val rawDate = regex.find(pdfContent, 0)

        return rawDate?.groupValues?.get(1) ?: throw RuntimeException("Failed to find $prefixString date")
    }

    fun getEFCNumber(format: SarFormat, pdfContent: String): EfcData {
        val regex = when (format) {
            SarFormat.BEFORE_2021 -> {
                // Regex contains a bunch of goofy alternate space characters
                """EFC:[$spaces]*(\d+)[$spaces]*(\*)?[$spaces]*([ch])?""".toRegex(RegexOption.IGNORE_CASE)
            }
            SarFormat.AFTER_2022 -> {
                """Expected[$spaces]*Family[$spaces]*Contribution:[$spaces]*(\d+)[$spaces]*(\*)?[$spaces]*([ch])?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
            }
        }
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

    private fun parseTableFields(format: SarFormat, text: String, parsedValues: MutableMap<CsvHeaders.Fields, String>) {
        val spaces = PdfNormalizer.groupByAsciiForRegex(' ')
        val regex = Regex(
            """^[$spaces]*\d+\S?[.,](.+?)[:?]([$spaces\w].+?)${'$'}""",
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
            .filter { it.pdfTableFieldName != null }
            .minus(fieldsFound)

        parsedValues[CsvHeaders.Fields.FIELDS_TO_REVIEW] = fieldsToReview.joinToString(", ") { it.csvFieldName }
    }

    private fun getFieldAndResponse(matchResult: MatchResult): Pair<String, String> {
        val groups = matchResult.groups
        val rawLabel = groups[1]!!.value.trim()
        // Check if YES/NO response is interleaved in label because life is suffering
        val labelRegex = Regex("""[$spaces](NO|YES)[$spaces]""")
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
        if (field == CsvHeaders.Fields.UNACCOMPANIED_HOMELESS_YOUTH) {
            val newResponse = ResponseValues.fromString(rawValue)
            val oldResponseString = parsedValues[CsvHeaders.Fields.UNACCOMPANIED_HOMELESS_YOUTH]?.toUpperCase()?.trim()
            val oldResponse = ResponseValues.fromString(oldResponseString)

            // YES takes precedence
            return if (newResponse == ResponseValues.YES || oldResponse == ResponseValues.YES) {
                ResponseValues.YES.outputValue
            } else if (newResponse == ResponseValues.NO || oldResponse == ResponseValues.NO) {
                // If no YES but there are any NOs, let's call it NO
                ResponseValues.NO.outputValue
            } else {
                // If no YES or NO, call it EMPTY
                ResponseValues.EMPTY.outputValue
            }
        }
        return rawValue
    }

    private fun getLayoutText(document: PDDocument): String {
        val stripper = PDFLayoutTextStripper()
        return stripper.getText(document)
    }

    private fun mapToCSVMap(m: HashMap<CsvHeaders.Fields, String>): HashMap<String, String> {
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
