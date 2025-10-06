package com.philabid.ui.cell;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A custom TableCell for displaying an auction's end date.
 * It shows the time in a relative format and displays a warning icon
 * for auctions ending within 24 hours.
 *
 * @param <T> The type of the TableView row data.
 */
public class EndingDateCell<T> extends TableCell<T, LocalDateTime> {

    private final HBox graphicBox = new HBox(5);
    private final Label textLabel = new Label();
    private final SVGPath warningIcon = new SVGPath();

    public EndingDateCell() {
        // A simple flame/warning icon
        warningIcon.setContent("M10,2c-4.42,0-8,3.58-8,8s3.58,8,8,8s8-3.58,8-8S14.42,2,10,2z M11,16H9v-2h2V16z M11,12H9V6h2V12z");
        warningIcon.getStyleClass().add("warning-icon");

        graphicBox.setAlignment(Pos.CENTER_RIGHT);
        graphicBox.getChildren().addAll(warningIcon, textLabel);
        setAlignment(Pos.CENTER_RIGHT);
    }

    @Override
    protected void updateItem(LocalDateTime item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            textLabel.setText(formatRelativeTime(item));

            LocalDateTime now = LocalDateTime.now();
            // Show the icon only for auctions ending within 24 hours
            warningIcon.setVisible(item.isAfter(now) && item.isBefore(now.plusHours(24)));

            setGraphic(graphicBox);
        }
    }

    private String formatRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, dateTime);

        if (duration.isNegative()) {
            return "Ended";
        }

        long days = duration.toDays();
        if (days > 1) {
            return String.format("in %d days", days);
        } else if (days == 1) {
            return String.format("in 1 day, %d h", duration.toHoursPart());
        } else { // Less than a day
            if (duration.toHours() > 0) {
                return String.format("in %d h, %d min", duration.toHoursPart(), duration.toMinutesPart());
            } else if (duration.toMinutes() > 0) {
                return String.format("in %d min", duration.toMinutesPart());
            } else {
                return "ending now";
            }
        }
    }
}