package com.djvk.sarPdfParser


import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class CsvWriterTest {
    @Test
    fun base() {

        val subject: CsvWriter = CsvWriter("csvwritertest.csv")
        val csvHeaders = CsvHeaders();

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

        subject.finish()

        assert(true);
    }
}