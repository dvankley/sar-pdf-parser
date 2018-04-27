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


class CsvWriterTest {
    @Test
    fun base() {

        val subject: CsvWriter = CsvWriter("csvwritertest.csv")
        val csvHeaders = CsvHeaders();

        val expectedHeader = "Student First Name,Student Middle Name,Student Last Name,Student Date of Birth,Social Security Number Last 4 Digits,Parent 1 Educational Level,Parent 2 Educational Level,Child Support Paid,Does student have children they support?,Does student have other non-child/non-spouse dependents?,Parents deceased / student ward of court / in foster care,Emancipated minor?,In legal guardianship?,Unaccompanied homeless youth,Parents received SNAP,Parents received TANF,Student received SNAP,Student received TANF";
        val expectedRow = "first name,middle name,last name,std dob,123-12-1234,parent ed 1,parent ed 2,child support,yes,2,no,yes,no,yes,1,2,3,4"

        val testmap = hashMapOf(
                csvHeaders.H_DSAR_STUDENT_FIRST_NAME to "first name",
                csvHeaders.H_DSAR_STUDENT_MIDDLE_NAME to "middle name",
                csvHeaders.H_DSAR_STUDENT_LAST_NAME to "last name",
                csvHeaders.H_DSAR_STUDENT_DOB to "std dob",
                csvHeaders.H_DSAR_STUDENT_SSN_L4 to "123-12-1234",
                csvHeaders.H_DSAR_PARENT_1_ED_LEVEL to "parent ed 1",
                csvHeaders.H_DSAR_PARENT_2_ED_LEVEL to "parent ed 2",
                csvHeaders.H_DSAR_CHILD_SUPPORT_PAID to "child support",
                csvHeaders.H_DSAR_STUDENT_HAS_CHILDREN to "yes",
                csvHeaders.H_DSAR_STUDENT_HAS_DEPENDENTS to "2",
                csvHeaders.H_DSAR_DECEASED_PARENTS_COURT_WARD_FOSTER_CARE to "no",
                csvHeaders.H_DSAR_EMANCIPATED_MINOR to "yes",
                csvHeaders.H_DSAR_IN_LEGAL_GUARDIANSHIP to "no",
                csvHeaders.H_DSAR_UNACCOMPANIED_HOMELESS_YOUTH to "yes",
                csvHeaders.H_DSAR_PARENTS_RECEIVED_SNAP to "1",
                csvHeaders.H_DSAR_PARENTS_RECEIVED_TANF to "2",
                csvHeaders.H_DSAR_STUDENT_RECEIVED_SNAP to "3",
                csvHeaders.H_DSAR_STUDENT_RECEIVED_TANF to "4"
        )

        subject.insertRow(testmap)
        subject.insertRow(testmap)
        subject.insertRow(testmap)

        subject.finish()

        val br = BufferedReader(FileReader("csvwritertest.csv"))

        assertEquals(expectedHeader,br.readLine());
        assertEquals(expectedRow,br.readLine())
        assertEquals(expectedRow,br.readLine())
        assertEquals(expectedRow,br.readLine())
    }
}