package com.example.EduVerse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "CATEGORY_NOT_NULL")
    @Size(max = 100, message = "INVALID_REQUEST")
    private String name;

    private String type;

    private Long parentId;
}
