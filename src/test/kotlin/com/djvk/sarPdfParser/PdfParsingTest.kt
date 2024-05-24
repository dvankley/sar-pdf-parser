package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.CsvHeaders
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.ComparisonFailure
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
                    Pair(name, parser.processText(content))
                } catch (e: Exception) {
                    throw Exception("Failed processing file $name", e)
                }
            }
        }

        for ((name, content) in outputs) {
            assertThat(content.isNotEmpty())
            runTestsOnFile(name, content)
        }
    }

    private fun runTestsOnFile(filename: String, contents: Map<CsvHeaders.Fields, String>) {
        val assertions = mapOf(
            CsvHeaders.Fields.YEAR to "2024",
            CsvHeaders.Fields.RECEIVED_DATE to "Jan. 1, 2024",
            CsvHeaders.Fields.PROCESSED_DATE to "March 31, 2024",
            CsvHeaders.Fields.SAI to "-1500",
            CsvHeaders.Fields.STUDENT_FIRST_NAME to "First",
            CsvHeaders.Fields.STUDENT_MIDDLE_NAME to "A",
            CsvHeaders.Fields.STUDENT_LAST_NAME to "Last",
            CsvHeaders.Fields.STUDENT_DOB to "01/01/2000",
            CsvHeaders.Fields.STUDENT_SSN_L4 to "•••-••-1234",
            CsvHeaders.Fields.STUDENT_EMAIL to "firstlast@gmail.com",
            CsvHeaders.Fields.STUDENT_HAS_DEPENDENTS to "No",
            CsvHeaders.Fields.ORPHAN_STATE_CUSTODY_OR_EMANCIPATED to "No",
            CsvHeaders.Fields.LEGAL_GUARDIAN_OTHER_THAN_PARENT to "No",
            CsvHeaders.Fields.HOMELESS to "No",
            CsvHeaders.Fields.CANT_PROVIDE_PARENT_INFORMATION to "No",
            CsvHeaders.Fields.APPLYING_FOR_UNSUBSIDIZED_ONLY to "No",
            CsvHeaders.Fields.PARENT_ATTENDED_COLLEGE to "One or both parents completed college",
            CsvHeaders.Fields.PARENT_MARITAL_STATUS to "Married (not separated)",
            CsvHeaders.Fields.FAMILY_RECEIVED_EIC to "No",
            CsvHeaders.Fields.FAMILY_RECEIVED_HOUSING_ASSISTANCE to "No",
            CsvHeaders.Fields.FAMILY_RECEIVED_FREE_LUNCH to "No",
            CsvHeaders.Fields.FAMILY_RECEIVED_MEDICAID to "Yes",
            CsvHeaders.Fields.FAMILY_RECEIVED_HEALTH_PLAN_CREDIT to "No",
            CsvHeaders.Fields.FAMILY_RECEIVED_SNAP to "No",
            CsvHeaders.Fields.FAMILY_RECEIVED_SSI to "No",
            CsvHeaders.Fields.FAMILY_RECEIVED_TANF to "No",
            CsvHeaders.Fields.FAMILY_RECEIVED_WIC to "Yes",
            CsvHeaders.Fields.FAMILY_SIZE to "4",
            CsvHeaders.Fields.FAMILY_MEMBERS_IN_COLLEGE to "1",
            CsvHeaders.Fields.COLLEGE_1 to "GEORGIA STATE UNIVERSITY",
            CsvHeaders.Fields.COLLEGE_2 to "KENNESAW STATE UNIVERSITY",
            CsvHeaders.Fields.COLLEGE_3 to "GEORGIA INSTITUTE OF TECHNOLOGY",
            CsvHeaders.Fields.COLLEGE_4 to "UNIVERSITY OF GEORGIA",
            CsvHeaders.Fields.COLLEGE_5 to "",
            CsvHeaders.Fields.COLLEGE_6 to "",
            CsvHeaders.Fields.COLLEGE_7 to "",
            CsvHeaders.Fields.COLLEGE_8 to "",
            CsvHeaders.Fields.COLLEGE_9 to "",
            CsvHeaders.Fields.COLLEGE_10 to "",
            CsvHeaders.Fields.COLLEGE_11 to "",
            CsvHeaders.Fields.COLLEGE_12 to "",
            CsvHeaders.Fields.COLLEGE_13 to "",
            CsvHeaders.Fields.COLLEGE_14 to "",
            CsvHeaders.Fields.COLLEGE_15 to "",
            CsvHeaders.Fields.COLLEGE_16 to "",
            CsvHeaders.Fields.COLLEGE_17 to "",
            CsvHeaders.Fields.COLLEGE_18 to "",
            CsvHeaders.Fields.COLLEGE_19 to "",
            CsvHeaders.Fields.COLLEGE_20 to "",
            CsvHeaders.Fields.FIELDS_TO_REVIEW to "",
        )

        assertThat(contents.keys).doesNotContain(CsvHeaders.Fields.ERROR)

        for ((key, value) in assertions) {
            val actual = contents[key]
                ?: throw AssertionError("Missing expected field $key in file $filename")
            try {
                assertThat(actual).isEqualTo(value)
            } catch (e: ComparisonFailure) {
                throw AssertionError("Got unexpected value for $key in file $filename", e)
            }
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
//            .filter { it.name == "variant2.txt" }
            .map { Pair(it.name, it.readText()) }
    }
}
