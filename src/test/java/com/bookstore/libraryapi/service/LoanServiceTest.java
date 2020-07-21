package com.bookstore.libraryapi.service;

import com.bookstore.libraryapi.exception.BusinessException;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.entity.Loan;
import com.bookstore.libraryapi.model.repository.LoanRepository;
import com.bookstore.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

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
        Loan savingLoan = createValidLoan();
        Loan savedLoan = createValidLoan();

        when (repository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

  public static Loan createValidLoan() {
        String customer = "valid-customer";
        Long bookId = 1L;
        return Loan.builder().book(Book.builder().author("valid-author").isbn("valid-isbn").title("valid-title").id(bookId).build())
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("should return business error when saving loan with book already in use")
    public void saveLoanWithBookAlreadyInUseTest() {

        Loan savingLoan = createValidLoan();

        when (repository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(true);
        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already in use");

        verify(repository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("should get information of a loan by id")
    public void getLoanDetailsTest() {
//        case
        Long id = 1L;
        Loan loan = createValidLoan();

        loan.setId(1L);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("should update a valid loan")
    public void updateLoanTest() {
//        case
        Loan loan = createValidLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }
}
