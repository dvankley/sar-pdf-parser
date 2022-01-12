package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.CsvHeaders
import org.junit.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.test.assertEquals

class CsvWriterTest {
    @Test
    fun base() {
        var testFilename = "csvwritertest.csv"

        var subject = CsvWriter(testFilename, CsvHeaders.DocType.SAR)

        var expectedHeader =
            "EFC Number,Is EFC Starred,Has EFC C Suffix,Has EFC H Suffix,Received Date,Processed Date,Year,Student First Name,Student Middle Name,Student Last Name,Student Date of Birth,Social Security Number Last 4 Digits,Parent 1 Educational Level,Parent 2 Educational Level,Student's  2015  Adjusted Gross  Income,Parents'  2015  Adjusted Gross  Income,Does student have children they support?,Does student have other non-child/non-spouse dependents?,Parents deceased / student ward of court / in foster care,Emancipated minor?,In legal guardianship?,Unaccompanied homeless youth,Parents received SNAP,Parents received TANF,Student received SNAP,Student received TANF,Interested in Work Study,Student received Medicaid,Parents received Medicaid,Student received WIC,Subsidized loans balance,Subsidized loans remaining,Subsidized loans total,Unsubsidized loans balance,Unsubsidized loans remaining,Unsubsidized loans total,Combined loans balance,Combined loans remaining,Combined loans total,Unallocated loans balance,Unallocated loans remaining,Unallocated loans total,Perkins loan balance,Perkins loan amount,Fields to Review,Filename"
        var expectedRow =
            "EFC number,1,0,1,01/02/2016,03/04/2017,2016-2015,first name,middle name,last name,std dob,123-12-1234,parent ed 1,parent ed 2,,,yes,2,no,yes,no,yes,1,2,3,4,,,,,,,,,,,,,,,,,,,,"

        val testmap = hashMapOf(
            CsvHeaders.Fields.EFC_NUMBER to "EFC number",
            CsvHeaders.Fields.IS_EFC_STARRED to "1",
            CsvHeaders.Fields.HAS_EFC_C_SUFFIX to "0",
            CsvHeaders.Fields.HAS_EFC_H_SUFFIX to "1",
            CsvHeaders.Fields.RECEIVED_DATE to "01/02/2016",
            CsvHeaders.Fields.PROCESSED_DATE to "03/04/2017",
            CsvHeaders.Fields.YEAR to "2016-2015",
            CsvHeaders.Fields.STUDENT_FIRST_NAME to "first name",
            CsvHeaders.Fields.STUDENT_MIDDLE_NAME to "middle name",
            CsvHeaders.Fields.STUDENT_LAST_NAME to "last name",
            CsvHeaders.Fields.STUDENT_DOB to "std dob",
            CsvHeaders.Fields.STUDENT_SSN_L4 to "123-12-1234",
            CsvHeaders.Fields.PARENT_1_ED_LEVEL to "parent ed 1",
            CsvHeaders.Fields.PARENT_2_ED_LEVEL to "parent ed 2",
            CsvHeaders.Fields.STUDENT_HAS_CHILDREN to "yes",
            CsvHeaders.Fields.STUDENT_HAS_DEPENDENTS to "2",
            CsvHeaders.Fields.DECEASED_PARENTS_COURT_WARD_FOSTER_CARE to "no",
            CsvHeaders.Fields.EMANCIPATED_MINOR to "yes",
            CsvHeaders.Fields.IN_LEGAL_GUARDIANSHIP to "no",
            CsvHeaders.Fields.UNACCOMPANIED_HOMELESS_YOUTH to "yes",
            CsvHeaders.Fields.PARENTS_RECEIVED_SNAP to "1",
            CsvHeaders.Fields.PARENTS_RECEIVED_TANF to "2",
            CsvHeaders.Fields.STUDENT_RECEIVED_SNAP to "3",
            CsvHeaders.Fields.STUDENT_RECEIVED_TANF to "4"
        )

        val normalizedTestMap = normalizeTestMap(testmap)

        subject.insertRow(normalizedTestMap)
        subject.insertRow(normalizedTestMap)
        subject.insertRow(normalizedTestMap)

        subject.finish()

        var br = BufferedReader(FileReader(testFilename))

        assertEquals(expectedHeader, br.readLine())
        assertEquals(expectedRow, br.readLine())
        assertEquals(expectedRow, br.readLine())
        assertEquals(expectedRow, br.readLine())

        File(testFilename).delete()

        testFilename = "csvwritertest.csv"

        subject = CsvWriter(testFilename, CsvHeaders.DocType.ERROR)

        expectedHeader = "Error,Filename"
        expectedRow = "some error,some filename"

        val errorMap = hashMapOf(
            CsvHeaders.Fields.FILENAME to "some filename",
            CsvHeaders.Fields.ERROR to "some error"
        )

        val normalizedErrorMap = normalizeTestMap(errorMap)

        subject.insertRow(normalizedErrorMap)
        subject.insertRow(normalizedErrorMap)
        subject.insertRow(normalizedErrorMap)

        subject.finish()

        br = BufferedReader(FileReader(testFilename))

        assertEquals(expectedHeader, br.readLine());
        assertEquals(expectedRow, br.readLine())
        assertEquals(expectedRow, br.readLine())
        assertEquals(expectedRow, br.readLine())

        File(testFilename).delete()
    }

    private fun normalizeTestMap(map: Map<CsvHeaders.Fields, String>): Map<String, String> {
        return map.keys.associate { Pair(PdfNormalizer.normalizeField(it.csvFieldName), map[it]!!) }
    }
}