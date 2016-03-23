package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainSceneController extends DefaultSceneController {
    @FXML
    private ImageView avatar;
    @FXML
    private Button add;
    @FXML
    private Label name;
    @FXML
    private Label lastName;
    @FXML
    private Label login;

    @Override
    @FXML
    protected void initialize() {
        //Fill user info
        name.setText(ClientApp.currentUser.getName());
        lastName.setText(ClientApp.currentUser.getLastName());
        login.setText(ClientApp.currentUser.getLogin());

        //Avatar TODO.
        avatar.setImage(new Image(String.valueOf(getClass().getResource("img/noav.png"))));

    }
}
