package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MisAlumnosResponseDTO<T> {
    private String message;
    private T data;
}