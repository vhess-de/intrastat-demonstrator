package com.ford.intrastat.repository;

import com.ford.intrastat.model.EuCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EuCountryRepository extends JpaRepository<EuCountry, String> {

    List<EuCountry> findByActiveMemberTrueOrderByName();
}
