package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Condition;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Condition management view (ConditionView.fxml).
 */
public class ConditionController extends ModalCrudTableViewController<Condition> {

    private static final Logger logger = LoggerFactory.getLogger(ConditionController.class);
    @FXML
    private TableColumn<Condition, String> nameColumn;
    @FXML
    private TableColumn<Condition, String> codeColumn;

    public ConditionController() {
        super(AppContext.getConditionService());
    }

    @Override
    protected void initializeView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/ConditionEditDialog.fxml";
    }
}
