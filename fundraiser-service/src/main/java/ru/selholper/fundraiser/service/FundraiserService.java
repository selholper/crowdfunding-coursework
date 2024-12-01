package ru.selholper.fundraiser.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.selholper.fundraiser.model.Fundraiser;
import ru.selholper.fundraiser.repository.FundraiserRepository;

import jakarta.transaction.Transactional;

@Service
public class FundraiserService {

	private final FundraiserRepository fundraiserRepository;

    @Autowired
    public FundraiserService(FundraiserRepository fundraiserRepository) {
        this.fundraiserRepository = fundraiserRepository;
    }

    public List<Fundraiser> getAllFundraisers() {
        return fundraiserRepository.findAllByOrderByCreatedAtDesc();
    }

	@Transactional
    public Fundraiser addFundraiser(Fundraiser fundraiser) {
        return fundraiserRepository.saveAndFlush(fundraiser);
    }

    @Transactional
    public void updateFundraiser(Long fundraiserId, BigDecimal donatedMoney) {
        Optional<Fundraiser> optionalFundraiser = fundraiserRepository.findById(fundraiserId);
        if (optionalFundraiser.isPresent()) {
            fundraiserRepository.updateMoneyCurrent(fundraiserId, donatedMoney);
        } else {
            throw new RuntimeException("Fundraiser not found.");
        }
    }

	@Transactional
    public void deleteFundraiser(Long fundraiserId) {
        Optional<Fundraiser> fundraiser = fundraiserRepository.findById(fundraiserId);
        if (fundraiser.isPresent()) {
            fundraiserRepository.deleteById(fundraiserId);
        } else {
            throw new RuntimeException("Fundraiser not found.");
        }
    }
}
