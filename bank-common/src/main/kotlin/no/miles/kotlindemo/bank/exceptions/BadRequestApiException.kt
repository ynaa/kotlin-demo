package no.miles.kotlindemo.bank.exceptions

class BadRequestApiException(errorDescription: String) : RuntimeException(errorDescription)

