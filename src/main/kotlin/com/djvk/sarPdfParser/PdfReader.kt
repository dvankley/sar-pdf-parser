package com.djvk.sarPdfParser

import kotlinx.coroutines.experimental.*
import java.io.File

class PdfReader(val files: Array<File>) {
    fun startProcessing() {
        val parser = PdfParser()
        val context = CommonPool
        files.forEach({ file ->
            async(context, CoroutineStart.DEFAULT, {
                parser.processFile(file)
            })
        })
    }
}
