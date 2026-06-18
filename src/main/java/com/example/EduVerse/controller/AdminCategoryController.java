package com.example.EduVerse.controller;

import com.example.EduVerse.dto.request.CategoryRequest;
import com.example.EduVerse.dto.response.ApiResponse;
import com.example.EduVerse.dto.response.CategoryResponse;
import com.example.EduVerse.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .data(categoryService.createCategory(request))
                .build();
    }

    @PutMapping("/id")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id,
                                                        @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .data(categoryService.updateCategory(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .message("Delete category successful")
                .build();
    }

}
