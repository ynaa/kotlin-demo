package no.miles.kotlindemo.exceptions

abstract class NotFoundApiException(errorDescription: String) : RuntimeException(errorDescription)

class ItemNotFoundException(message: String) : NotFoundApiException(
    "$message not found"
)