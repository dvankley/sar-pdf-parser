package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.CsvHeaders
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.collections.ArrayList

class CsvWriter(outFile: String, val docType: CsvHeaders.DocType) {
    private val csvPrinter: CSVPrinter
    init {
        val writer = Files.newBufferedWriter(Paths.get(outFile))
        val headers = getHeaders(docType)
                .map { it.csvFieldName }
                .toTypedArray()
        csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT.withHeader( *headers))
    }

    private fun getHeaders(docType: CsvHeaders.DocType): List<CsvHeaders.Fields> {
        return CsvHeaders.Fields.values()
                .filter { it.docType == docType || it.docType == CsvHeaders.DocType.ALL }
    }

    fun insertRow(row: Map<String,String>){
        val list = getRowValuesInOrder(row)
        csvPrinter.printRecord(list)
    }

    fun insertRows(rows: Array<Map<String,String>>, fileName: String){
        rows.forEach {
            insertRow(it)
        }
    }

    fun finish(){
        csvPrinter.flush();
    }

    private fun getRowValuesInOrder(row: Map<String,String>): MutableList<String> {
        val ret: MutableList<String> = ArrayList()
        getHeaders(docType).forEach {
            val fieldName = PdfNormalizer.normalizeField(it.csvFieldName)
            ret.add(row[fieldName] ?: "")
        }
        return ret
    }
}