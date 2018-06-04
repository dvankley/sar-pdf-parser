package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.exceptions.FileProcessingException
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

class PdfParser {
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
                parseGlobalFields(text, parsedValues)
                parseTableFields(text, parsedValues)
            } catch (e: Exception) {
                throw FileProcessingException(file.name, e)
            }

            return mapToCSVMap(parsedValues)
        }
    }

    private fun parseGlobalFields(text: String, parsedValues: MutableMap<CsvHeaders.Fields, String>) {
        val efc = getEFCNumber(text)
        parsedValues[CsvHeaders.Fields.EFC_NUMBER] = efc.number ?: ""
        parsedValues[CsvHeaders.Fields.IS_EFC_STARRED] = if (efc.isStarred) "1" else "0"
        parsedValues[CsvHeaders.Fields.HAS_EFC_C_SUFFIX] = if (efc.hasCSuffix) "1" else "0"
        parsedValues[CsvHeaders.Fields.HAS_EFC_H_SUFFIX] = if (efc.hasHSuffix) "1" else "0"
        parsedValues[CsvHeaders.Fields.RECEIVED_DATE] = getDate(text, applicationReceiptPrefix)
        parsedValues[CsvHeaders.Fields.PROCESSED_DATE] = getDate(text, processedPrefix)
        parsedValues[CsvHeaders.Fields.YEAR] = getYear(text)
    }

    fun getDate(pdfContent: String, prefixString: String): String {
        val dateRegex = """\d{2}/\d{2}/\d{4}"""
        val regex = """$prefixString[$spaces]*Date:[$spaces]*($dateRegex)""".toRegex(RegexOption.IGNORE_CASE)
        val rawDate = regex.find(pdfContent, 0)

        return rawDate?.groupValues?.get(1) ?: throw RuntimeException("Failed to find $prefixString date")
    }

    fun getEFCNumber(pdfContent: String): EfcData {
        // Regex contains a bunch of goofy alternate space characters
        val regex = """EFC:[$spaces]*(\d+)[$spaces]*(\*)?[$spaces]*([ch])?""".toRegex(RegexOption.IGNORE_CASE)
        val rawEfc = regex.find(pdfContent, 0)

        return EfcData(
                rawEfc?.groupValues?.get(1),
                rawEfc?.groups?.get(2) != null,
                rawEfc?.groupValues?.get(3)?.toLowerCase() == "c",
                rawEfc?.groupValues?.get(3)?.toLowerCase() == "h"
        )
    }

    data class EfcData(
            val number: String?,
            val isStarred: Boolean,
            val hasCSuffix: Boolean,
            val hasHSuffix: Boolean
    )

    fun getYear(pdfContent: String): String {
        val dashes = PdfNormalizer.groupByAsciiForRegex('-')
        val spaces = PdfNormalizer.groupByAsciiForRegex(' ')
        val yearRegex = """(\d{4}[$spaces]*[$dashes][$spaces]*\d{4})[$spaces]*Free[$spaces]*Application[$spaces]*for[$spaces]*Federal""".toRegex()
        val yearMatch = yearRegex.find(pdfContent)?.groupValues?.get(1)

        return PdfNormalizer.normalizeValue(yearMatch ?: throw RuntimeException("Failed to find SAR year in PDF"))
    }

    private fun parseTableFields(text: String, parsedValues: MutableMap<CsvHeaders.Fields, String>) {
        val regex = Regex("""^\s*\d+\S?\.(.*)[:\?](.*)${'$'}""", RegexOption.MULTILINE)
        val matchResults = regex.findAll(text)
        for (matchResult in matchResults) {
            val groups = matchResult.groups
            if (groups.size == 3) {
                val label = PdfNormalizer.normalizeField(groups[1]!!.value.trim())
                val field = CsvHeaders.fieldsByNormalizedPdfName[label] ?: continue
                val response = groups[2]!!.value.trim()
                parsedValues[field] = response
            }
        }
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
