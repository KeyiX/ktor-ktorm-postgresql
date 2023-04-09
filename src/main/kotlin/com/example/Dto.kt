package com.example

import kotlinx.serialization.Serializable

@Serializable
data class BookRequest(val name: String)

@Serializable
data class BookResponse(val id: Long, val name: String)

@Serializable
data class ErrorResponse(val message: String)