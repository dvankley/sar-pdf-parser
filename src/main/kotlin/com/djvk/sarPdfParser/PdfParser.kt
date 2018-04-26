package com.djvk.sarPdfParser

import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

class PdfParser(val documentPath: String) {
    init {
        // PDFBox said to use this for JDK8 and later
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider")
    }
    val document = PDDocument.load(File(documentPath))

    fun getCreator(): String {
        return document.documentInformation.creator
    }
}