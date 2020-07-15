package com.bookstore.libraryapi.model.repository;

import com.bookstore.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

//    Manage entities in test
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    private Book createValidBook() {
        return Book.builder().isbn("valid-isbn").author("valid-author").title("valid-title").build();
    }

    private Book persistBook(Book book) {
        entityManager.persist(book);
        return book;
    }

    @Test
    @DisplayName("should return true if  book exists with provided isbn")
    public void returnTrueWhenIsbnExists() {
//        cenario
        String isbn = "valid-isbn";
        Book book = createValidBook();
        persistBook(book);
//        execução
        boolean exists = repository.existsByIsbn(isbn);
//        verificão
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should return false if  book exists with provided isbn")
    public void returnFalseWhenIsbnExists() {
//        cenario
        String isbn = "not-persisted-isbn";
//        execução
        boolean exists = repository.existsByIsbn(isbn);
//        verificão
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("should return book by id")
    public void findByIdTest() {
//        cenario
        Book book = createValidBook();
        persistBook(book);
//        execução
        Optional<Book> foundBook = repository.findById(book.getId());
//        verificão
        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("should save a book")
    public void saveBookTest() {
        Book book = createValidBook();
        Book savedBook = repository.save(book);

//        verify
        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("should delete a book")
    public void deleteBookTest() {
        Book book = createValidBook();
        persistBook(book);

        Book bookFound = entityManager.find( Book.class, book.getId());
        repository.delete(bookFound);

        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();

    }
}
