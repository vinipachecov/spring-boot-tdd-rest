package com.bookstore.libraryapi.service;

import com.bookstore.libraryapi.model.entity.Loan;

public interface LoanService {
    Loan save(Loan loan);
}
