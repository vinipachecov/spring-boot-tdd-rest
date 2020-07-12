package com.bookstore.libraryapi.api.resource;

import com.bookstore.libraryapi.api.dto.BookDto;

import com.bookstore.libraryapi.api.exception.ApiErrors;
import com.bookstore.libraryapi.exception.BusinessException;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.service.BookService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    BookService bookService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@RequestBody @Validated BookDto bookDto) {
        BookDto returnValue;
        Book entity = modelMapper.map(bookDto, Book.class);
        entity = bookService.save(entity);
        returnValue = modelMapper.map(entity,BookDto.class);
        return returnValue;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(HttpMessageNotReadableException ex) {
        return new ApiErrors(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex.getMessage());
    }
}
