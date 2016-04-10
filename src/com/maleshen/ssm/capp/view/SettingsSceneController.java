package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.PropertiesLoader;
import com.maleshen.ssm.template.Flags;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;

public class SettingsSceneController extends DefaultSceneController {
    private static final String DEFAULT = "Default";

    @FXML
    private ListView<String> list;
    @FXML
    private AnchorPane defSettings;
    @FXML
    private TextField server;
    @FXML
    private TextField port;

    private ObservableList<String> values = FXCollections.observableArrayList();

    @Override
    @FXML
    protected void initialize() {
        values.add(DEFAULT);

        list.setItems(values);

        list.getSelectionModel().select(0);
        openSettings(list.getSelectionModel().getSelectedItem());

        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 0) {
                    openSettings(list.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    private void openSettings(String s) {
        if (s.equals(DEFAULT)) {
            defSettings.setVisible(true);
            server.setText(ClientApp.properties.getProperty(Flags.C_SERVER_A));
            port.setText(ClientApp.properties.getProperty(Flags.C_SERVER_P));
        }
    }

    @FXML
    private void calcel() {
        ((Stage) defSettings.getScene().getWindow()).close();
    }

    @FXML
    private void save() throws IOException {
        ClientApp.properties.setProperty(Flags.C_SERVER_A, server.getText());
        ClientApp.properties.setProperty(Flags.C_SERVER_P, port.getText());
        OutputStream os = PropertiesLoader.getOutputStream();
        if (os != null) {
            ClientApp.properties.store(os, null);
            os.flush();
        }
    }
}
