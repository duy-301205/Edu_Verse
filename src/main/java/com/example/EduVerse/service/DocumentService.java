package com.example.EduVerse.service;

import com.example.EduVerse.dto.request.DocumentCreateRequest;
import com.example.EduVerse.dto.request.DocumentUpdateRequest;
import com.example.EduVerse.dto.response.DocumentCreateResponse;
import com.example.EduVerse.dto.response.DocumentDetailResponse;
import com.example.EduVerse.dto.response.DocumentItemResponse;
import com.example.EduVerse.entity.Document;
import com.example.EduVerse.entity.User;
import com.example.EduVerse.enums.DocumentPriceType;
import com.example.EduVerse.enums.DocumentStatus;
import com.example.EduVerse.exception.AppException;
import com.example.EduVerse.exception.ErrorCode;
import com.example.EduVerse.repository.CategoryRepository;
import com.example.EduVerse.repository.DocumentRepository;
import com.example.EduVerse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;

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

    @Transactional
    public DocumentCreateResponse createDocument(DocumentCreateRequest request) {
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        var uploader = getCurrentUser();

        DocumentPriceType priceType = DocumentPriceType.valueOf(request.getPriceType().toUpperCase());
        if(priceType == DocumentPriceType.FREE) {
            request.setPriceCoin(0);
            request.setPriceVnd(BigDecimal.ZERO);
        } else if (priceType == DocumentPriceType.COIN && request.getPriceCoin() <= 0) {
            throw new AppException(ErrorCode.INVALID_COIN_PRICE);
        } else if (priceType == DocumentPriceType.VND && request.getPriceVnd().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_VND_PRICE);
        }

        String fileExtension = request.getFileName().contains(".")
                ? request.getFileName().substring(request.getFileName().lastIndexOf("."))
                : ".pdf";
        String s3Key = "documents/" + UUID.randomUUID() + fileExtension;

        String uploadUrl = s3StorageService.generatePresignedUploadUrl(s3Key, "application/pdf");
        String finalFileUrl = "http://localhost:9000/eduverse-bucket/" + s3Key;

        Document doc = new Document();
        doc.setTitle(request.getTitle());
        doc.setDescription(request.getDescription());
        doc.setFileUrl(finalFileUrl);
        doc.setPriceType(priceType);
        doc.setPriceCoin(priceType == DocumentPriceType.COIN ? request.getPriceCoin() : 0);
        doc.setPriceVnd(priceType == DocumentPriceType.VND ? request.getPriceVnd() : BigDecimal.ZERO);
        doc.setUploader(uploader);
        doc.setCategory(category);
        doc.setStatus(DocumentStatus.PENDING);

        Document savedDoc = documentRepository.save(doc);

        return DocumentCreateResponse.builder()
                .documentId(savedDoc.getId())
                .uploadUrl(uploadUrl)
                .fileUrl(savedDoc.getFileUrl())
                .build();
    }

    @Transactional
    public DocumentDetailResponse updateDocument(Long documentId, DocumentUpdateRequest request) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        if(!doc.getId().equals(getCurrentUser().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        doc.setTitle(request.getTitle());
        doc.setDescription(request.getDescription());
        doc.setCategory(category);
        doc.setUpdatedAt(ZonedDateTime.now());

        DocumentPriceType newPriceType = DocumentPriceType.valueOf(request.getPriceType().toUpperCase());
        doc.setPriceType(newPriceType);
        doc.setPriceCoin(newPriceType == DocumentPriceType.COIN ? request.getPriceCoin() : 0);
        doc.setPriceVnd(newPriceType == DocumentPriceType.VND ? request.getPriceVnd() : BigDecimal.ZERO);

        return toDocumentDetailResponse(documentRepository.save(doc));
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (!doc.getUploader().getId().equals(getCurrentUser().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        doc.setStatus(DocumentStatus.HIDDEN);
        doc.setUpdatedAt(ZonedDateTime.now());
        documentRepository.save(doc);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return user;
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
