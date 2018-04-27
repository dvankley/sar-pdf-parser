package com.djvk.sarPdfParser

import java.util.*

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter

import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.io.BufferedWriter
import kotlin.collections.ArrayList


public class CsvWriter(outFile: String, docType: CsvHeaders.DocType) {

    val csvPrinter: CSVPrinter
    val docType = docType
    init {
        val writer = Files.newBufferedWriter(Paths.get(outFile))
        csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT.withHeader( *getHeaders(docType)))
    }

    fun getHeaders(docType: CsvHeaders.DocType): Array<String>{
        when (docType) {
            CsvHeaders.DocType.DOCTYPE_SAR -> {
                return CsvHeaders.DOC_SAR
            }
            CsvHeaders.DocType.DOCTYPE_ERROR -> {
                return  CsvHeaders.DOC_ERRORS
            }
        }
        return CsvHeaders.DOC_SAR
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

    fun getRowValuesInOrder(row: Map<String,String>): MutableList<String> {
        val ret: MutableList<String> = ArrayList()
        getHeaders(docType).forEach {
            ret.add(row.get(it) ?: "")
        }
        return ret
    }

}