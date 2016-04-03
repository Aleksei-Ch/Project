package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.SSMConnector;
import com.maleshen.ssm.entity.ArrayListExt;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.template.Flags;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class FindAddContactsController extends DefaultSceneController {
    @FXML
    private TextField keywords;
    @FXML
    private Button search;
    @FXML
    private TableView<User> results;
    @FXML
    private TableColumn<User, String> login;
    @FXML
    private TableColumn<User, String> name;
    @FXML
    private TableColumn<User, String> lastName;
    @FXML
    private Label notFound;
    @FXML
    private Label success;

    private static ObservableList<User> resultSet;
    private static boolean founded = true;
    private static boolean error = false;

    @FXML
    @Override
    protected void initialize() {
        resultSet = FXCollections.observableArrayList();

        final ContextMenu contextMenu = new ContextMenu();

        MenuItem add = new MenuItem("Add to contacts");

        add.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                addContact(results.getSelectionModel().getSelectedItem());
            }
        });

        contextMenu.getItems().addAll(add);

        login.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));
        name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        lastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        results.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 0) {
                    if (results.getSelectionModel().getSelectedItem() != null) {
                        results.setContextMenu(contextMenu);
                    } else {
                        results.setContextMenu(null);
                    }
                }
            }
        });

        new Renewer();
    }

    @FXML
    private void search() throws InterruptedException {
        success.setVisible(false);
        results.setVisible(true);
        notFound.setVisible(false);
        error = false;
        notFound.setText("No matches founded.");
        if (!keywords.getText().equals("")) {
            resultSet = FXCollections.observableArrayList();
            SSMConnector.sendFoundRequest(keywords.getText());
        }
    }

    private void error(){
        keywords.setText("");
        results.setVisible(false);
        notFound.setVisible(true);
        notFound.setText("This user already in your contact list or it's you.");
        error = true;
    }

    private void addContact(User user) {
        if (user.getLogin().equals(ClientApp.currentUser.getLogin()) ||
                MainSceneController.dialogs.containsKey(user.getLogin())){
            error();
            return;
        }
        SSMConnector.sendContactsReq(ClientApp.currentUser.getId() +
                Flags.USER_SPLITTER + user.getId());
        success.setVisible(true);
        SSMConnector.renewData();
    }

    public static void getResult(ArrayListExt<User> results) {
        if (results != null && results.size() > 0) {
            founded = true;
            resultSet.addAll(results);
        } else {
            founded = false;
        }
    }

    private class Renewer implements Runnable {

        private Thread thread;

        Renewer() {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        private void wait(int msec) {
            try {
                Thread.sleep(msec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {

                wait(2000);

                if (!MainSceneController.findIsOpen) {
                    break;
                }

                results.setItems(resultSet);

                if (!error) {
                    results.setVisible(founded);
                    notFound.setVisible(!founded);
                }

            }
        }
    }
}

