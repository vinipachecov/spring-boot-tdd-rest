package com.bookstore.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    @NotEmpty
    private String isbn;
    @NotEmpty
    private String customer;

    private BookDto book;
    @NotEmpty
    private String email;
}
