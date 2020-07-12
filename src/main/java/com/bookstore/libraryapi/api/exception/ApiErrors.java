package com.bookstore.libraryapi.api.exception;

import lombok.Data;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiErrors {
    private List<String> errors;

    public ApiErrors(String errorMessage) {
        this.errors = new ArrayList<>();
        this.errors.add(errorMessage);
    }

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }
}
