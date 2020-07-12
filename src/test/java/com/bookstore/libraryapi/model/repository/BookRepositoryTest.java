package com.bookstore.libraryapi.model.repository;

import com.bookstore.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @Test
    @DisplayName("should return true if  book exists with provided isbn")
    public void returnTrueWhenIsbnExists() {
//        cenario
        String isbn = "valid-isbn";
        Book book = createValidBook();
        entityManager.persist(book);
//        execução
        boolean exists = repository.existsByIsbn(isbn);
//        verificão
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should return false if  book exists with provided isbn")
    public void returnFalseWhenIsbnExists() {
//        cenario
        String isbn = "not-persisted-isbn";
//        execução
        boolean exists = repository.existsByIsbn(isbn);
//        verificão
        Assertions.assertThat(exists).isFalse();
    }
}
