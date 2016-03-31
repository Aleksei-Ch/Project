package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.SSMConnector;
import com.maleshen.ssm.entity.Message;
import com.maleshen.ssm.entity.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainSceneController extends DefaultSceneController {
    @FXML
    private ImageView avatar;
    @FXML
    private Button add;
    @FXML
    private Button history;
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

    //Dialogs
    private static Map<String, ObservableList<Message>> dialogs = new HashMap();

    @Override
    @FXML
    protected void initialize() {

        //Setting up buttons.
        add.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("img/add.png")))));
        setButtonEffect(add);
        history.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("img/history.png")))));
        setButtonEffect(history);
        settings.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("img/settings.png")))));
        setButtonEffect(settings);
        exit.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("img/exit.png")))));
        setButtonEffect(exit);

        //Fill user info
        name.setText(ClientApp.currentUser.getName());
        lastName.setText(ClientApp.currentUser.getLastName());
        login.setText(ClientApp.currentUser.getLogin());

        //TODO. Avatars.
        avatar.setImage(new Image(String.valueOf(getClass().getResource("img/noav.png"))));

        //Put contacts into the table
        contactName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));

        // Look for click on contact
        contacts.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 0) {
                    if (contacts.getSelectionModel().getSelectedItem() != null)
                        openDialogPane(contacts.getSelectionModel().getSelectedItem());
                }
            }
        });

        //Some logic of renew contact list or any else data.
        new Renewer();
    }

    private void fillChat(String contact){
        chatTable.setItems(dialogs.get(contact));

        author.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFromUser().equals(ClientApp.currentUser.getLogin()) ?
                "You: " : cellData.getValue().getFromUser()));
        message.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMsg()));
        msgTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
    }

    @FXML
    private void sendMessage(){
        if (contacts.getSelectionModel().getSelectedItem() != null &&
                !msg.getText().equals("")){

            Message m = new Message(ClientApp.currentUser.getLogin(),
                    contacts.getSelectionModel().getSelectedItem().getLogin(),
                    msg.getText(),
                    (new SimpleDateFormat("HH:mm")).format(Calendar.getInstance().getTime()),
                    false);

            dialogs.get(contacts.getSelectionModel().getSelectedItem().getLogin()).add(m);
            SSMConnector.sendMessage(m.toString());
            msg.setText("");
        }
    }

    public static void getMessage(Message message){
        if (dialogs.keySet().contains(message.getFromUser())){
            dialogs.get(message.getFromUser()).add(message);
        } else {
            dialogs.put(message.getFromUser(), FXCollections.observableArrayList());
            dialogs.get(message.getFromUser()).add(message);
        }
    }

    private void setButtonEffect(Button b){
        //Adding the shadow when the mouse cursor is on
        b.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        b.setEffect(new DropShadow());
                    }
                });
        //Removing the shadow when the mouse cursor is off
        b.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        b.setEffect(null);
                    }
                });
    }

    private void openDialogPane(User contact){
        welcomeMsg.setVisible(false);
        dialogPane.setVisible(true);
        //init
        nameLastnameContact.setText(contact.getName()+" "+contact.getLastName());
        loginContact.setText(contact.getLogin());
        //TODO. Avatars
        contactAvatar.setImage(new Image(String.valueOf(getClass().getResource("img/noav.png"))));
        //Send button
        send.setGraphic(new ImageView(
                new Image(String.valueOf(getClass().getResource("img/send.png")))));
        setButtonEffect(send);
        //Open ChatTable
        fillChat(contacts.getSelectionModel().getSelectedItem().getLogin());
    }

    private class Renewer implements Runnable{

        private Thread thread;

        Renewer(){
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        private void wait(int msec){
            try {
                Thread.sleep(msec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                if (SSMConnector.authenticated == null || !SSMConnector.authenticated) {
                    break;
                }
                //Send request for updates
                SSMConnector.renewData();

                //Wait for a two second
                wait(2000);

                //Push updated data
                if (ClientApp.contactList != null){
                    int i = contacts.getSelectionModel().getSelectedIndex();
                    contacts.setItems(ClientApp.contactList);
                    contacts.getSelectionModel().select(i);
                }
                //Setting up dialogs
                if (ClientApp.contactList != null)
                    for (User user : ClientApp.contactList){
                        if (!dialogs.containsKey(user.getLogin()))
                            dialogs.put(user.getLogin(), FXCollections.observableArrayList());
                    }

                //Wait for a minute
                wait(60000);
            }
        }
    }

}
