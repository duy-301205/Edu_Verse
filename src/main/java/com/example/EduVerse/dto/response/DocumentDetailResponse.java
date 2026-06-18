package com.example.EduVerse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String previewUrl;
    private Long fileSize;
    private String fileType;
    private String priceType;
    private Integer priceCoin;
    private BigDecimal priceVnd;
    private Integer downloadCount;
    private Integer viewCount;
    private String status;
    private ZonedDateTime createdAt;

    private Long uploaderId;
    private String uploaderName;
    private String uploaderAvatar;

    private Long categoryId;
    private String categoryName;
}
