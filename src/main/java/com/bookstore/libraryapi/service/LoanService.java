package com.bookstore.libraryapi.service;

import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(com.bookstore.libraryapi.api.dto.LoanFilterDTO filterDTO, Pageable page );

    Page<Loan> getLoansByBook(Book book, Pageable pageable);
}

