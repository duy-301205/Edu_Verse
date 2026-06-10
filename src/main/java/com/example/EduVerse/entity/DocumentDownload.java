package com.example.EduVerse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "document_downloads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDownload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "downloaded_at", updatable = false)
    private ZonedDateTime downloadedAt;

    @Column(name = "reward_counted", nullable = false)
    @Builder.Default
    private Boolean rewardCounted = false;

    @PrePersist
    protected void onCreate() {
        this.downloadedAt = ZonedDateTime.now();
    }
}
