package com.example.EduVerse.entity;

import com.example.EduVerse.enums.TxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "document_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "price_vnd", nullable = false)
    @Builder.Default
    private BigDecimal priceVnd = BigDecimal.ZERO;

    @Column(name = "price_coin", nullable = false)
    @Builder.Default
    private Integer priceCoin = 0;

    @Column(name = "platform_fee_vnd", nullable = false)
    @Builder.Default
    private BigDecimal platformFeeVnd = BigDecimal.ZERO;

    @Column(name = "platform_fee_coin", nullable = false)
    @Builder.Default
    private Integer platformFeeCoin = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TxStatus status = TxStatus.SUCCESS;

    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }
}
