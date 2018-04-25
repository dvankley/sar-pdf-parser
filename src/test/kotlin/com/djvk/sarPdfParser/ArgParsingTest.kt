package com.djvk.sarPdfParser

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ArgParsingTest {
    @Test
    fun base() {
        val out = ByteArrayOutputStream()
        System.setOut(PrintStream(out))
        Main.main(arrayOf("-d", "pdfs"))

        assertThat(out.toString()).isEqualTo("Target directory: pdfs\n")
    }
}