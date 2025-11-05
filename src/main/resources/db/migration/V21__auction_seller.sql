ALTER TABLE auctions
    ADD seller_id INTEGER NULL REFERENCES sellers (id);

