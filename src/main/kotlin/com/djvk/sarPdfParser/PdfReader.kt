package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.exceptions.dump
import kotlinx.coroutines.experimental.*
import java.io.File

class PdfReader(private val files: Array<File>) {
    fun startProcessing() {
        val parser = SarPdfParser()
        val primaryCsvWriter = CsvWriter("outfile.csv", CsvHeaders.DocType.SAR)
        val errorCsvWriter = CsvWriter("errors.csv", CsvHeaders.DocType.ERROR)
        val context = CommonPool
        val jerbs: MutableList<Pair<String, Deferred<Map<String, String>>>> = ArrayList()
        files.forEach { file ->
            jerbs.add(Pair(file.name, async(context, CoroutineStart.DEFAULT, {
                parser.processFile(file)
            })))
        }
        jerbs.forEach { (fileName, jerb) ->
            runBlocking {
                try {
                    val output = jerb.await().toMutableMap()
                    output[PdfNormalizer.normalizeField(CsvHeaders.Fields.FILENAME.csvFieldName)] = fileName
                    primaryCsvWriter.insertRow(output)
                } catch (e: Exception) {
                    println("Error processing file $fileName ${e.dump()}")
                    errorCsvWriter.insertRow(mapOf(
                            PdfNormalizer.normalizeField(CsvHeaders.Fields.FILENAME.csvFieldName) to fileName,
                            PdfNormalizer.normalizeField(CsvHeaders.Fields.ERROR.csvFieldName) to e.localizedMessage
                    ))
                }
            }
        }
        errorCsvWriter.finish()
        primaryCsvWriter.finish()
    }
}
