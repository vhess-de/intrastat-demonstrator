package com.ford.intrastat.repository;

import com.ford.intrastat.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    /**
     * Returns distinct reporting periods as "YYYY-MM" strings, newest first.
     * Used to populate the period selector in the frontend.
     */
    @Query(value = "SELECT DISTINCT TO_CHAR(period, 'YYYY-MM') FROM transactions ORDER BY 1 DESC",
           nativeQuery = true)
    List<String> findDistinctPeriods();
}
