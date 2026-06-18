package com.example.EduVerse.service;

import com.example.EduVerse.dto.response.DocumentDetailResponse;
import com.example.EduVerse.dto.response.DocumentItemResponse;
import com.example.EduVerse.entity.Document;
import com.example.EduVerse.exception.AppException;
import com.example.EduVerse.exception.ErrorCode;
import com.example.EduVerse.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Transactional
    public DocumentDetailResponse getDocumentDetail(Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        if(!"APPROVED".equals(doc.getStatus().toString())) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_ACCESSIBLE);
        }

        doc.setViewCount(doc.getViewCount() + 1);
        Document updatedDoc = documentRepository.save(doc);

        return toDocumentDetailResponse(updatedDoc);
    }

    public List<DocumentItemResponse> getDocumentsByUserId(Long userId) {
        List<Document> docs = documentRepository.findByUploaderIdAndStatus(userId, "APPROVED");

        return docs.stream()
                .map(this::toDocumentItemResponse)
                .collect(Collectors.toList());
    }



    private DocumentDetailResponse toDocumentDetailResponse(Document doc) {
        DocumentDetailResponse res = new DocumentDetailResponse();
        res.setId(doc.getId());
        res.setTitle(doc.getTitle());
        res.setDescription(doc.getDescription());
        res.setPreviewUrl(doc.getPreviewUrl());
        res.setFileSize(doc.getFileSize());
        res.setFileType(doc.getFileType());
        res.setPriceType(doc.getPriceType().toString());
        res.setPriceCoin(doc.getPriceCoin());
        res.setPriceVnd(doc.getPriceVnd());
        res.setDownloadCount(doc.getDownloadCount());
        res.setViewCount(doc.getViewCount());
        res.setStatus(doc.getStatus().toString());
        res.setCreatedAt(doc.getCreatedAt());

        if (doc.getUploader() != null) {
            res.setUploaderId(doc.getUploader().getId());
            res.setUploaderName(doc.getUploader().getFullName());
            res.setUploaderAvatar(doc.getUploader().getAvatarUrl());
        }
        if (doc.getCategory() != null) {
            res.setCategoryId(doc.getCategory().getId());
            res.setCategoryName(doc.getCategory().getName());
        }
        return res;
    }



    private DocumentItemResponse toDocumentItemResponse(Document doc) {
        return DocumentItemResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .previewUrl(doc.getPreviewUrl())
                .priceType(doc.getPriceType().toString())
                .priceCoin(doc.getPriceCoin())
                .priceVnd(doc.getPriceVnd())
                .downloadCount(doc.getDownloadCount())
                .viewCount(doc.getViewCount())
                .build();
    }
}
