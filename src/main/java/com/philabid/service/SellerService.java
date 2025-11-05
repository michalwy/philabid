package com.philabid.service;

import com.philabid.database.SellerRepository;
import com.philabid.model.Seller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

public class SellerService extends AbstractCrudService<Seller> {

    private static final Logger logger = LoggerFactory.getLogger(SellerService.class);
    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        super(sellerRepository);
        this.sellerRepository = sellerRepository;
    }

    @Override
    protected boolean validate(Seller seller) {
        if (seller.getName() == null || seller.getName().trim().isEmpty()) {
            logger.warn("Attempted to save a seller with an empty name.");
            return false;
        }
        return true;
    }

    public Optional<Seller> findByName(String name) {
        try {
            return sellerRepository.findByName(name);
        } catch (SQLException e) {
            logger.error("Failed to find seller with name: {}", name, e);
            return Optional.empty();
        }
    }
}
