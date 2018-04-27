package com.djvk.sarPdfParser

import kotlinx.coroutines.experimental.*
import java.io.File

class PdfReader(val files: Array<File>) {
    fun startProcessing() {
        val parser = PdfParser()
        val context = CommonPool
        val jerbs: MutableList<Deferred<Map<String, String>>> = ArrayList()
        files.forEach({ file ->
            jerbs.add(async(context, CoroutineStart.DEFAULT, {
                parser.processFile(file)
            }))
        })
        jerbs.forEach({ jerb ->
            runBlocking {
                // TODO: write output from here
                val output = jerb.await()
            }
        })
    }
}
