package com.bookstore.libraryapi.api.resources;

import com.bookstore.libraryapi.api.dto.BookDto;
import com.bookstore.libraryapi.exception.BusinessException;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
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

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    private BookDto createBook() {
        return BookDto.builder().author("valid-author").title("valid-title").isbn("valid-isbn").build();
    }

    @Test
    @DisplayName("Should create a book with success")
    public void createBookTest() throws Exception {
        BookDto dto = createBook();
        Book book = Book.builder().id(10l).author("valid-author").title("valid-title").isbn("valid-isbn").build();

        // mocking the return value of booksService
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(book);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(book.getTitle()))
                        .andExpect(jsonPath("author").value(book.getAuthor()))
                                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }


    @Test
    @DisplayName("should throw validation error when invalid input provided")
    public void createBookTest_InvalidInputProvided() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDto());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                    .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)));
    }

    @Test
    @DisplayName("should throw error when trying to create book with isbn already in use")
    public void createBookWithDuplicatedIsbn() throws Exception {
        BookDto bookDto = createBook();
        String json = new ObjectMapper().writeValueAsString(bookDto);
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException("isbn já cadastrado"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("isbn já cadastrado"));
    }
}
