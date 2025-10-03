package com.philabid.parsing.impl;

import com.philabid.model.AuctionHouse;
import com.philabid.parsing.AuctionPageData;
import com.philabid.parsing.UrlAuctionParser;
import com.philabid.service.AuctionHouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllegroUrlParser implements UrlAuctionParser {

    private static final Logger logger = LoggerFactory.getLogger(AllegroUrlParser.class);

    // This pattern finds the last group of digits in the URL path,
    // ignoring any query parameters (?) or fragments (#).
    private static final Pattern LOT_ID_PATTERN = Pattern.compile("-(\\d+)(?:[?#]|$)");

    @Override
    public boolean supports(String url) {
        return url != null && url.contains("allegro.pl");
    }

    @Override
    public AuctionPageData parse(String url, AuctionHouseService auctionHouseService) throws IOException {
        logger.info("Using AllegroUrlParser for URL: {}", url);

        // Find the corresponding AuctionHouse by name using the service
        AuctionHouse allegroHouse = auctionHouseService.findByName("Allegro")
                .orElse(null);

        Matcher matcher = LOT_ID_PATTERN.matcher(url);
        String lotId = null;
        if (matcher.find()) {
            lotId = matcher.group(1);
            logger.debug("Extracted Lot ID from Allegro URL: {}", lotId);
        } else {
            logger.warn("Could not extract Lot ID from Allegro URL: {}", url);
            // Optionally, you could throw an exception here
            // throw new IOException("Could not parse Lot ID from Allegro URL: " + url);
        }

        return new AuctionPageData(
                allegroHouse,
                lotId,
                null, // Price will be added later
                null  // Closing date will be added later
        );
    }

}