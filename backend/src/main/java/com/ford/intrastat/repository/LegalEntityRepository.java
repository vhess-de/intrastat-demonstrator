package com.ford.intrastat.repository;

import com.ford.intrastat.model.LegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalEntityRepository extends JpaRepository<LegalEntity, Long> {

    List<LegalEntity> findByActiveTrueOrderByName();
}
