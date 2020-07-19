package com.bookstore.libraryapi.api.resources;

import com.bookstore.libraryapi.api.dto.BookDto;
import com.bookstore.libraryapi.api.resource.BookController;
import com.bookstore.libraryapi.exception.BusinessException;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.swing.text.html.Option;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    private BookDto createBookDto() {
        return BookDto.builder().author("valid-author").title("valid-title").isbn("valid-isbn").build();
    }

    @Test
    @DisplayName("should return book information")
    public void getBookDetails() throws Exception {
//        cenario
        Long id = 1l;
        Book book = new ModelMapper().map(createBookDto(), Book.class);
        book.setId(1l);
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));
//        execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
//        verificação
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(book.getId()))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()));
    }

    @Test
    @DisplayName("GET/ should not return not found when book id provided not exits")
    public void bookNotFoundTest() throws Exception {
//        execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
//        verificação
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create a book with success")
    public void createBookTest() throws Exception {
        BookDto dto = createBookDto();
        Book book = Book.builder().id(10l).author("valid-author").title("valid-title").isbn("valid-isbn").build();

        // mocking the return value of booksService
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(book);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request =    MockMvcRequestBuilders.post(BOOK_API)
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
        BookDto bookDto = createBookDto();
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

//    Delete tests

    @Test
    @DisplayName("should delete a book")
    public void deleteBookTest() throws Exception {
//        cenario
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

//        execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat(("/" + 1)));

//        verificação
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should throw NotFound exception when book is not found to delete")
    public void deleteBookNotFoundTest() throws Exception {
//        cenario
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

//        execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat(("/" + 1)));

//        verificação
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should update book when provided id is valid")
    public void updateBookTest() throws Exception {
        //        cenario
        long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createBookDto());
        Book updatingBook = Book.builder().id(1l).title("another-valid-title").author("another-valid-author").isbn("valid-isbn").build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(updatingBook));
        Book updatedBook = Book.builder().id(id).title("updated-valid-title").author("updated-valid-author").isbn("valid-isbn").build();
        BDDMockito.given(bookService.update(updatingBook)).willReturn(updatedBook);

//        execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat(("/" + id))).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

//        verificação
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(updatedBook.getTitle()))
                .andExpect(jsonPath("author").value(updatedBook.getAuthor()))
                .andExpect(jsonPath("isbn").value(updatedBook.getIsbn()));
    }

    @Test
    @DisplayName("should throw NotFound exception when book is not found to delete")
    public void updateNotFoundBookTest() throws Exception {
//        cenario
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

//        execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat(("/" + 1)));

//        verificação
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should filter books")
    public void findBookTest() throws Exception {
        Long id = 1l;
        Book book = Book.builder().id(id).title(createBookDto().getTitle()).author(createBookDto().getAuthor())
                .isbn(createBookDto().getIsbn()).build();

        BDDMockito.given( bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value((100)))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
}
