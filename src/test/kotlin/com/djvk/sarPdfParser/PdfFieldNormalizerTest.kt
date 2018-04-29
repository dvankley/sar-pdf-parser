package com.djvk.sarPdfParser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PdfFieldNormalizerTest {
    @Test
    fun normalizationTest_punctuation() {
        val pdfField = "Parent  2  (Father’s/Mother’s/Stepparent’s)    Social  Security  Number"
        val actual = PdfFieldNormalizer.normalize(pdfField)
        val expected = "Parent2FathersMothersStepparentsSocialSecurityNumber"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun normalizationTest_year() {
        val pdfField = "Student's  2015  Adjusted Gross  Income"
        val actual = PdfFieldNormalizer.normalize(pdfField)
        val expected = "Students|normalizedYear|AdjustedGrossIncome"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun normalizationTest_mangledYear() {
        val pdfField = "Spouse's  20   Income Earned from  Work"
        val actual = PdfFieldNormalizer.normalize(pdfField)
        val expected = "Spouses|normalizedYear|IncomeEarnedfromWork"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun normalizationTest_moreFunWithYears() {
        val pdfField = "Working  on  Master’s or  Doctorate  in  2017-2018"
        val actual = PdfFieldNormalizer.normalize(pdfField)
        val expected = "WorkingonMastersorDoctoratein|normalizedYear||normalizedYear|"
        assertThat(actual).isEqualTo(expected)
    }
}
