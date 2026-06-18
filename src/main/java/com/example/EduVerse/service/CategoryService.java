package com.example.EduVerse.service;

import com.example.EduVerse.dto.request.CategoryRequest;
import com.example.EduVerse.dto.response.CategoryResponse;
import com.example.EduVerse.dto.response.CategoryTreeResponse;
import com.example.EduVerse.entity.Category;
import com.example.EduVerse.exception.AppException;
import com.example.EduVerse.exception.ErrorCode;
import com.example.EduVerse.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllFlatCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryTreeResponse> getCategoryTree() {
        List<Category> allCategories = categoryRepository.findAll();

        List<CategoryTreeResponse> dtoList = allCategories.stream()
                .map(this::toCategoryTreeResponse)
                .collect(Collectors.toList());

        Map<Long, List<CategoryTreeResponse>> childrenMap = dtoList.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(CategoryTreeResponse::getParentId));

        List<CategoryTreeResponse> rootCategories = dtoList.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());

        dtoList.forEach(node -> {
            List<CategoryTreeResponse> children = childrenMap.get(node.getId());
            if(children != null) {
                node.setChildren(children);
            }
        });

        return rootCategories;
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());

        if(request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            category.setParent(parent);
        }

        return toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if(id.equals(request.getParentId())) {
            throw new AppException(ErrorCode.INVALID_PARENT_CATEGORY);
        }

        category.setName(request.getName());
        category.setType(request.getType());

        if(request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if(categoryRepository.existsByParentId(id)) {
            throw new AppException(ErrorCode.CATEGORY_HAS_CHILDREN);
        }

        // 2. Nghiệp vụ: Check nếu có tài liệu (Document) nằm trong danh mục này thì chặn lại
        // boolean hasDocuments = documentRepository.existsByCategoryId(id);
        // if (hasDocuments) { throw new AppException(ErrorCode.CATEGORY_HAS_DOCUMENTS); }

        categoryRepository.delete(category);
    }


    private CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .createdAt(category.getCreatedAt())
                .build();
    }

    private CategoryTreeResponse toCategoryTreeResponse(Category category) {
        return CategoryTreeResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }


}
