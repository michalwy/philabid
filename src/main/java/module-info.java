module philabid {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires com.ibm.icu;
    requires org.javamoney.moneta;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires javafx.graphics;
    requires javafx.base;
    requires okhttp3;
    requires org.javamoney.moneta.convert;
    requires org.jetbrains.annotations;
    requires java.money;
    requires com.google.api.client.json.gson;
    requires com.google.api.client;
    requires com.google.api.client.auth;

    opens com.philabid to javafx.fxml;
    opens com.philabid.ui to javafx.fxml;
    opens com.philabid.model to javafx.base, javafx.fxml;

    exports com.philabid;
    exports com.philabid.ui;
    exports com.philabid.ui.util;
    exports com.philabid.ui.control;
    exports com.philabid.model;
    exports com.philabid.service;
    exports com.philabid.database;
    exports com.philabid.parsing;
    exports com.philabid.i18n;
    exports com.philabid.util;
    opens com.philabid.ui.control to javafx.fxml;
    exports com.philabid.database.util;
    opens com.philabid.database.util to javafx.fxml;
    exports com.philabid.ui.cell;
}