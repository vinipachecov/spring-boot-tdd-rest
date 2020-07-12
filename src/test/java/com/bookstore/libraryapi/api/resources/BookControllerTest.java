package com.bookstore.libraryapi.api.resources;

import com.bookstore.libraryapi.api.dto.BookDto;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    @DisplayName("Should create a book with success")
    public void createBookTest() throws Exception {
        BookDto dto = BookDto.builder().author("valid-author").title("valid-title").isbn("valid-isbn").build();
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
    public void createBookTest_InvalidInputProvided() {

    }
}
