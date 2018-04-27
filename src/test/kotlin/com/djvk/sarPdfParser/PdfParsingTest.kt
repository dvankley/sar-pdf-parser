package com.djvk.sarPdfParser

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class PdfParsingTest {
    @Test
    fun base() {
        val file = File("src/test/resources/testInput/sample-2016.pdf")
        val parser = PdfParser()
        var fileContents = runBlocking {
            parser.processFile(file)
        }
        assertThat(fileContents != null)
    }
}
