package com.bookstore.libraryapi.api.resource;

import com.bookstore.libraryapi.api.dto.BookDto;

import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.service.BookService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public BookDto createBook(@RequestBody BookDto bookDto) {
        BookDto returnValue;
        Book entity = modelMapper.map(bookDto, Book.class);
        entity = bookService.save(entity);
        returnValue = modelMapper.map(entity,BookDto.class);
        return returnValue;
    }

}
