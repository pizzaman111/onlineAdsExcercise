package com.criteo.onlinesponsoredads.repository;

import com.criteo.onlinesponsoredads.domain.Campaign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Query("SELECT c FROM Campaign c " +
            "WHERE :currentDate >= c.startDate " +
            "AND :currentDate <= FUNCTION('DATEADD', 'DAY', 10, c.startDate) " +
            "ORDER BY c.bid DESC ")
    List<Campaign> findActiveCampaignsOrderByBidDesc(Instant currentDate, Pageable pageable);
}
