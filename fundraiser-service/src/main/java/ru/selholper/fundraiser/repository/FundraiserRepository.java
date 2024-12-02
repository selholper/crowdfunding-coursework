package ru.selholper.fundraiser.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.selholper.fundraiser.model.Fundraiser;

@Repository
public interface FundraiserRepository extends JpaRepository<Fundraiser, Long> {

	List<Fundraiser> findAllByOrderByCreatedAtDesc();

    @Modifying
    @Query("UPDATE Fundraiser f SET f.moneyCurrent = f.moneyCurrent + :donatedMoney WHERE f.id = :fundraiserId")
    void updateMoneyCurrent(@Param("fundraiserId") Long fundraiserId, @Param("donatedMoney") BigDecimal donatedMoney);
}
