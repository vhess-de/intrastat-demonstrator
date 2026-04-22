package com.ford.intrastat.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "legal_entities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "country_code", columnDefinition = "bpchar(2)", nullable = false)
    private String countryCode;

    @Column(name = "vat_number")
    private String vatNumber;

    @Column(nullable = false)
    private boolean active;
}
