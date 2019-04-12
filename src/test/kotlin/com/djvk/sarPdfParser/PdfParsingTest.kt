package com.djvk.sarPdfParser

import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class PdfParsingTest {
    val testHeader1 = """

      4/26/2016                                       Processed  Information  -  FAFSA on  the  Web -  Federal  Student  Aid

          Processed Information










          2016­2017 Electronic Student Aid Report (SAR)




          The SAR summarizes the information you submitted on your 2016­2017 Free Application for Federal Student Aid (FAFSA).



             Application Receipt Date:       01/02/2016       XXX­XX­1234 CR 05
             Processed Date:                 01/03/2016       EFC: 123456
                                                              DRN: 0123



          Comments About Your Information




          Learn about federal tax benefits for education, including the American Opportunity Tax Credit (AOTC).


            Based on the information we have on record for you, your EFC is 000000. You may be eligible to receive a Federal Pell Grant and
          other federal student aid. Your school will use your EFC to determine your financial aid eligibility for federal grants, loans, and
          work‑study, and possible funding from your state and school. 

            There is a limit to the total amount of Federal Pell Grants that a student may receive, which is the equivalent of 6 school years. Once a
          total amount of Pell Grant eligibility has been received, a student can no longer receive Pell Grant aid.

          WHAT YOU MUST DO NOW (Use the checklist below to make sure that all of your issues are resolved.)

          If you need to make corrections to your information, click 'Make FAFSA Corrections' on the 'My FAFSA' page using your FSA ID. If you
          need additional help with your SAR, contact your school's financial aid office or click the 'Help' icon on the FAFSA home page. If your
          mailing address or e‑mail address changes, you can make the correction online. 


          Based on your EFC of 000000, you may be eligible to receive a Federal Pell Grant of up to ${'$'}5,815 for the 2016­2017 school year
          provided you have not met or exceeded the lifetime limit established for the Federal Pell Grant program.




          FAFSA Data
"""

    val testHeader2 = """

    2018-2019

    The SAR  summarizes the  information  you submitted  on  your  2018-2019  Free  Application for  Federal  Student Aid  (FAFSA).


     Application  Receipt  Date:           01/02/2018                            XXX-XX-1234  KE  01
     Processed Date:                       01/03/2018                            EFC:  888888   *    H
                                                                                 DRN:   0123



    Comments   About Your Information

    Learn about  federal  tax benefits  for  education, including  the  American Opportunity  Tax Credit  (AOTC).

      Based  on  the information  we  have  on  record  for you,  your  EFC  is  000000.  You may be  eligible to  receive  a  Federal  Pell  Grant  and
    Your school  will  use your EFC  to  determine  your  financial  aid eligibility  for federal  grants,  loans,  and  work-study,  and possible fundin


      There  is a  limit to  the  total amount  of  Federal  Pell  Grants  that a student  may  receive, which  is  the equivalent  of  6 school years.
    eligibility  has been received,  a  student  can  no  longer  receive Pell  Grant  aid.

    WHAT  YOU  MUST   DO NOW   (Use  the  checklist  below to  make  sure  that all  of  your  issues are resolved.)

    If  you  need  to  make corrections  to  your  information, click  'Make  FAFSA  Corrections'  on  the 'My  FAFSA'  page  using  your  FSA  ID.   If
    your  SAR,  contact  your school's  financial  aid  office  or  click the  'Help'  icon  on  the  FAFSA  home  page.   If your  mailing  address or
    make the correction  online.

    Based on your  EFC  of 000000,  you may be  eligible  to  receive  a Federal  Pell  Grant  of  up to  ${'$'}5,920  for the  2018-2019  school year provide
    exceeded the lifetime  limit  established  for  the  Federal Pell Grant  program.

















































































                                                                        Page 1

       FAFSA    Data
"""

    @Test
    fun base() {
        val file = File("src/test/resources/testInput/sample-2016.pdf")
        val parser = SarPdfParser()
        var fileContents = runBlocking {
            parser.processFile(file)
        }
        println(fileContents)
        for ((k, v) in fileContents) {
            println(k + ": " + v)
        }
        assertThat(fileContents != null)
    }

    @Test
    fun testGetYear1() {
        val parser = SarPdfParser()
        val year = parser.getYear(testHeader1)

        assertThat(year).isEqualTo("2016-2017")
    }

    @Test
    fun testGetYear2() {
        val parser = SarPdfParser()
        val year = parser.getYear(testHeader2)

        assertThat(year).isEqualTo("2018-2019")
    }

    @Test
    fun testGetEfc1() {
        val parser = SarPdfParser()
        val result = parser.getEFCNumber(testHeader1)

        assertThat(result.number).isEqualTo("123456")
        assertThat(result.isStarred).isEqualTo(false)
        assertThat(result.hasCSuffix).isEqualTo(false)
        assertThat(result.hasHSuffix).isEqualTo(false)
    }

    @Test
    fun testGetEfc2() {
        val parser = SarPdfParser()
        val result = parser.getEFCNumber(testHeader2)

        assertThat(result.number).isEqualTo("888888")
        assertThat(result.isStarred).isEqualTo(true)
        assertThat(result.hasCSuffix).isEqualTo(false)
        assertThat(result.hasHSuffix).isEqualTo(true)
    }

    @Test
    fun testGetDate1() {
        val parser = SarPdfParser()
        val receipt = parser.getDate(testHeader1, parser.applicationReceiptPrefix)
        val processed = parser.getDate(testHeader1, parser.processedPrefix)

        assertThat(receipt).isEqualTo("01/02/2016")
        assertThat(processed).isEqualTo("01/03/2016")
    }

    @Test
    fun testGetDate2() {
        val parser = SarPdfParser()
        val receipt = parser.getDate(testHeader2, parser.applicationReceiptPrefix)
        val processed = parser.getDate(testHeader2, parser.processedPrefix)

        assertThat(receipt).isEqualTo("01/02/2018")
        assertThat(processed).isEqualTo("01/03/2018")
    }
}
