package com.example.EduVerse.controller;

import com.example.EduVerse.dto.response.ApiResponse;
import com.example.EduVerse.dto.response.DocumentDetailResponse;
import com.example.EduVerse.dto.response.DocumentItemResponse;
import com.example.EduVerse.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/{documentId}")
    public ApiResponse<DocumentDetailResponse> getDocumentDetail(@PathVariable Long documentId) {
        return ApiResponse.<DocumentDetailResponse>builder()
                .data(documentService.getDocumentDetail(documentId))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<DocumentItemResponse>> getDocumentsByUserId(@PathVariable Long userId) {
        return ApiResponse.<List<DocumentItemResponse>>builder()
                .data(documentService.getDocumentsByUserId(userId))
                .build();
    }
}
