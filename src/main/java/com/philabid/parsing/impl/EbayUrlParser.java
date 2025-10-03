package com.philabid.parsing.impl;

import com.philabid.model.AuctionHouse;
import com.philabid.parsing.AuctionPageData;
import com.philabid.parsing.UrlAuctionParser;
import com.philabid.service.AuctionHouseService;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EbayUrlParser implements UrlAuctionParser {

    private static final Logger logger = LoggerFactory.getLogger(EbayUrlParser.class);

    @Override
    public boolean supports(String url) {
        return url != null && (url.contains("ebay.com") || url.contains("ebay.de") || url.contains("ebay.pl"));
    }

    @Override
    public AuctionPageData parse(String url, AuctionHouseService auctionHouseService) throws IOException {
        logger.info("Using EbayUrlParser for URL: {}", url);

        // Find the corresponding AuctionHouse by name using the service
        AuctionHouse ebayHouse = auctionHouseService.findByName("eBay")
                .orElse(null);

        // TODO: Implement actual web scraping logic here (e.g., using Jsoup)
        // For now, returning dummy data.
        return new AuctionPageData(
                ebayHouse,
                "ebay-98765",
                Money.of(new BigDecimal("15.50"), "USD"),
                LocalDateTime.now().plusDays(5).plusHours(4)
        );
    }
}