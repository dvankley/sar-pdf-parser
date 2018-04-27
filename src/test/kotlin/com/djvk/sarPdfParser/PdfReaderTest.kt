package com.djvk.sarPdfParser


import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.FileReader
import java.io.BufferedReader
import kotlin.test.assertEquals


class PdfReaderTest {
    @Test
    fun base() {
        val files: Array<File> = arrayOf(File("src/test/resources/testInput/sample-2016.pdf"))
        PdfReader(files).startProcessing()
        val expected = File("src/test/resources/testOutput/outfile.csv").readText()
        val actual = File("outfile.csv").readText()
        assertThat(actual).isEqualTo(expected)
    }
}