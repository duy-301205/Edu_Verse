package com.example.EduVerse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DocumentCreateRequest {

    @NotBlank(message = "TITLE_REQUIRED")
    @Size(max = 255, message = "TITLE_INVALID")
    private String title;

    private String description;

    @NotBlank(message = "Tên file không được để trống")
    private String fileName;

    @NotBlank(message = "Loại cấu hình giá không được để trống")
    private String priceType; // FREE, COIN, VND

    private Integer priceCoin = 0;

    private BigDecimal priceVnd = BigDecimal.ZERO;

    @NotNull(message = "Danh mục tài liệu không được để trống")
    private Long categoryId;
}
