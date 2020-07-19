package com.bookstore.libraryapi.api.resources;

import com.bookstore.libraryapi.api.dto.LoanDto;
import com.bookstore.libraryapi.api.resource.LoanController;
import com.bookstore.libraryapi.exception.BusinessException;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.entity.Loan;
import com.bookstore.libraryapi.service.BookService;
import com.bookstore.libraryapi.service.LoanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    private LoanDto createValidLoanDto() {
        return new LoanDto().builder().isbn("valid-isbn").customer("valid-customer").build();
    }

    @Test
    @DisplayName("should create a loan")
    public void createLoanTest() throws Exception {
//       case
        LoanDto dto = createValidLoanDto();
        String json = new ObjectMapper().writeValueAsString(dto);
        Book book = Book.builder().id(1l).isbn("valid-isbn").build();
        BDDMockito.given(bookService.getByIsbn(dto.getIsbn())).willReturn(Optional.of(book));
        Loan loan = Loan.builder().id(1l).book(book).customer("valid-customer").build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
// run
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string(loan.getId().toString()));
    }

    @Test
    @DisplayName("should return error when providing invalid book")
    public void createLoanWithInvalidBook() throws Exception {
//        scenario
        LoanDto dto = createValidLoanDto();
        String jsonBody = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getByIsbn(dto.getIsbn())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody);
//      run and verify
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("should return error when creating loan of invalid book")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        LoanDto dto = createValidLoanDto();
        String jsonBody = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getByIsbn(dto.getIsbn())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book note found for passed isbn"));
    }

}
