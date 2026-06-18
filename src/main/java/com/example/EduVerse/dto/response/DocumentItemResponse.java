package com.example.EduVerse.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DocumentItemResponse {

    private Long id;
    private String title;
    private String previewUrl;
    private String priceType;
    private Integer priceCoin;
    private BigDecimal priceVnd;
    private Integer downloadCount;
    private Integer viewCount;
}
