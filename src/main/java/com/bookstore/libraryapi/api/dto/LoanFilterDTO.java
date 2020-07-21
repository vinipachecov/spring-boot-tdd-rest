package com.bookstore.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoanFilterDTO {
    private String isbn;
    private String customer;

}
