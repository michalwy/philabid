package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.Category;
import com.philabid.service.CatalogService;
import com.philabid.service.CategoryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the Category management view (CategoryView.fxml).
 */
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, String> nameColumn;
    @FXML
    private TableColumn<Category, String> codeColumn;
    @FXML
    private TableColumn<Category, String> catalogColumn;
    private CategoryService categoryService;
    private CatalogService catalogService;
    private I18nManager i18nManager;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        // Custom cell value factory to display formatted catalog name and year
        catalogColumn.setCellValueFactory(cellData -> {
            Category category = cellData.getValue();
            return new SimpleStringProperty(String.format("%s (%d)", category.getCatalogName(),
                    category.getCatalogIssueYear()));
        });

        categoryTable.setItems(categoryList);
    }

    public void setServices(CategoryService categoryService, CatalogService catalogService, I18nManager i18nManager) {
        this.categoryService = categoryService;
        this.catalogService = catalogService;
        this.i18nManager = i18nManager;

        loadCategories();
    }

    private void loadCategories() {
        if (categoryService != null) {
            categoryList.setAll(categoryService.getAllCategories());
            logger.info("Loaded {} categories into the table.", categoryList.size());
        } else {
            logger.warn("CategoryService is not available. Cannot load data.");
        }
    }

    @FXML
    private void handleAddCategory() {
        logger.info("Add category button clicked.");
        Category newCategory = new Category();
        boolean saveClicked = showCategoryEditDialog(newCategory);
        if (saveClicked) {
            categoryService.saveCategory(newCategory);
            loadCategories();
        }
    }

    @FXML
    private void handleEditCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit category button clicked for: {}", selected.getName());
            boolean saveClicked = showCategoryEditDialog(selected);
            if (saveClicked) {
                categoryService.saveCategory(selected);
                loadCategories();
            }
        }
    }

    @FXML
    private void handleDeleteCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete category button clicked for: {}", selected.getName());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Category");
            alert.setContentText("Are you sure you want to delete the selected category: " + selected.getName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = categoryService.deleteCategory(selected.getId());
                if (deleted) {
                    loadCategories();
                } else {
                    logger.error("Failed to delete category with ID: {}", selected.getId());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    private boolean showCategoryEditDialog(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CategoryEditDialog.fxml"));
            loader.setResources(i18nManager.getResourceBundle());

            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(category.getId() == null ? "Add Category" : "Edit Category");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(categoryTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CategoryEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setServices(catalogService);
            controller.setCategory(category);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            logger.error("Failed to load the category edit dialog.", e);
            return false;
        }
    }
}
