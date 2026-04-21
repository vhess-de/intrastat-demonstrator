package com.ford.intrastat.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "eu_countries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EuCountry {

    @Id
    @Column(length = 2)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "active_member", nullable = false)
    private boolean activeMember;
}
