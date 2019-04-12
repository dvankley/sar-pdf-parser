package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.exceptions.FileProcessingException
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

class TranscriptPdfParser {
    private val spaces = PdfNormalizer.groupByAsciiForRegex(' ')
    val applicationReceiptPrefix = """Application[$spaces]*Receipt"""
    val processedPrefix = """Processed"""

    init {
        // PDFBox said to use this for JDK8 and later
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider")
    }

    suspend fun processFile(file: File): Map<String, String> {
        println("Processing file ${file.name}")
        PDDocument.load(file).use { document ->
            val text = getLayoutText(document).replace(0x00A0.toChar(), ' ')
            if (text.isEmpty() || text.isBlank()) {
                throw FileProcessingException(file.name, RuntimeException("No content parsed from file"))
            }
            val parsedValues = HashMap<CsvHeaders.Fields, String>()
            try {
                val id = getId(text)
                val name = getName(text)

                val programs = parseCurrentProgram(text)
                System.out.println(text)
            } catch (e: Exception) {
                throw FileProcessingException(file.name, e)
            }

            return mapToCSVMap(parsedValues)
        }
    }

    private fun parseTerms(text: String): MatchResult? {
        val regex = """(Term:.*?[\n\r]\s*College:.*?Cumulative:.*?${'$'})""".toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE))
        return regex.find(text, 0)
    }

    private fun getId(text: String): String? {
        val regex = """(\d{9})(.*)Display""".toRegex(RegexOption.DOT_MATCHES_ALL)
        return regex.find(text, 0)?.groupValues?.get(1)
    }

    private fun getName(text: String): String? {
        val regex = """(\d{9})(.*)Display""".toRegex(RegexOption.DOT_MATCHES_ALL)
        return regex.find(text, 0)?.groupValues?.get(2)?.trim()
    }

    private fun parseCurrentProgram(text: String): List<String>? {
        val parsedProgram = HashMap<String, String>()
        val regex = """Current\s*Program(.*)INSTITUTION\s*CREDIT""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val programText = regex.find(text, 0)?.groupValues?.get(1)?.trim()

        val tokens = programText?.split('\n')
        val relevantTokens = tokens?.filter { token ->
            !token.trim().equals("")
        }?.take(2)
        val program = relevantTokens?.map { token ->
            token.split(":").last().replace("Major OR Pathway and", "").trim()
        }
        return program
    }


    fun getLayoutText(document: PDDocument): String {
        val stripper = PDFLayoutTextStripper()
        return stripper.getText(document)
    }

    private fun mapToCSVMap(m: HashMap<CsvHeaders.Fields, String>): HashMap<String, String> {
        val csvMap = HashMap<String, String>()
        for (header in CsvHeaders.Fields.values()) {
            val fieldValue = m[header]
            if (fieldValue != null) {
                csvMap[PdfNormalizer.normalizeField(header.csvFieldName)] = PdfNormalizer.normalizeValue(fieldValue)
            }
        }

        return csvMap
    }
}
