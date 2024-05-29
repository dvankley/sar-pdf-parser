package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.exceptions.dump
import org.apache.commons.cli.DefaultParser
import java.io.File
import java.util.*

object Main {
    @JvmStatic fun main(args : Array<String>) {
        try {
            val parser = DefaultParser()
            val line = parser.parse(ArgumentParser.getDefaultOptions(), args)

            val directoryPath = line.getOptionValue("d") ?: throw IllegalArgumentException("Could not parse input directory")
            println("Target directory: $directoryPath")

            val folder = File(directoryPath)
            val listOfFiles = folder.listFiles { _, filename ->
                filename
                    .lowercase(Locale.getDefault())
                    .endsWith(".pdf")
            }?.filterNotNull()
                ?: throw RuntimeException("Target directory $directoryPath contained no files")

            val reader = PdfReader(listOfFiles)
            reader.startProcessing()
        } catch (e: Exception) {
            println("Top level exception: ${e.dump()}")
        }
    }
}
