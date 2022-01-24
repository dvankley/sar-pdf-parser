package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.CsvHeaders
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

class PdfReader(private val files: Array<File>) {
    fun startProcessing() {
        val parser = SarPdfParser()
        val primaryCsvWriter = CsvWriter("outfile.csv", CsvHeaders.DocType.SAR)
        val errorCsvWriter = CsvWriter("errors.csv", CsvHeaders.DocType.ERROR)
        val scope = CoroutineScope(SupervisorJob())
        val jerbs: MutableList<Pair<String, Deferred<Map<String, String>>>> = ArrayList()
        files.forEach { file ->
            jerbs.add(Pair(file.name, scope.async(EmptyCoroutineContext, CoroutineStart.DEFAULT) {
                parser.processFile(file)
            }))
        }
        jerbs.forEach { (fileName, jerb) ->
            runBlocking {
                try {
                    val output = jerb.await().toMutableMap()
                    output[PdfNormalizer.normalizeField(CsvHeaders.Fields.FILENAME.csvFieldName)] = fileName
                    primaryCsvWriter.insertRow(output)
                } catch (e: Exception) {
                    println("${e.message}\n${e.printStackTrace()}")
                    errorCsvWriter.insertRow(
                        mapOf(
                            PdfNormalizer.normalizeField(CsvHeaders.Fields.FILENAME.csvFieldName) to fileName,
                            PdfNormalizer.normalizeField(CsvHeaders.Fields.ERROR.csvFieldName) to getLastExceptionInChain(e).localizedMessage
                        )
                    )
                }
            }
        }
        errorCsvWriter.finish()
        primaryCsvWriter.finish()
    }

    private tailrec fun getLastExceptionInChain(e: Throwable): Throwable {
        val cause = e.cause ?: return e
        return getLastExceptionInChain(cause)
    }
}
