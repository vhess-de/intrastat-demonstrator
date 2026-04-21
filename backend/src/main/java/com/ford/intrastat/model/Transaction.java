package com.ford.intrastat.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_entity_id", nullable = false)
    @ToString.Exclude
    private LegalEntity legalEntity;

    /**
     * Stored as first day of the reporting month (e.g. 2025-01-01 for January 2025).
     */
    @Column(nullable = false)
    private LocalDate period;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_type", nullable = false, length = 10)
    private FlowType flowType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterpart_country_code", nullable = false)
    @ToString.Exclude
    private EuCountry counterpartCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    @ToString.Exclude
    private Part part;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "net_mass_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal netMassKg;

    @Column(name = "statistical_value_eur", nullable = false, precision = 12, scale = 2)
    private BigDecimal statisticalValueEur;

    @Column(name = "invoice_value_eur", nullable = false, precision = 12, scale = 2)
    private BigDecimal invoiceValueEur;

    /** Nature of transaction code (NOTc): 11 = outright purchase/sale, 21 = return */
    @Column(name = "nature_of_transaction_code", nullable = false)
    private Integer natureOfTransactionCode;

    /** Mode of transport: 1=Sea, 3=Road, 4=Air, 5=Post, 7=Fixed, 9=Own propulsion */
    @Column(name = "mode_of_transport", nullable = false)
    private Integer modeOfTransport;

    /** Incoterms delivery terms (e.g. DAP, FCA, EXW, DDP) */
    @Column(name = "delivery_terms", nullable = false, length = 5)
    private String deliveryTerms;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
