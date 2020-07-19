package com.bookstore.libraryapi.service;

import com.bookstore.libraryapi.exception.BusinessException;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.repository.BookRepository;
import com.bookstore.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        Book book = createValidBook();
        when(repository.existsByIsbn(anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(book);
        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
    }

    private Book createValidBook() {
        return Book.builder().id(1l).isbn("valid-isbn").author("valid-author").title("valid-title").build();
    }

    @Test
    @DisplayName("should throw business error when saving a book with duplicated isbn")
    public void shouldNotSaveBookWithDuplicatedIsbn() {
        Book book = createValidBook();

        when(repository.existsByIsbn(anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("should get a book by id")
    public void getBookByIdTest() {
//        cenario
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
//        execução
        Optional<Book> foundbook = service.getById(id);
//        verificação
        assertThat(foundbook.isPresent()).isTrue();
        assertThat(foundbook.get().getId()).isEqualTo(book.getId());
        assertThat(foundbook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundbook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundbook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("should empty if get book by id does not found a book")
    public void bookNotFoundByIdTest() {
//        cenario
        Long id = 1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
//        execução
        Optional<Book> book = service.getById(id);
//        verificação
        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("should delete book if valid id is provided ")
    public void deleteBookTest() {
//        cenario
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
//        execução
        service.delete(book);
//        verificação
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("should throw error when providing invalid book id")
    public void deleteBookInvalidId() {
//        cenario
        Book book = new Book();
        book.setId(null);
        //        run
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete((book)));
//      verify
        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("should update a book if valid id is provided")
    public void updateBookById() {
//        cenario
        Book updatingBook = createValidBook();

        Book updatedBook = createValidBook();
        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);
//        run
        Book resultBook = service.update(updatingBook);
//      verify
        assertThat(resultBook.getId()).isEqualTo(updatedBook.getId());
        assertThat(resultBook.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(resultBook.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(resultBook.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("update book should throw error when providing invalid book id")
    public void updateBookInvalidId() {
//        cenario
        Book book = new Book();
        book.setId(null);
        //        run
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update((book)));
//      verify
        verify(repository, never()).save(book);
    }


    @Test
    @DisplayName("Should filter names by book properties")
    public void findBookTest() {
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(list,pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
                .thenReturn(page);
//      run
        Page<Book> result = service.find(book, pageRequest);

//      verify
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(pageRequest.getPageNumber());
        assertThat(result.getPageable().getPageSize()).isEqualTo(pageRequest.getPageSize());

    }

    @Test
    @DisplayName("Should return a book by isbn")
    public void getBookByIsbnTest() {
        String isbn = createValidBook().getIsbn();

        when(repository.findByIsbn(anyString())).thenReturn(Optional.of(createValidBook()));

        Optional<Book> book = service.getByIsbn(isbn);
        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(createValidBook().getId());
        assertThat(book.get().getIsbn()).isEqualTo(createValidBook().getIsbn());

        verify(repository, times(1)).findByIsbn(isbn);
    }

}

