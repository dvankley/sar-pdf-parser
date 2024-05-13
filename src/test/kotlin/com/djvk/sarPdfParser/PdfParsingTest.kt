package com.djvk.sarPdfParser

import com.djvk.sarPdfParser.constants.CsvHeaders
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class PdfParsingTest {
    val header1 = """
                                                                                                                                                                                                                          
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
        val files = getInputFiles()
        val parser = SarPdfParser()

        // Let's do this processing synchronously for now to make debugging easier
        val outputs = files.map { runBlocking { parser.processText(it) } }

        for (output in outputs) {
            assertThat(output.isNotEmpty())
            runTestsOnFile(output)
        }
    }

    private fun runTestsOnFile(contents: Map<CsvHeaders.Fields, String>) {
        val assertions = mapOf(
            CsvHeaders.Fields.YEAR to 2024,
        )

        for ((key, value) in assertions) {
            val actual = contents[key]
                ?: throw AssertionError("Missing expected field $key")
            assertThat(actual)
                .isEqualTo(value)
                .withFailMessage("Got unexpected value for $key")
        }
    }

    private fun getInputFiles(): List<String> {
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
            .map { it.readText() }
    }
}
