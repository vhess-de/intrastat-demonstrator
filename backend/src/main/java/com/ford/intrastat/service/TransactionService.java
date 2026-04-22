package com.ford.intrastat.service;

import com.ford.intrastat.dto.PagedResponse;
import com.ford.intrastat.dto.TransactionDto;
import com.ford.intrastat.dto.TransactionPatchRequest;
import com.ford.intrastat.exception.BusinessValidationException;
import com.ford.intrastat.exception.ResourceNotFoundException;
import com.ford.intrastat.model.EuCountry;
import com.ford.intrastat.model.Transaction;
import com.ford.intrastat.repository.EuCountryRepository;
import com.ford.intrastat.repository.TransactionRepository;
import com.ford.intrastat.spec.TransactionSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private static final Set<String> VALID_DELIVERY_TERMS =
            Set.of("EXW", "FCA", "CPT", "CIP", "DAP", "DDP", "FAS", "FOB", "CFR", "CIF");

    private final TransactionRepository transactionRepository;
    private final EuCountryRepository euCountryRepository;

    public PagedResponse<TransactionDto> list(
            String period,
            String flowType,
            Long legalEntityId,
            String counterpartCountry,
            int page,
            int size) {

        Page<Transaction> txPage = transactionRepository.findAll(
                TransactionSpec.withFilters(period, flowType, legalEntityId, counterpartCountry),
                PageRequest.of(page, size));

        return PagedResponse.from(txPage.map(TransactionDto::from));
    }

    public TransactionDto findById(Long id) {
        return TransactionDto.from(loadOrThrow(id));
    }

    @Transactional
    public TransactionDto patch(Long id, TransactionPatchRequest req) {
        Transaction tx = loadOrThrow(id);

        if (req.getCounterpartCountryCode() != null) {
            EuCountry country = euCountryRepository.findById(req.getCounterpartCountryCode().toUpperCase())
                    .filter(EuCountry::isActiveMember)
                    .orElseThrow(() -> new BusinessValidationException(
                            "counterpartCountryCode",
                            "Must be an active EU member state"));
            tx.setCounterpartCountry(country);
        }

        if (req.getDeliveryTerms() != null) {
            String terms = req.getDeliveryTerms().toUpperCase();
            if (!VALID_DELIVERY_TERMS.contains(terms)) {
                throw new BusinessValidationException(
                        "deliveryTerms",
                        "Must be one of: EXW, FCA, CPT, CIP, DAP, DDP, FAS, FOB, CFR, CIF");
            }
            tx.setDeliveryTerms(terms);
        }

        if (req.getQuantity() != null) {
            tx.setQuantity(req.getQuantity());
            BigDecimal unitMass = tx.getPart().getUnitMassKg();
            tx.setNetMassKg(unitMass.multiply(BigDecimal.valueOf(req.getQuantity())));
        }

        if (req.getStatisticalValueEur() != null) {
            tx.setStatisticalValueEur(req.getStatisticalValueEur());
        }

        if (req.getInvoiceValueEur() != null) {
            tx.setInvoiceValueEur(req.getInvoiceValueEur());
        }

        if (req.getModeOfTransport() != null) {
            tx.setModeOfTransport(req.getModeOfTransport());
        }

        if (req.getNatureOfTransactionCode() != null) {
            tx.setNatureOfTransactionCode(req.getNatureOfTransactionCode());
        }

        return TransactionDto.from(transactionRepository.save(tx));
    }

    private Transaction loadOrThrow(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
    }
}
