package com.criteo.onlinesponsoredads.repository;

import com.criteo.onlinesponsoredads.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> getAllBySerialNumberIn(List<String> serialNums);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.campaigns c " +
            "WHERE :currentDate >= c.startDate " +
            "AND :currentDate <= FUNCTION('DATEADD', 'DAY', 10, c.startDate) " +
            "AND p.category = :category " +
            "ORDER BY c.bid DESC ")
    List<Product> findProductsWithHighestBidInActiveCampaign(String category, Instant currentDate, Pageable pageable);

}
