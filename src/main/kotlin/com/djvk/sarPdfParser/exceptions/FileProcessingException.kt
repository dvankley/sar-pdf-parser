package com.djvk.sarPdfParser.exceptions

class FileProcessingException(private val fileName: String, exception: Exception): Exception(exception.message, exception.cause) {
    override fun getLocalizedMessage(): String {
        return "Filename: $fileName ${super.getLocalizedMessage()}"
    }
}