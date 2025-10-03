package com.philabid.parsing;

import com.philabid.model.AuctionHouse;
import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

/**
 * A Data Transfer Object (DTO) to hold raw data scraped from an auction URL.
 */
public record AuctionPageData(
        AuctionHouse auctionHouse,
        String lotId,
        MonetaryAmount currentPrice,
        LocalDateTime closingDate
) {
}