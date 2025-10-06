package com.philabid;

import com.philabid.database.*;
import com.philabid.i18n.I18nManager;
import com.philabid.parsing.UrlParsingService;
import com.philabid.parsing.impl.AllegroUrlParser;
import com.philabid.parsing.impl.EbayUrlParser;
import com.philabid.service.*;
import javafx.application.HostServices;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * A central context holding all application-wide services.
 * This acts as a simple service locator or dependency injection container.
 */
public class AppContext {

    private static AppContext instance;
    private final I18nManager i18nManager;
    private final CurrencyService currencyService;
    private final AuctionHouseService auctionHouseService;
    private final CatalogService catalogService;
    private final CategoryService categoryService;
    private final ConditionService conditionService;
    private final AuctionItemService auctionItemService;
    private final AuctionService auctionService;
    private final CatalogValueService catalogValueService;
    private final UrlParsingService urlParsingService;
    private final DatabaseManager databaseManager;
    private final ConfigurationService configurationService;
    private final ExchangeRateService exchangeRateService;
    private final PriceRecommendationService priceRecommendationService;
    private HostServices hostServices;

    private AppContext() {
        i18nManager = new I18nManager();
        databaseManager = new DatabaseManager();
        configurationService = new ConfigurationService();

        AuctionHouseRepository auctionHouseRepository = new AuctionHouseRepository(databaseManager);
        CatalogRepository catalogRepository = new CatalogRepository(databaseManager);
        CategoryRepository categoryRepository = new CategoryRepository(databaseManager);
        ConditionRepository conditionRepository = new ConditionRepository(databaseManager);
        AuctionItemRepository auctionItemRepository = new AuctionItemRepository(databaseManager);
        AuctionRepository auctionRepository = new AuctionRepository(databaseManager);
        CatalogValueRepository catalogValueRepository = new CatalogValueRepository(databaseManager);
        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(databaseManager);

        currencyService = new CurrencyService();
        auctionHouseService = new AuctionHouseService(auctionHouseRepository);
        catalogService = new CatalogService(catalogRepository);
        categoryService = new CategoryService(categoryRepository);
        conditionService = new ConditionService(conditionRepository);
        auctionItemService = new AuctionItemService(auctionItemRepository);
        auctionService = new AuctionService(auctionRepository);
        catalogValueService = new CatalogValueService(catalogValueRepository);
        urlParsingService =
                new UrlParsingService(List.of(new AllegroUrlParser(), new EbayUrlParser()), auctionHouseService);
        exchangeRateService = new ExchangeRateService(exchangeRateRepository);
        priceRecommendationService = new PriceRecommendationService();
    }

    public static AppContext get() {
        if (Objects.isNull(instance)) {
            instance = new AppContext();
        }
        return instance;
    }

    public static I18nManager getI18nManager() {
        return get().i18nManager;
    }

    public static CurrencyService getCurrencyService() {
        return get().currencyService;
    }

    public static AuctionHouseService getAuctionHouseService() {
        return get().auctionHouseService;
    }

    public static CatalogService getCatalogService() {
        return get().catalogService;
    }

    public static CategoryService getCategoryService() {
        return get().categoryService;
    }

    public static ConditionService getConditionService() {
        return get().conditionService;
    }

    public static AuctionItemService getAuctionItemService() {
        return get().auctionItemService;
    }

    public static AuctionService getAuctionService() {
        return get().auctionService;
    }

    public static CatalogValueService getCatalogValueService() {
        return get().catalogValueService;
    }

    public static UrlParsingService getUrlParsingService() {
        return get().urlParsingService;
    }

    public static HostServices getHostServices() {
        return get().hostServices;
    }

    public static ConfigurationService getConfigurationService() {
        return get().configurationService;
    }

    public static ExchangeRateService getExchangeRateService() {
        return get().exchangeRateService;
    }

    public static PriceRecommendationService getPriceRecommendationService() {
        return get().priceRecommendationService;
    }

    public static DatabaseManager getDatabaseManager() {
        return get().databaseManager;
    }

    public void init(HostServices hostServices) throws SQLException {
        databaseManager.initialize();

        this.hostServices = hostServices;
    }
}