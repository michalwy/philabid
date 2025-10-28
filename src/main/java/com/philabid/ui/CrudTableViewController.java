package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.BaseModel;
import com.philabid.service.CrudService;
import com.philabid.ui.control.CrudEditDialog;
import com.philabid.ui.control.CrudTableView;
import com.philabid.util.TriConsumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class CrudTableViewController<T extends BaseModel<T>> extends TableViewController {
    private static final Logger logger = LoggerFactory.getLogger(CrudTableViewController.class);

    protected final ObservableList<T> tableItems = FXCollections.observableArrayList();
    private final List<TriConsumer<TableRow<T>, T, Boolean>> rowFormatters = new ArrayList<>();
    private final CrudService<T> crudService;

    @FXML
    private CrudTableView<T> crudTableView;

    protected CrudTableViewController(CrudService<T> crudService) {
        this.crudService = crudService;
    }

    @FXML
    protected void initialize() {
        crudTableView.setItems(tableItems);
        initializeView();
        initializeToolbar();
        initializeFilterToolbar();
        setRowFactories();
        setupContextMenu();
    }

    @Override
    protected void refreshTable() {
        logger.info("Refreshing table view");
        tableItems.setAll(loadTableItems());
        crudTableView.sort();
    }

    @Override
    public void unload() {
        tableItems.clear();
    }

    protected void initializeView() {
    }

    private void initializeToolbar() {
        crudTableView.setAddAction(e -> handleAdd());
        crudTableView.setEditAction(e -> handleEdit());
        crudTableView.setDeleteAction(e -> handleDelete());
        crudTableView.setRefreshAction(e -> refreshTable());
        crudTableView.bindButtonsDisabledProperty(
                crudTableView.getTableView().getSelectionModel().selectedItemProperty().isNull());
    }

    protected CrudService<T> getCrudService() {
        return crudService;
    }

    protected void initializeFilterToolbar() {
    }

    protected void addRowFormatter(TriConsumer<TableRow<T>, T, Boolean> formatter) {
        rowFormatters.add(formatter);
    }

    private void setRowFactories() {
        crudTableView.getTableView().setRowFactory(param -> {
            TableRow<T> row = new TableRow<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);

                    rowFormatters.forEach(formatter -> formatter.accept(this, item, isEmpty()));
                }
            };

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    handleDoubleClick();
                }
            });

            return row;
        });
    }

    private void setupContextMenu() {
        List<MenuItem> menuItems = getContextMenuItems();

        if (menuItems.isEmpty()) {
            return;
        }

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(menuItems);

        // Show context menu only for non-empty rows
        crudTableView.getTableView().setContextMenu(contextMenu);
        contextMenu.setOnShowing(e -> {
            if (crudTableView.getTableView().getSelectionModel().isEmpty()) {
                e.consume(); // Don't show the menu
            }
            onContextMenuShowing(contextMenu);
        });
    }

    protected List<MenuItem> getContextMenuItems() {
        return List.of();
    }

    protected CrudTableView<T> getCrudTableView() {
        return crudTableView;
    }

    /**
     * A hook for subclasses to customize the context menu just before it is shown.
     * For example, to hide or disable certain items based on the selected row.
     *
     * @param contextMenu The context menu that is about to be shown.
     */
    protected void onContextMenuShowing(ContextMenu contextMenu) {
        // Default implementation does nothing.
    }

    protected Collection<T> loadTableItems() {
        return crudService.getAll();
    }

    protected abstract String getDialogFXMLResourcePath();

    protected void handleDoubleClick() {
        handleEdit();
    }

    protected void handleAdd() {
        logger.info("Add category button clicked.");
        T newEntity = crudService.create();
        EditDialogResult result = showEntityEditDialog(newEntity);
        if (result != null && result.saved()) {
            crudService.save(newEntity);
            refreshTable();
        }
    }

    protected void handleEdit() {
        T selected = crudTableView.getTableView().getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit button clicked for entity: {}", selected.getDisplayName());
            EditDialogResult result = showEntityEditDialog(selected);
            if (result != null && result.saved()) {
                crudService.save(selected);
                refreshTable();
            }
        }
    }

    protected EditDialogResult showEntityEditDialog(T entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(getDialogFXMLResourcePath()));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());

            CrudEditDialog<T> page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(entity.getId() == null ? "Create" : "Edit");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(crudTableView.getTableView().getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CrudEditDialogController<T> controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEntity(entity);

            dialogStage.showAndWait();

            return controller.getResult();
        } catch (IOException e) {
            logger.error("Failed to load edit dialog.", e);
            return null;
        }
    }

    protected void handleDelete() {
        T selected = crudTableView.getTableView().getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete");
            alert.setContentText(
                    "Are you sure you want to delete the selected item: " +
                            selected.getDisplayName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = crudService.delete(selected.getId());
                if (deleted) {
                    refreshTable();
                } else {
                    logger.error("Failed to delete item with ID: {}", selected.getId());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.setHeaderText("Could not delete the selected item.");
                    errorAlert.setContentText("An error occurred while trying to delete the selected item. Please " +
                            "check the logs.");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    protected TableView<T> getTableView() {
        return crudTableView.getTableView();
    }
}
