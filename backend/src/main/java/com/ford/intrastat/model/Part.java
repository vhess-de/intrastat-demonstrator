package com.ford.intrastat.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "parts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part_code", unique = true, nullable = false)
    private String partCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "cn8_code", columnDefinition = "bpchar(8)", nullable = false)
    private String cn8Code;

    @Column(name = "supplementary_unit", length = 10)
    private String supplementaryUnit;

    @Column(name = "unit_mass_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal unitMassKg;

    @Column(name = "unit_price_eur", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPriceEur;
}
