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

        // do stuff
        var pdfStripper = PDFTextStripper()
        var pdfContents = pdfStripper.getText(document)
        println(pdfContents)

        return HashMap()
    }
}