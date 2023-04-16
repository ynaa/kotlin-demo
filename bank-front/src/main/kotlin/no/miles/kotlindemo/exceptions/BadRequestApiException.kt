package no.miles.kotlindemo.exceptions

import io.ktor.http.*

abstract class BadRequestApiException(errorDescription: String) : RuntimeException(errorDescription)

class BadContentTypeException(contentType: ContentType, supported: Set<String>) : BadRequestApiException(
    "Content type $contentType is not supported, use $supported"
)

class MissingParameterException(name: String) : BadRequestApiException(
    "Required parameter $name is missing"
)

class MissingHeaderException(name: String) : BadRequestApiException(
    "Required header $name is missing"
)