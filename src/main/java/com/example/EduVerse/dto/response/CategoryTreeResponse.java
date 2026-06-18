package com.example.EduVerse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeResponse {

    private Long id;
    private String name;
    private String type;
    private Long parentId;
    private List<CategoryTreeResponse> children = new ArrayList<>();
}
