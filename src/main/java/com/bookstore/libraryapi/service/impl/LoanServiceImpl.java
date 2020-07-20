package com.bookstore.libraryapi.service.impl;

import com.bookstore.libraryapi.exception.BusinessException;
import com.bookstore.libraryapi.model.entity.Loan;
import com.bookstore.libraryapi.model.repository.LoanRepository;
import com.bookstore.libraryapi.service.LoanService;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already in use");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }
}
