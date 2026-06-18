package com.example.EduVerse.controller;

import com.example.EduVerse.dto.response.ApiResponse;
import com.example.EduVerse.dto.response.CategoryResponse;
import com.example.EduVerse.dto.response.CategoryTreeResponse;
import com.example.EduVerse.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllFlat() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .data(categoryService.getAllFlatCategories())
                .build();
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryTreeResponse>> getCategoryTree() {
        return ApiResponse.<List<CategoryTreeResponse>>builder()
                .data(categoryService.getCategoryTree())
                .build();
    }

    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .data(categoryService.getCategoryById(categoryId))
                .build();
    }
}
