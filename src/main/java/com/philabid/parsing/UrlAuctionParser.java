package com.philabid.parsing;

import com.philabid.service.AuctionHouseService;
import java.io.IOException;

/**
 * Defines the contract for a parser that can extract auction data from a specific auction site URL.
 */
public interface UrlAuctionParser {

    /**
     * Checks if this parser can handle the given URL.
     *
     * @param url The URL of the auction page.
     * @return {@code true} if the parser supports this URL, {@code false} otherwise.
     */
    boolean supports(String url);

    /**
     * Parses the given URL and extracts auction data.
     *
     * @param url The URL of the auction page.
     * @param auctionHouseService A service to find auction houses in the database.
     * @return An {@link AuctionPageData} object containing the scraped data.
     * @throws IOException if there is a problem fetching or parsing the page.
     */
    AuctionPageData parse(String url, AuctionHouseService auctionHouseService) throws IOException;
}