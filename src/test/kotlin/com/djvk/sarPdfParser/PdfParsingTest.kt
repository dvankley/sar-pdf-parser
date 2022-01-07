package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.SarFormat
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class PdfParsingTest {
    val priorTo2021Header1 = """

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

    val priorTo2021Header2 = """

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

    val post2022Header1 = """
                                                                                                                                                                                                                          
      1/1/22,  12:01  AM                                                            2022-2023  Student  Aid Report  Print  | FAFSA  Application | Federal  Student  Aid                                            
                                                                                                                                                                                                                  
         2022–23                               Student                       Aid          Report                                                                                                                  
                                                                                                                                                                                                                  
         TRANSACTION        05                                                                                                                                                                                    
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                Application  Receipt  Date:                                                   Processed   Date:                                                  Data   Release  Number    (DRN)                  
                12/31/2021                                                                    01/01/2022                                                         8888                                             
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
         Processing         Results                                                                                                                                                                               
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                Learn    about    federal    tax  benefits    for  education,      including     the   American      Opportunity        tax  credit.                                                              
                Expected      Family     Contribution:      9999    *                                                                                                                                             
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                This  Student     Aid  Report     (SAR)   reflects   the   parental     data   that  you   have    added     to your    Free   Application      for Federal     Student    Aid                    
                (FAFSA    ®) form.                                                                                                                                                                                
                                                                                                                                                                                                                  
                Based    on  the   information       we   have   on   record    for  you,   your   Expected      Family     Contribution       (EFC)   is 9999.    You   may    be  eligible   to                 
                receive    a Federal     Pell  Grant    and   other   federal    student     aid.  Your   school    will  use   your    EFC   to determine       your    financial    aid                         
                eligibility   for  federal    grants,   loans,   and    work-study       funds,   and    possible    funding     from    your   state   and   school.                                             
                                                                                                                                                                                                                  
                Your   Free   Application      for  Federal    Student    Aid   (FAFSA    ®)  form    has  been    selected    for  a  review    process     called   verification.     Your                      
                school   has   the  authority      to request     copies    of certain    financial     documents       from    you   and   your    parent(s).                                                    
                                                                                                                                                                                                                  
                There    is a limit   to the   total  amount      of Federal     Pell  Grants    that   a student     may    receive,    which    is the   equivalent      of six  school                         
                years.   Based    on  information        reported     to the   National     Student    Loan    Data    System    (NSLDS     ®)  database     by  the   schools    you   have                      
                attended,     you   have   received      Federal    Pell  Grants    for  the   equivalent      of between      one   and    one-half    and    two   school    years.                             
                                                                                                                                                                                                                  
                Based    on  your    EFC   of  9999,   you   may    be  eligible   to  receive    a  Federal    Pell  Grant    of  up  to  ${'$'}7,777    for  the  2022–23     school    year                         
                provided     you   have    not   met   or  exceeded      the  lifetime    limit   established     for   the  Federal    Pell  Grant    program.                                                   
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
         FAFSA       Data                                                                                                                                                                                         
                                                                                                                                                                                                                  
         Your    FAFSA     data   reflects   the  answers      you   provided      on  your    FAFSA     form.    Assumed      values    are   marked      with   an  asterisk    (*).                            
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                What    you   must     do  now:                                                                                                                                                                   
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                Use  the   checklist    below    to make    sure   that   all of  your   issues   are  resolved.                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                       If you   need    to make     corrections      to  your   information,       select   "Make     Correction"      on  the  "My    FAFSA"     page   using    your                            
                       account     username       and   password       (FSA   ID).  If you   need    additional     help   with    your   Student     Aid  Report    (SAR),    contact    your                    
                       school's    financial    aid   office  or  select   the  "Get   FAFSA     help"   link   from    the  FAFSA     home     page.   If your    mailing    address     or                      
                       email    address     changes,     you   can   make    the   correction     online.                                                                                                         
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                   Collapse   All                                                                                                
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                Student     Information                                                                                                                                                                          
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                                                                                                                                                                                                                  
                1. Student's     Last  Name:                                                                   LASTY                                                                                             
      https://studentaid.gov/fafsa-app/CYCLE2223/ESAR/PRINT                                                                                                                                             1/10      
    """.trimIndent()

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
        val (startYear, endYear) = parser.getReportYears(priorTo2021Header1)

        assertThat(startYear).isEqualTo(2016)
        assertThat(endYear).isEqualTo(2017)
    }

    @Test
    fun testGetYear2() {
        val parser = SarPdfParser()
        val (startYear, endYear) = parser.getReportYears(priorTo2021Header2)

        assertThat(startYear).isEqualTo(2018)
        assertThat(endYear).isEqualTo(2019)
    }

    @Test
    fun testGetYear3() {
        val parser = SarPdfParser()
        val (startYear, endYear) = parser.getReportYears(post2022Header1)

        assertThat(startYear).isEqualTo(2022)
        assertThat(endYear).isEqualTo(2023)
    }

    @Test
    fun testGetEfc1() {
        val parser = SarPdfParser()
        val result = parser.getEFCNumber(SarFormat.BEFORE_2021, priorTo2021Header1)

        assertThat(result.number).isEqualTo("123456")
        assertThat(result.isStarred).isEqualTo(false)
        assertThat(result.hasCSuffix).isEqualTo(false)
        assertThat(result.hasHSuffix).isEqualTo(false)
    }

    @Test
    fun testGetEfc2() {
        val parser = SarPdfParser()
        val result = parser.getEFCNumber(SarFormat.BEFORE_2021, priorTo2021Header2)

        assertThat(result.number).isEqualTo("888888")
        assertThat(result.isStarred).isEqualTo(true)
        assertThat(result.hasCSuffix).isEqualTo(false)
        assertThat(result.hasHSuffix).isEqualTo(true)
    }

    @Test
    fun testGetEfc3() {
        val parser = SarPdfParser()
        val result = parser.getEFCNumber(SarFormat.AFTER_2022, post2022Header1)

        assertThat(result.number).isEqualTo("9999")
        assertThat(result.isStarred).isEqualTo(true)
        assertThat(result.hasCSuffix).isEqualTo(false)
        assertThat(result.hasHSuffix).isEqualTo(true)
    }

    @Test
    fun testGetDate1() {
        val parser = SarPdfParser()
        val receipt = parser.getDate(priorTo2021Header1, parser.applicationReceiptPrefix)
        val processed = parser.getDate(priorTo2021Header1, parser.processedPrefix)

        assertThat(receipt).isEqualTo("01/02/2016")
        assertThat(processed).isEqualTo("01/03/2016")
    }

    @Test
    fun testGetDate2() {
        val parser = SarPdfParser()
        val receipt = parser.getDate(priorTo2021Header2, parser.applicationReceiptPrefix)
        val processed = parser.getDate(priorTo2021Header2, parser.processedPrefix)

        assertThat(receipt).isEqualTo("01/02/2018")
        assertThat(processed).isEqualTo("01/03/2018")
    }

    @Test
    fun testGetHeaderData() {
        val parser = SarPdfParser()
        val actual = parser.getHeaderTableDataAfter2022(post2022Header1)

        assertThat(actual.receivedDate).isEqualTo("12/31/2021")
        assertThat(actual.processedDate).isEqualTo("01/01/2022")
        assertThat(actual.DRN).isEqualTo(8888)
    }
}
