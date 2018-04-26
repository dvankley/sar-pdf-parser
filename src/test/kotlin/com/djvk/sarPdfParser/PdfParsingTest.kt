package com.djvk.sarPdfParser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PdfParsingTest {
    @Test
    fun base() {
        val parser = PdfParser("src/test/resources/testInput/sample-2016.pdf")
        assertThat(parser.getCreator()).isEqualTo("PDFium")
    }
}
