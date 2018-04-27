package com.djvk.sarPdfParser

import java.util.*

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter

import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.io.BufferedWriter
import kotlin.collections.ArrayList


public class CsvWriter(outFile: String) {

    val csvPrinter: CSVPrinter

    init {
        val writer = Files.newBufferedWriter(Paths.get(outFile))
        csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT.withHeader( * CsvHeaders.DOC_SAR ) )
    }

    fun insertRow(row: Map<String,String>, fileName: String){
        val list = getRowValuesInOrder(row)
        list.add(fileName)
        csvPrinter.printRecord(list)
    }

    fun insertRows(rows: Array<Map<String,String>>, fileName: String){
        rows.forEach {
            insertRow(it, fileName)
        }
    }

    fun finish(){
        csvPrinter.flush();
    }

    fun getRowValuesInOrder(row: Map<String,String>): MutableList<String> {
        val ret: MutableList<String> = ArrayList()
        CsvHeaders.DOC_SAR.forEach {
            ret.add(row.get(it) ?: "")
        }
        return ret
    }

}