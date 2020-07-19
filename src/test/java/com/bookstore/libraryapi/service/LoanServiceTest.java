package com.bookstore.libraryapi.service;

import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.entity.Loan;
import com.bookstore.libraryapi.model.repository.LoanRepository;
import com.bookstore.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @Mock
    LoanRepository repository;

    @BeforeEach
    public void setUp() {
        service = new LoanServiceImpl(this.repository);
    }


    @Test
    @DisplayName("shoul save a loan")
    public void saveLoanTest() {
        String customer = "valid-customer";
        Long bookId = 1L;
        Loan savingLoan = Loan.builder().book(Book.builder().id(bookId).build())
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
        Loan savedLoan = Loan.builder().book(Book.builder().id(bookId).build())
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }
}