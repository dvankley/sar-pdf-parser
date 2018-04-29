package com.djvk.sarPdfParser

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
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


            val EFCNUmber = getEFCNumber(text)
            parsedText.put("EFC Number", EFCNUmber)

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

    fun getLayoutText(document: PDDocument): String {
        val stripper = PDFLayoutTextStripper()
        val text = stripper.getText(document)
        return text
    }


    fun getEFCNumber(pdfContent: String): String {
        val regexForEFCWithLabel = """EFC:\s*\d+""".toRegex()
        var EFCNUmberWithLabel = regexForEFCWithLabel.find(pdfContent, 0)?.value ?: ""


        val regexForEFCNumber = """\d+""".toRegex()
        val EFCNUmber = regexForEFCNumber.find(EFCNUmberWithLabel, 0)?.value ?: ""

        return EFCNUmber
    }

    fun mapToCSVMap(m: HashMap<String, String>): HashMap<String, String> {
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
