package com.example.EduVerse.repository;

import com.example.EduVerse.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUploaderIdAndStatus(Long uploaderId, Object status);
}
