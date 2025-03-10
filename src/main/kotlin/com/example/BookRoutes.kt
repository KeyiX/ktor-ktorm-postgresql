package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureBookRoutes() {
    routing {
        route("/book") {
            val bookService = BookService()
            createBook(bookService)
            getAllBooksRoute(bookService)
            getBookByIdRoute(bookService)
            updateBookByIdRoute(bookService)
            deleteBookByIdRoute(bookService)
        }
        route("/test") {
            testEndpoint()
        }
    }
}

fun Route.testEndpoint() {
    get {
        call.respond(HttpStatusCode.Created)
    }
}

fun Route.createBook(bookService: BookService) {
    post {
        val request = call.receive<BookRequest>()

        val success = bookService.createBook(bookRequest = request)

        if (success)
            call.respond(HttpStatusCode.Created)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Cannot create book"))
    }
}

fun Route.getAllBooksRoute(bookService: BookService) {
    get {
        val books = bookService.findAllBooks()
            .map(Book::toBookResponse)

        call.respond(message = books)
    }
}

fun Route.getBookByIdRoute(bookService: BookService) {
    get("/{id}") {
        val id: Long = call.parameters["id"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))

        bookService.findBookById(id)
            ?.let { foundBook -> foundBook.toBookResponse() }
            ?.let { response -> call.respond(response) }
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Book with id [$id] not found"))
    }
}

fun Route.updateBookByIdRoute(bookService: BookService) {
    patch("/{id}") {
        val id: Long = call.parameters["id"]?.toLongOrNull()
            ?: return@patch call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))

        val request = call.receive<BookRequest>()
        val success = bookService.updateBookById(id, request)

        if (success)
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Cannot update book with id [$id]"))
    }
}

fun Route.deleteBookByIdRoute(bookService: BookService) {
    delete("/{id}") {
        val id: Long = call.parameters["id"]?.toLongOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))

        val success = bookService.deleteBookById(id)

        if (success)
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Cannot delete book with id [$id]"))
    }
}

private fun Book?.toBookResponse(): BookResponse? =
    this?.let { BookResponse(it.id, it.name) }
