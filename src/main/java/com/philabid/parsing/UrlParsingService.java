package com.philabid.parsing;

import com.philabid.service.AuctionHouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * A service that orchestrates URL parsing by selecting the appropriate parser
 * for a given URL from a list of available parsers.
 */
public class UrlParsingService {

    private static final Logger logger = LoggerFactory.getLogger(UrlParsingService.class);
    private final List<UrlAuctionParser> parsers;
    private final AuctionHouseService auctionHouseService;

    public UrlParsingService(List<UrlAuctionParser> parsers, AuctionHouseService auctionHouseService) {
        this.parsers = parsers;
        this.auctionHouseService = auctionHouseService;
        logger.info("Initialized UrlParsingService with {} parsers.", parsers.size());
    }

    /**
     * Parses an auction URL using the first available parser that supports it.
     *
     * @param url The URL to parse.
     * @return An {@link Optional} containing the {@link AuctionPageData} if successful, or an empty Optional if no
     * suitable parser was found or an error occurred.
     */
    public Optional<AuctionPageData> parseUrl(String url) {
        return parsers.stream()
                .filter(parser -> parser.supports(url))
                .findFirst()
                .flatMap(parser -> {
                    try {
                        return Optional.of(parser.parse(url, auctionHouseService));
                    } catch (IOException e) {
                        logger.error("Failed to parse URL '{}' with parser {}", url, parser.getClass().getSimpleName(),
                                e);
                        return Optional.empty();
                    }
                });
    }
}