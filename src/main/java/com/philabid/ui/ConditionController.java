package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Condition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the Condition management view (ConditionView.fxml).
 */
public class ConditionController {

    private static final Logger logger = LoggerFactory.getLogger(ConditionController.class);
    private final ObservableList<Condition> conditionList = FXCollections.observableArrayList();
    @FXML
    private TableView<Condition> conditionTable;
    @FXML
    private TableColumn<Condition, String> nameColumn;
    @FXML
    private TableColumn<Condition, String> codeColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        conditionTable.setItems(conditionList);

        editButton.disableProperty().bind(conditionTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(conditionTable.getSelectionModel().selectedItemProperty().isNull());

        loadConditions();
        logger.debug("ConditionController initialized.");
    }

    private void loadConditions() {
        conditionList.setAll(AppContext.getConditionService().getAllConditions());
        logger.info("Loaded {} conditions into the table.", conditionList.size());
    }

    @FXML
    private void handleAddCondition() {
        logger.info("Add condition button clicked.");
        Condition newCondition = new Condition();
        boolean saveClicked = showConditionEditDialog(newCondition);
        if (saveClicked) {
            AppContext.getConditionService().saveCondition(newCondition);
            loadConditions();
        }
    }

    @FXML
    private void handleEditCondition() {
        Condition selected = conditionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit condition button clicked for: {}", selected.getName());
            boolean saveClicked = showConditionEditDialog(selected);
            if (saveClicked) {
                AppContext.getConditionService().saveCondition(selected);
                loadConditions();
            }
        }
    }

    @FXML
    private void handleDeleteCondition() {
        Condition selected = conditionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete condition button clicked for: {}", selected.getName());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Condition");
            alert.setContentText("Are you sure you want to delete the selected condition: " + selected.getName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = AppContext.getConditionService().deleteCondition(selected.getId());
                if (deleted) {
                    loadConditions();
                } else {
                    logger.error("Failed to delete condition with ID: {}", selected.getId());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    private boolean showConditionEditDialog(Condition condition) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ConditionEditDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());

            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(condition.getId() == null ? "Add Condition" : "Edit Condition");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(conditionTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ConditionEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCondition(condition);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            logger.error("Failed to load the condition edit dialog.", e);
            return false;
        }
    }
}
