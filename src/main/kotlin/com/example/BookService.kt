package com.example

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toSet

class BookService {

    fun createBook(bookRequest: BookRequest): Boolean {
        val newBook = Book {
            name = bookRequest.name
        }

        val affectedRecordsNumber =
            database.sequenceOf(Books)
                .add(newBook)

        return affectedRecordsNumber == 1
    }

    fun findAllBooks(): Set<Book> =
        database.sequenceOf(Books).toSet()

    fun findBookById(bookId: Long): Book? =
        database.sequenceOf(Books)
            .find { book -> book.id eq bookId }

    fun updateBookById(bookId: Long, bookRequest: BookRequest): Boolean {
        val foundBook = findBookById(bookId)
        foundBook?.name = bookRequest.name

        val affectedRecordsNumber = foundBook?.flushChanges()

        return affectedRecordsNumber == 1
    }

    fun deleteBookById(bookId: Long): Boolean {
        val foundBook = findBookById(bookId)

        val affectedRecordsNumber = foundBook?.delete()

        return affectedRecordsNumber == 1
    }

    private val database = Database.connect(
        url = "jdbc:postgresql://wanqutire1-server.postgres.database.azure.com:5432/wanqutire1-database",
        driver = "org.postgresql.Driver",
        user = "rrkggusubg",
        password = "0054ZE5R120B55EH$"
    )
}
