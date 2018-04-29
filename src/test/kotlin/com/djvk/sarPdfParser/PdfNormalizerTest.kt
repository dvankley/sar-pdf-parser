package com.djvk.sarPdfParser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PdfNormalizerTest {
    @Test
    fun normalizeField_punctuation() {
        val pdfField = "Parent  2  (Father’s/Mother’s/Stepparent’s)    Social  Security  Number"
        val actual = PdfNormalizer.normalizeField(pdfField)
        val expected = "Parent2FathersMothersStepparentsSocialSecurityNumber"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun normalizeField_year() {
        val pdfField = "Student's  2015  Adjusted Gross  Income"
        val actual = PdfNormalizer.normalizeField(pdfField)
        val expected = "Students|normalizedYear|AdjustedGrossIncome"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun normalizeField_mangledYear() {
        val pdfField = "Spouse's  20   Income Earned from  Work"
        val actual = PdfNormalizer.normalizeField(pdfField)
        val expected = "Spouses|normalizedYear|IncomeEarnedfromWork"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun normalizeField_moreFunWithYears() {
        val pdfField = "Working  on  Master’s or  Doctorate  in  2017-2018"
        val actual = PdfNormalizer.normalizeField(pdfField)
        val expected = "WorkingonMastersorDoctoratein|normalizedYear||normalizedYear|"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun normalizeValue_softHyphen() {
        val pdfValue = "XXX\u00ADXX\u00AD1234"
        val actual = PdfNormalizer.normalizeValue(pdfValue)
        val expected = "XXX-XX-1234"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun normalizeValue_nbsp() {
        val pdfValue = "HIGH\u00A0SCHOOL"
        val actual = PdfNormalizer.normalizeValue(pdfValue)
        val expected = "HIGH SCHOOL"
        assertThat(actual).isEqualTo(expected)
    }
}
