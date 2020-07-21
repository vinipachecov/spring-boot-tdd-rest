package com.bookstore.libraryapi.api.resource;

import com.bookstore.libraryapi.api.dto.BookDto;
import com.bookstore.libraryapi.api.dto.LoanDto;
import com.bookstore.libraryapi.api.dto.LoanFilterDTO;
import com.bookstore.libraryapi.api.dto.ReturnedLoanDTO;
import com.bookstore.libraryapi.model.entity.Book;
import com.bookstore.libraryapi.model.entity.Loan;
import com.bookstore.libraryapi.service.BookService;
import com.bookstore.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto dto) {
        Book book = bookService.getByIsbn(dto.getIsbn()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Book not found for passed isbn"));
                Loan entity = Loan.builder()
                        .book(book)
                        .customer(dto.getCustomer())
                        .loanDate(LocalDate.now())
                        .build();
        entity = loanService.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto ) {
        Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));
        loan.setReturned(dto.getReturned());
        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDto> findLoan(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result =  loanService.find(dto, pageRequest);
        List<LoanDto> loans = result.getContent().stream().map(
                entity ->{
                    Book book = entity.getBook();
                    BookDto bookdto = modelMapper.map(book, BookDto.class);
                    LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
                    loanDto.setBook(bookdto);
                    return loanDto;
                }
        ).collect(Collectors.toList());
        return new PageImpl<LoanDto>(loans, pageRequest, result.getTotalElements());

    }


}
