package com.example.EduVerse.controller;

import com.example.EduVerse.dto.request.DocumentCreateRequest;
import com.example.EduVerse.dto.request.DocumentUpdateRequest;
import com.example.EduVerse.dto.response.ApiResponse;
import com.example.EduVerse.dto.response.DocumentCreateResponse;
import com.example.EduVerse.dto.response.DocumentDetailResponse;
import com.example.EduVerse.dto.response.DocumentItemResponse;
import com.example.EduVerse.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    public ApiResponse<DocumentCreateResponse> createDocument(@Valid @RequestBody DocumentCreateRequest request) {
        return ApiResponse.<DocumentCreateResponse>builder()
                .data(documentService.createDocument(request))
                .build();
    }

    @PutMapping("/update/{documentId}")
    public ApiResponse<DocumentDetailResponse> updateDocument(@PathVariable Long documentId, @Valid @RequestBody DocumentUpdateRequest request) {
        return ApiResponse.<DocumentDetailResponse>builder()
                .data(documentService.updateDocument(documentId, request))
                .build();
    }

    @DeleteMapping("/delete/{documentId}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ApiResponse.<Void>builder()
                .message("Delete document successful")
                .build();
    }
}
