package com.bookstore.libraryapi.api.resource;

import com.bookstore.libraryapi.api.dto.BookDto;

import com.bookstore.libraryapi.api.dto.LoanDto;
import com.bookstore.libraryapi.api.exception.ApiErrors;
import com.bookstore.libraryapi.exception.BusinessException;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.entity.Loan;
import com.bookstore.libraryapi.service.BookService;

import com.bookstore.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@Api("Book API")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private  ModelMapper modelMapper;

    @Autowired
    private LoanService loanService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a new book")
    public BookDto createBook(@RequestBody @Validated BookDto bookDto) {
        BookDto returnValue;
        Book entity = modelMapper.map(bookDto, Book.class);
        entity = bookService.save(entity);
        returnValue = modelMapper.map(entity,BookDto.class);
        return returnValue;
    }

    @GetMapping("{id}")
    @ApiOperation("Retrieves a book by book ID.")
    public BookDto getBook(@PathVariable Long id) {
        return bookService.getById(id).map( book -> modelMapper.map(book, BookDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete a book by book ID.")
    @ApiResponses(
            @ApiResponse(code = 204, message = "Book successfuly deleted")
    )
    public void deleteBook(@PathVariable Long id) {
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @PutMapping("{id}")
    @ApiOperation("Update a book by book ID.")
    public BookDto updateBook(@PathVariable Long id,  BookDto dto) {
       return bookService.getById(id).map(book -> {
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = bookService.update(book);
            return modelMapper.map(book, BookDto.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Find books by params")
    public Page<BookDto> find(BookDto dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        return bookService.find(filter, pageRequest).map(entity -> modelMapper.map(entity, BookDto.class));
    }

    @GetMapping("{id}/loans")
    @ApiOperation("Find Loans by a book id.")
    public Page<LoanDto> loansByBook(@PathVariable Long id, Pageable pageable) {
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDto> list = result.getContent()
                .stream()
                .map( loan -> {
                    Book loanBook = loan.getBook();
                    BookDto bookDTO = modelMapper.map(loanBook, BookDto.class);
                    LoanDto loanDTO = modelMapper.map(loan, LoanDto.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, result.getTotalElements());

    }
}
