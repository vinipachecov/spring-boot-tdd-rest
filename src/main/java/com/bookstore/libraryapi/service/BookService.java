package com.bookstore.libraryapi.service;

import com.bookstore.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface BookService {
    Book save(Book any);
    Optional<Book> getById(Long id);
    Optional<Book> getByIsbn(String isbn);
    void delete(Book book);
    Book update(Book book);
    Page<Book> find(Book filter, Pageable pageRequest);
}
