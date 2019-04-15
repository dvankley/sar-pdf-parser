package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.exceptions.dump
import org.apache.commons.cli.DefaultParser
import java.awt.Image
import java.io.File

object Main {
    @JvmStatic fun main(args : Array<String>) {
        try {
            val parser = DefaultParser()
            val line = parser.parse(ArgumentParser.getDefaultOptions(), args)

            val directoryPath = line.getOptionValue("d") ?: throw IllegalArgumentException("Could not parse input directory")
            println("Target directory: $directoryPath")

            val folder = File(directoryPath)

            // attempt to convert images to pdfs first
            val converter = ImageToPdfConverter(folder.absolutePath)
            converter.convertFiles()

            val listOfPdfFiles = folder.listFiles { dir, filename -> filename.toLowerCase().endsWith(".pdf")}

            val reader = PdfReader(listOfPdfFiles)
            reader.startProcessing()
        } catch (e: Exception) {
            println("Top level exception: ${e.dump()}")
        }
    }
}
