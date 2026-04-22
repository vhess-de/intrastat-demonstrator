package com.ford.intrastat.service;

import com.ford.intrastat.dto.CountryDto;
import com.ford.intrastat.dto.LegalEntityDto;
import com.ford.intrastat.dto.PartDto;
import com.ford.intrastat.repository.EuCountryRepository;
import com.ford.intrastat.repository.LegalEntityRepository;
import com.ford.intrastat.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceDataService {

    private final LegalEntityRepository legalEntityRepository;
    private final EuCountryRepository euCountryRepository;
    private final PartRepository partRepository;

    public List<LegalEntityDto> getLegalEntities() {
        return legalEntityRepository.findByActiveTrueOrderByName()
                .stream()
                .map(LegalEntityDto::from)
                .toList();
    }

    public List<CountryDto> getCountries() {
        return euCountryRepository.findByActiveMemberTrueOrderByName()
                .stream()
                .map(CountryDto::from)
                .toList();
    }

    public List<PartDto> getParts() {
        return partRepository.findAllByOrderByPartCode()
                .stream()
                .map(PartDto::from)
                .toList();
    }
}
