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
        val document: PDDocument = PDDocument.load(file)
        val parsedText = HashMap<String, String>()
        val text = getLayoutText(document)
        val regex = Regex("""^\s*\d+\S?\.(.*)[:\?](.*)${'$'}""", RegexOption.MULTILINE)
        val matchResults = regex.findAll(text)
        for (matchResult in matchResults) {
            val groups = matchResult.groups
            if (groups.size == 3) {
                val label = groups[1]!!.value.trim()
                val response = groups[2]!!.value.trim()
                parsedText.put(label, response)
            }
        }
        return parsedText;
    }

    fun getLayoutText(document: PDDocument): String {
        val stripper = PDFLayoutTextStripper()
        val text = stripper.getText(document)
        return text
    }
}
