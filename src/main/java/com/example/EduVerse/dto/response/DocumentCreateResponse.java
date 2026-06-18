package com.example.EduVerse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentCreateResponse {

    private Long documentId;
    private String uploadUrl;
    private String fileUrl;
}
