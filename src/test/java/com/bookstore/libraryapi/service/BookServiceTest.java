package com.bookstore.libraryapi.service;

import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.repository.BookRepository;
import com.bookstore.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a book")
    public void saveBookTest() {
        Book book = Book.builder().id(1l).isbn("valid-isbn").author("valid-author").title("valid-title").build();
        when(repository.save(book)).thenReturn(book);
        Book savedBook = service.save(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
        Assertions.assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
    }

}
