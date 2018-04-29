package com.djvk.sarPdfParser

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
            val parsedText = HashMap<String, String>()
            val text = getLayoutText(document)


            val efcNumber = getEFCNumber(text)
            parsedText["EFC Number"] = efcNumber

            val regex = Regex("""^\s*\d+\S?\.(.*)[:\?](.*)${'$'}""", RegexOption.MULTILINE)
            val matchResults = regex.findAll(text)
            for (matchResult in matchResults) {
                val groups = matchResult.groups
                if (groups.size == 3) {
                    val label = groups[1]!!.value.trim()
                    val response = groups[2]!!.value.trim()
                    parsedText.put(PdfFieldNormalizer.normalize(label), response)
                }
            }
            return mapToCSVMap(parsedText)
        }
    }

    private fun getLayoutText(document: PDDocument): String {
        val stripper = PDFLayoutTextStripper()
        return stripper.getText(document)
    }


    private fun getEFCNumber(pdfContent: String): String {
        val regexForEFCWithLabel = """EFC:\s*\d+""".toRegex()
        val efcNumberWithLabel = regexForEFCWithLabel.find(pdfContent, 0)?.value ?: ""

        val regexForEFCNumber = """\d+""".toRegex()
        return regexForEFCNumber.find(efcNumberWithLabel, 0)?.value ?: ""
    }

    private fun mapToCSVMap(m: HashMap<String, String>): HashMap<String, String> {
        val csvMap = HashMap<String, String>()
        for (header in CsvHeaders.Fields.values()) {
            val normalizedFieldName = PdfFieldNormalizer.normalize(header.pdfFieldName)
            val fieldValue = m[normalizedFieldName]
            if (fieldValue != null) {
                csvMap[normalizedFieldName] = fieldValue
            }
        }

        return csvMap
    }
}
