package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.exceptions.FileProcessingException
import kotlinx.coroutines.experimental.*
import java.io.File

class PdfReader(private val files: Array<File>) {
    fun startProcessing() {
        val parser = PdfParser()
        val CsvWriter = CsvWriter("outfile.csv")
        val context = CommonPool
        val jerbs: MutableList<Deferred<Map<String, String>>> = ArrayList()
        files.forEach({ file ->
            jerbs.add(async(context, CoroutineStart.DEFAULT, {
                try {
                    parser.processFile(file)
                } catch (e: Exception) {
                    throw FileProcessingException(file.name, e)
                }
            }))
        })
        jerbs.forEach({ jerb ->
            runBlocking {
                // TODO: write output from here
                val output = jerb.await()
                CsvWriter.insertRow(output)
            }
        })
    }
}
