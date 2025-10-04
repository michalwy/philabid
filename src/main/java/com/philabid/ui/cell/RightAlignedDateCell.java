package com.philabid.ui.cell;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A reusable TableCell for displaying LocalDateTime objects, right-aligned.
 * @param <T> The type of the TableView row data.
 */
public class RightAlignedDateCell<T> extends TableCell<T, LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RightAlignedDateCell() {
        setAlignment(Pos.CENTER_RIGHT);
    }

    @Override
    protected void updateItem(LocalDateTime item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(FORMATTER.format(item));
        }
    }
}
