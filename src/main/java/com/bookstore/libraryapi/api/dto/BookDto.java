package com.bookstore.libraryapi.api.dto;

import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullFields;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String author;

    @NonNull
    private String isbn;
}
