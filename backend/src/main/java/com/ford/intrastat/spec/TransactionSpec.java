package com.ford.intrastat.spec;

import com.ford.intrastat.model.FlowType;
import com.ford.intrastat.model.Transaction;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class TransactionSpec {

    private TransactionSpec() {}

    public static Specification<Transaction> withFilters(
            String period,
            String flowType,
            Long legalEntityId,
            String counterpartCountry) {

        return (root, query, cb) -> {

            // Eagerly fetch associations only for the data query, not the count query
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("legalEntity", JoinType.INNER);
                root.fetch("counterpartCountry", JoinType.INNER);
                root.fetch("part", JoinType.INNER);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (period != null && !period.isBlank()) {
                try {
                    LocalDate start = YearMonth.parse(period).atDay(1);
                    predicates.add(cb.equal(root.get("period"), start));
                } catch (DateTimeParseException ignored) {
                    // invalid period format — no filter applied
                }
            }

            if (flowType != null && !flowType.isBlank()) {
                try {
                    predicates.add(cb.equal(root.get("flowType"), FlowType.valueOf(flowType.toUpperCase())));
                } catch (IllegalArgumentException ignored) {
                    // unknown flow type — no filter applied
                }
            }

            if (legalEntityId != null) {
                predicates.add(cb.equal(root.get("legalEntity").get("id"), legalEntityId));
            }

            if (counterpartCountry != null && !counterpartCountry.isBlank()) {
                predicates.add(cb.equal(root.get("counterpartCountry").get("code"),
                        counterpartCountry.toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
