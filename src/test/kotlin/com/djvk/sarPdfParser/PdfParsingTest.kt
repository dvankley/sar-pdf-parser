package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.CsvHeaders
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class PdfParsingTest {
    @Test
    fun base() {
        val filesByName = getInputFiles()
        val parser = SarPdfParser()

        // Let's do this processing synchronously for now to make debugging easier
        val outputs = filesByName.map { (name, content) ->
            runBlocking {
                try {
                    parser.processText(content)
                } catch (e: Exception) {
                    throw Exception("Failed processing file $name", e)
                }
            }
        }

        for (output in outputs) {
            assertThat(output.isNotEmpty())
            runTestsOnFile(output)
        }
    }

    private fun runTestsOnFile(contents: Map<CsvHeaders.Fields, String>) {
        val assertions = mapOf(
            CsvHeaders.Fields.YEAR to "2024",
        )

        for ((key, value) in assertions) {
            val actual = contents[key]
                ?: throw AssertionError("Missing expected field $key")
            assertThat(actual)
                .isEqualTo(value)
                .withFailMessage("Got unexpected value for $key")
        }
    }

    private fun getInputFiles(): List<Pair<String, String>> {
        val directory = File("src/test/resources/testInput/")

        if (!directory.exists() || !directory.isDirectory) {
            throw RuntimeException("Could not find input file directory")
        }

        val files = directory.listFiles()
            ?: throw RuntimeException("Unable to find test input files")

        if (files.size != 3) {
            throw RuntimeException("Unexpected count of test input files")
        }

        return files
//            .filter { it.name == "variant3.txt" }
            .map { Pair(it.name, it.readText()) }
    }
}
