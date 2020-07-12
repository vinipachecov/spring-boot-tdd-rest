package com.bookstore.libraryapi.service;

import com.bookstore.libraryapi.model.entity.Book;
import java.util.Optional;

public interface BookService {
    Book save(Book any);
    Optional<Book> getById(Long id);
    void delete(Book book);
}
