package com.philabid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Key;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AllegroApiService {

    private static final Logger logger = LoggerFactory.getLogger(AllegroApiService.class);
    private static final String ALLEGRO_AUTH_URL = "https://allegro.pl/auth/oauth/authorize";
    private static final String ALLEGRO_TOKEN_URL = "https://allegro.pl/auth/oauth/token";
    private static final File DATA_STORE_DIR =
            new File(System.getProperty("user.home"), ".philabid/credentials/allegro");
    private static final int CALLBACK_PORT = 13654;
    private static final String ALLEGRO_API_URL = "https://api.allegro.pl";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ConfigurationService configService;
    private final FileDataStoreFactory dataStoreFactory;

    public AllegroApiService(ConfigurationService configService) {
        this.configService = configService;
        try {
            this.dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public CompletableFuture<Boolean> authorize() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Build the flow for authorization
                AuthorizationCodeFlow flow = buildFlow();

                LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(CALLBACK_PORT).build();

                // This is the heart of the operation - the library opens the browser and waits for the callback
                Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

                if (credential != null && credential.getAccessToken() != null) {
                    logger.info("Successfully authorized with Allegro. Access token obtained.");
                    // The library automatically saves the credential to the FileDataStore
                    return true;
                } else {
                    logger.warn("Authorization failed or was cancelled by the user.");
                    return false;
                }
            } catch (IOException e) {
                logger.error("Exception during Allegro authorization flow.", e);
                return false;
            }
        });
    }

    /**
     * Loads the stored credential for the user.
     *
     * @return The loaded Credential, or null if not found.
     * @throws IOException if there is an issue loading the credential.
     */
    private Credential loadCredential() throws IOException {
        Credential credential = buildFlow().loadCredential("user");
        credential.refreshToken();
        return credential;
    }

    /**
     * Builds the AuthorizationCodeFlow with all necessary Allegro parameters.
     */
    private AuthorizationCodeFlow buildFlow() throws IOException {
        String clientId = configService.getString("allegro.clientId", "");

        return new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new GsonFactory(),
                new GenericUrl(ALLEGRO_TOKEN_URL),
                new ClientParametersAuthentication(clientId, null),
                clientId,
                ALLEGRO_AUTH_URL)
                .setDataStoreFactory(dataStoreFactory)
                .enablePKCE()
                .build();
    }

    public Optional<BidDetails> getBidDetails(String offerId) {
        try {
            Credential credential = loadCredential();
            if (credential == null) {
                logger.warn("Cannot get bidding info: User is not authorized.");
                return Optional.empty();
            }

            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);

            GenericUrl url = new GenericUrl(ALLEGRO_API_URL + "/bidding/offers/" + offerId + "/bid");
            HttpRequest request = requestFactory.buildGetRequest(url).setParser(null);
            request.getHeaders().set("Accept", "application/vnd.allegro.public.v1+json");

            logger.info("Fetching bid details for offer ID: {}", offerId);
            String jsonResponse = request.execute().parseAsString();
            return Optional.of(OBJECT_MAPPER.readValue(jsonResponse, BidDetails.class));
        } catch (IOException e) {
            logger.error("Failed to get bid details for ID: {}", offerId, e);
            return Optional.empty();
        }
    }

    public Optional<BidDetails> sendBid(String offerId, MonetaryAmount amount) {
        try {
            Credential credential = loadCredential();
            if (credential == null) {
                logger.warn("Cannot send bid: User is not authorized.");
                return Optional.empty();
            }

            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);

            SendBidRequest bidRequestObject = new SendBidRequest(AllegroMonetaryAmount.of(amount));
            String jsonContent = OBJECT_MAPPER.writeValueAsString(bidRequestObject);
            ByteArrayContent content =
                    ByteArrayContent.fromString("application/vnd.allegro.public.v1+json", jsonContent);

            GenericUrl url = new GenericUrl(ALLEGRO_API_URL + "/bidding/offers/" + offerId + "/bid");
            HttpRequest request = requestFactory.buildPutRequest(url, content);
            request.getHeaders().set("Accept", "application/vnd.allegro.public.v1+json");

            logger.info("Sending bid for offer ID: {}", offerId);
            String jsonResponse = request.execute().parseAsString();
            return Optional.of(OBJECT_MAPPER.readValue(jsonResponse, BidDetails.class));
        } catch (IOException e) {
            logger.error("Failed to send bid for ID: {}", offerId, e);
            return Optional.empty();
        }
    }

    private record SendBidRequest(AllegroMonetaryAmount maxAmount) {
    }

    public record AllegroMonetaryAmount(
            @Key BigDecimal amount,
            @Key String currency
    ) {
        public static AllegroMonetaryAmount of(MonetaryAmount amount) {
            return new AllegroMonetaryAmount(amount.getNumber().numberValue(BigDecimal.class),
                    amount.getCurrency().getCurrencyCode());
        }

        public MultiCurrencyMonetaryAmount asMultiCurrency() {
            return MultiCurrencyMonetaryAmount.of(Money.of(amount, currency));
        }
    }

    public record BidDetailsAuction(
            @Key("currentPrice") AllegroMonetaryAmount currentPrice
    ) {
    }

    public record BidDetails(
            @Key("maxAmount") AllegroMonetaryAmount maxAmount,
            @Key("highBidder") Boolean highBidder,
            @Key("auction") BidDetailsAuction auction,
            @Key("minimalPriceMet") Boolean minimalPriceMet
    ) {
    }
}