package com.criteo.onlinesponsoredads.service;

import com.criteo.onlinesponsoredads.domain.Campaign;
import com.criteo.onlinesponsoredads.domain.Product;
import com.criteo.onlinesponsoredads.domain.dto.CampaignDto;
import com.criteo.onlinesponsoredads.repository.CampaignRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CampaignService {

    @Value("${campaignActivePeriod:10}")
    private int campaignActivePeriod;

    private static final Comparator<Product> PRODUCT_COMPARATOR = Comparator.comparing(Product::getPrice).reversed();

    private final CampaignRepository repository;

    public CampaignService(CampaignRepository repository) {
        this.repository = repository;
    }

    public CampaignDto createCampaign(String name, Instant startDate, List<Product> products, double bid) {
        Instant beginOfDay = startDate.truncatedTo(ChronoUnit.DAYS);
        try {
            Campaign campaignDb = repository.save(new Campaign(name, beginOfDay, bid, products));
            return new CampaignDto(campaignDb);
        } catch (Exception e) {
            log.error("error while saving campaign {} to db", name, e);
            throw e;
        }
    }

    public Product getProductOfCampaignWithHighestBid(Instant time) {
        Optional<Campaign> first = repository.findActiveCampaignsOrderByBidDesc(time, PageRequest.of(0, 1)).stream().findFirst();
        if(first.isPresent()) {
            List<Product> products = first.get().getProducts();
            products.sort(PRODUCT_COMPARATOR);
            return products.stream().findFirst().orElse(null);
        }
        return null;
    }

    public List<CampaignDto> getAll() {
        return repository.findAll().stream().map(CampaignDto::new).collect(Collectors.toList());
    }
}
