package com.bookstore.libraryapi.model.repository;

import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    EntityManager entityMananger;

    @Autowired
    private LoanRepository repository;

    private Book createValidBook() {
        return Book.builder().isbn("valid-isbn").author("valid-author").title("valid-title").build();
    }

    @Test
    @DisplayName("should verify if there is loan for returned book")
    public void existsByBookAndNotReturnedTest() {
//        case

        Book book = createValidBook();
        Loan loan = Loan.builder().book(book).customer("valid-customer").loanDate(LocalDate.now()).build();
        entityMananger.persist(book);
        entityMananger.persist(loan);
//        run
        boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();

    }
}
