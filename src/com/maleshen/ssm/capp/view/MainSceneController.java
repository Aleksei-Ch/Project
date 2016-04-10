package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.ClientConnector;
import com.maleshen.ssm.entity.Message;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.template.Flags;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.maleshen.ssm.capp.ClientApp.primaryStage;

public class MainSceneController extends DefaultSceneController {
    //Dialogs
    static Map<String, ObservableList<Message>> dialogs = new HashMap();
    static boolean findIsOpen = false;
    static boolean settingsIsOpen = false;

    @FXML
    private ImageView avatar;
    @FXML
    private Button add;
    @FXML
    private Button info;
    @FXML
    private Button settings;
    @FXML
    private Button exit;
    @FXML
    private Label name;
    @FXML
    private Label lastName;
    @FXML
    private Label login;
    @FXML
    private TableView<User> contacts;
    @FXML
    private TableColumn<User, String> contactName;
    @FXML
    private AnchorPane welcomeMsg;
    @FXML
    private AnchorPane dialogPane;
    //Dialog fields
    @FXML
    private Label nameLastnameContact;
    @FXML
    private Label loginContact;
    @FXML
    private TextArea msg;
    @FXML
    private Button send;
    @FXML
    private ImageView contactAvatar;
    @FXML
    private TableView<Message> chatTable;
    @FXML
    private TableColumn<Message, String> author;
    @FXML
    private TableColumn<Message, String> message;
    @FXML
    private TableColumn<Message, String> msgTime;
    //Find contacts scene
    private Stage findAddContactsStage = new Stage();
    private Stage settingsStage = new Stage();
    private Stage infoStage = new Stage();

    public static void getMessage(Message message) {
        if (dialogs.keySet().contains(message.getFromUser())) {
            dialogs.get(message.getFromUser()).add(message);
        } else {
            //TODO. Contact request
            dialogs.put(message.getFromUser(), FXCollections.observableArrayList());
            dialogs.get(message.getFromUser()).add(message);
        }
    }

