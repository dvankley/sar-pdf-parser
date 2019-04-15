package com.djvk.sarPdfParser

import java.lang.ProcessBuilder.Redirect

class ImageToPdfConverter (private val directory: String) {
    fun convertFiles() {
        //Runtime.getRuntime().exec("docker build -t sarPdfParser . && docker run -v $directory sarPdfParser /images")

        val pb = ProcessBuilder("bash", "-c", "docker build -t sar_pdf_parser . ; docker run -v $directory:/images sar_pdf_parser")
        pb.redirectOutput(Redirect.INHERIT)
        pb.redirectError(Redirect.INHERIT)
        pb.start()
    }
}