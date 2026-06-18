package com.example.EduVerse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DocumentUpdateRequest {

    @NotBlank(message = "Tiêu đề tài liệu không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    private String description;

    @NotBlank(message = "Loại cấu hình giá không được để trống")
    private String priceType; // FREE, COIN, VND

    private Integer priceCoin = 0;

    private BigDecimal priceVnd = BigDecimal.ZERO;

    @NotNull(message = "Danh mục tài liệu không được để trống")
    private Long categoryId;
}