    @Override
    @FXML
    protected void initialize() {
        final ContextMenu contextMenu = new ContextMenu();

        MenuItem remove = new MenuItem("Remove from contacts");

        remove.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                removeContact(contacts.getSelectionModel().getSelectedItem());
            }
        });

        MenuItem information = new MenuItem("Info");

        information.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                openUserInfo(contacts.getSelectionModel().getSelectedItem());
            }
        });

        contextMenu.getItems().addAll(information, remove);

        //Setting up buttons.
        add.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("/resources/client/img/add.png")))));
        setButtonEffect(add);
        info.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("/resources/client/img/info.png")))));
        setButtonEffect(info);
        settings.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("/resources/client/img/settings.png")))));
        setButtonEffect(settings);
        exit.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("/resources/client/img/exit.png")))));
        setButtonEffect(exit);
        send.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("/resources/client/img/send.png")))));
        setButtonEffect(send);

        //Fill user info
        fillUserInfo();

        //TODO. Avatars.
        avatar.setImage(new Image(String.valueOf(getClass().getResource("/resources/client/img/noav.png"))));

        message.setCellFactory(new Callback<TableColumn<Message, String>, TableCell<Message, String>>() {
            @Override
            public TableCell<Message, String> call(TableColumn<Message, String> param) {
                return new TableCell<Message, String>() {
                    private Text text;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item);
                            text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                            text.fontProperty().bind(fontProperty());
                            setGraphic(text);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });

        //Put contacts into the table
        contactName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));

        // Look for click on contact
        contacts.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 0) {
                    if (contacts.getSelectionModel().getSelectedItem() != null) {
                        openDialogPane(contacts.getSelectionModel().getSelectedItem());
                        contacts.setContextMenu(contextMenu);
                    }
                }
            }
        });

        //Some logic of renew contact list or any else data.
        new Renew();
    }

    private void fillUserInfo() {
        name.setText(ClientApp.currentUser.getName());
        lastName.setText(ClientApp.currentUser.getLastName());
        login.setText(ClientApp.currentUser.getLogin());
    }

    private void removeContact(User user) {
        contacts.getItems().remove(user);
        dialogs.remove(user.getLogin());
        ClientConnector.sendContactRemReq(ClientApp.currentUser.getId() +
                Flags.USER_SPLITTER + user.getId());
    }

    /**
     * This method must fill chat table
     * for current selected contact
     *
     * @param contact login of selected contact
     */
    private void fillChat(String contact) {
        chatTable.setItems(dialogs.get(contact));

        author.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFromUser().equals(ClientApp.currentUser.getLogin()) ?
                        "You: " : cellData.getValue().getFromUser()));
        message.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMsg()));
        msgTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
    }

    @FXML
    private void sendMessage() {
        if (contacts.getSelectionModel().getSelectedItem() != null &&
                !msg.getText().equals("")) {

            Message m = new Message(ClientApp.currentUser.getLogin(),
                    contacts.getSelectionModel().getSelectedItem().getLogin(),
                    msg.getText(),
                    (new SimpleDateFormat("HH:mm")).format(Calendar.getInstance().getTime()));

            dialogs.get(contacts.getSelectionModel().getSelectedItem().getLogin()).add(m);
            ClientConnector.sendMessage(m.toString());
            msg.setText("");
        }
    }

    private void setButtonEffect(Button b) {
        //Adding the shadow when the mouse cursor is on
        b.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        b.setEffect(new DropShadow());
                    }
                });
        //Removing the shadow when the mouse cursor is off
        b.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        b.setEffect(null);
                    }
                });
    }

    private void openDialogPane(User contact) {
        if (contact != null) {
            welcomeMsg.setVisible(false);
            dialogPane.setVisible(true);
            //init
            nameLastnameContact.setText(contact.getName() + " " + contact.getLastName());
            loginContact.setText(contact.getLogin());
            //TODO. Avatars
            contactAvatar.setImage(new Image(String.valueOf(getClass().getResource("/resources/client/img/noav.png"))));

            //Open ChatTable
            fillChat(contacts.getSelectionModel().getSelectedItem().getLogin());
        }
    }

    @FXML
    private void findAddContact() throws IOException {
        if (!findAddContactsStage.isShowing()) {
            findIsOpen = true;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("view/FindContactsScene.fxml"));

            Parent parent = (Parent) loader.load();

            findAddContactsStage.setScene(new Scene(parent));
            findAddContactsStage.setResizable(false);
            findAddContactsStage.show();
            findAddContactsStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    findAddContactsStage.close();
                    findIsOpen = false;
                }
            });
        }
    }

    @FXML
    private void logout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApp.class.getResource("view/AuthRegScene.fxml"));
        Parent parent = (Parent) loader.load();

        ClientConnector.authenticated = false;
        ClientConnector.close();
        ClientApp.currentUser = null;
        dialogs = new HashMap<>();


        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    @FXML
    private void info() {
        openUserInfo(ClientApp.currentUser);
    }

    private void openUserInfo(User user) {
        InfoSceneController.user = user;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApp.class.getResource("view/InfoScene.fxml"));

        Parent parent = null;
        try {
            parent = (Parent) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert parent != null;
        infoStage.setScene(new Scene(parent));
        infoStage.setResizable(false);
        infoStage.show();
    }

    @FXML
    private void settings() throws IOException {
        if (!settingsStage.isShowing()) {
            settingsIsOpen = true;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("view/SettingsScene.fxml"));

            Parent parent = (Parent) loader.load();

            settingsStage.setScene(new Scene(parent));
            settingsStage.setResizable(false);
            settingsStage.show();
            settingsStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    settingsStage.close();
                    settingsIsOpen = false;
                }
            });
        }
    }

    /**
     * Inner class for daemon thread
     * needed for autorenew some data
     * every time.
     */
    private class Renew implements Runnable {

        private Thread thread;

        Renew() {
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
                if (ClientConnector.authenticated == null || !ClientConnector.authenticated) {
                    break;
                }
                //Send request for updates
                ClientConnector.renewData();

                //Wait for a two second
                wait(2000);

//                Platform.runLater(MainSceneController::fillUserInfo);

                //Push updated data
                if (ClientApp.contactList != null && ClientApp.contactList.size() > 0) {
                    int i = contacts.getSelectionModel().getSelectedIndex();
                    contacts.setItems(ClientApp.contactList);
                    contacts.getSelectionModel().select(i);
                }
//                Setting up dialogs
                if (ClientApp.contactList != null && ClientApp.contactList.size() > 0) {
                    ClientApp.contactList.stream().filter(u -> !dialogs.containsKey(u.getLogin())).forEach(u -> {
                        dialogs.put(u.getLogin(), FXCollections.observableArrayList());
                    });
                }

                //Wait for a 30 sec
                wait(30000);
            }
        }
    }
}
