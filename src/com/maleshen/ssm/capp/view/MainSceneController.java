package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

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
}
