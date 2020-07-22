package com.bookstore.libraryapi.model.repository;

import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

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

    @Test
    @DisplayName("should retrieve laon by isbn or customer")
    public void findBookIsbnOrCustomerTest() {
        Book book = createValidBook();
        entityMananger.persist(book);
         Loan loan = Loan.builder().book(book).customer("valid-customer").loanDate(LocalDate.now()).build();
         entityMananger.persist(loan);

         Page<Loan> result = repository.findByBookIsbnOrCustomer(book.getIsbn(), loan.getCustomer(), PageRequest.of(0, 10));

         assertThat(result.getContent().size()).isEqualTo(1);
         assertThat(result.getPageable().getPageSize()).isEqualTo(10);
         assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("should retrieve loans where loans data is three days late and not returned")
    public void findByLoanDateThreeDaysLateTest() {
        Book book = createValidBook();
        entityMananger.persist(book);
        Loan loan = Loan.builder().book(book).customer("valid-customer").loanDate(LocalDate.now()).build();
        loan.setLoanDate(LocalDate.now().minusDays(5));
        entityMananger.persist(loan);

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("should return empty array of loans when non late ")
    public void notFindByLoanDateThreeDaysLateTest() {
        Book book = createValidBook();
        entityMananger.persist(book);
        Loan loan = Loan.builder().book(book).customer("valid-customer").loanDate(LocalDate.now()).build();
        entityMananger.persist(loan);

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();
    }
}
