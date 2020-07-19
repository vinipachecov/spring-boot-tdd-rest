package com.bookstore.libraryapi.api.exception;

import com.bookstore.libraryapi.exception.BusinessException;
import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class ApiErrors {
    private List<String> errors;

    public ApiErrors(String errorMessage) {
        this.errors = new ArrayList<>();
        this.errors.add(errorMessage);
    }

    public ApiErrors(BusinessException ex ) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public ApiErrors(ResponseStatusException ex ) {
        this.errors = Arrays.asList(ex.getReason());
    }

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

}
