module philabid {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.flywaydb.core;
    requires com.ibm.icu;
    requires org.javamoney.moneta;
    requires java.money;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;

    opens com.philabid to javafx.fxml;
    opens com.philabid.ui to javafx.fxml;
    opens com.philabid.model to javafx.fxml;

    exports com.philabid;
    exports com.philabid.ui;
    exports com.philabid.model;
    exports com.philabid.service;
    exports com.philabid.database;
    exports com.philabid.i18n;
}