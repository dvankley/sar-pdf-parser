package com.djvk.sarPdfParser.exceptions

class FileProcessingException(fileName: String, exception: Exception) :
    Exception("Error processing file $fileName", exception)