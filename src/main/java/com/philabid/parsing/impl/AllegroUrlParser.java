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

    // New pattern for URLs with "offerId=" query parameter
    private static final Pattern NEW_LOT_ID_PATTERN = Pattern.compile("offerId=(\\d+)");

    // Old pattern for URLs where the ID is at the end of the path
    private static final Pattern OLD_LOT_ID_PATTERN = Pattern.compile("-(\\d+)(?:[?#]|$)");

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

        String lotId = null;

        // 1. Try the new pattern first (more specific)
        Matcher newMatcher = NEW_LOT_ID_PATTERN.matcher(url);
        if (newMatcher.find()) {
            lotId = newMatcher.group(1);
            logger.debug("Extracted Lot ID from Allegro URL (new format): {}", lotId);
        } else {
            // 2. If the new pattern fails, try the old one
            Matcher oldMatcher = OLD_LOT_ID_PATTERN.matcher(url);
            if (oldMatcher.find()) {
                lotId = oldMatcher.group(1);
                logger.debug("Extracted Lot ID from Allegro URL (old format): {}", lotId);
            } else {
                logger.warn("Could not extract Lot ID from Allegro URL: {}", url);
            }
        }

        return new AuctionPageData(
                allegroHouse,
                lotId,
                null, // Price will be added later
                null  // Closing date will be added later
        );
    }

}