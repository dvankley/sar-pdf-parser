package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.exceptions.FileProcessingException
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

class PdfParser {
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
            val parsedValues = HashMap<String, String>()
            try {
                parseGlobalFields(text, parsedValues)
                parseTableFields(text, parsedValues)
            } catch (e: Exception) {
                throw FileProcessingException(file.name, e)
            }

            return mapToCSVMap(parsedValues)
        }
    }

    private fun parseGlobalFields(text: String, parsedValues: MutableMap<String, String>) {
        val (efc, isStarred) = getEFCNumber(text)
        parsedValues[PdfNormalizer.normalizeField(CsvHeaders.Fields.EFC_NUMBER.pdfFieldName)] = efc ?: ""
        parsedValues[PdfNormalizer.normalizeField(CsvHeaders.Fields.IS_EFC_STARRED.pdfFieldName)] = if (isStarred) "1" else "0"
        parsedValues[PdfNormalizer.normalizeField(CsvHeaders.Fields.YEAR.pdfFieldName)] = getYear(text)
    }

    fun getEFCNumber(pdfContent: String): Pair<String?, Boolean> {
        // Regex contains a bunch of goofy alternate space characters
        val spaces = PdfNormalizer.groupByAsciiForRegex(' ')
        val regex = """EFC:[$spaces]*(\d+)[$spaces]*(\*)?""".toRegex()
        val rawEfc = regex.find(pdfContent, 0)


        return Pair(rawEfc?.groupValues?.get(1), rawEfc?.groups?.get(2) != null)
    }

    fun getYear(pdfContent: String): String {
        val dashes = PdfNormalizer.groupByAsciiForRegex('-')
        val spaces = PdfNormalizer.groupByAsciiForRegex(' ')
        val yearRegex = """(\d{4}[$spaces]*[$dashes][$spaces]*\d{4})[$spaces]*Free[$spaces]*Application[$spaces]*for[$spaces]*Federal""".toRegex()
        val yearMatch = yearRegex.find(pdfContent)?.groupValues?.get(1)

        return PdfNormalizer.normalizeValue(yearMatch ?: throw RuntimeException("Failed to find SAR year in PDF"))
    }

    private fun parseTableFields(text: String, parsedValues: MutableMap<String, String>) {
        val regex = Regex("""^\s*\d+\S?\.(.*)[:\?](.*)${'$'}""", RegexOption.MULTILINE)
        val matchResults = regex.findAll(text)
        for (matchResult in matchResults) {
            val groups = matchResult.groups
            if (groups.size == 3) {
                val label = groups[1]!!.value.trim()
                val response = groups[2]!!.value.trim()
                parsedValues[PdfNormalizer.normalizeField(label)] = response
            }
        }
    }

    private fun getLayoutText(document: PDDocument): String {
        val stripper = PDFLayoutTextStripper()
        return stripper.getText(document)
    }

    private fun mapToCSVMap(m: HashMap<String, String>): HashMap<String, String> {
        val csvMap = HashMap<String, String>()
        for (header in CsvHeaders.Fields.values()) {
            val normalizedFieldName = PdfNormalizer.normalizeField(header.pdfFieldName)
            val fieldValue = m[normalizedFieldName]
            if (fieldValue != null) {
                csvMap[normalizedFieldName] = PdfNormalizer.normalizeValue(fieldValue)
            }
        }

        return csvMap
    }
}
