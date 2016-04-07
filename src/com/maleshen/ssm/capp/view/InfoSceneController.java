package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.ClientConnector;
import com.maleshen.ssm.entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class InfoSceneController extends DefaultSceneController {
    public static User user;
    //Panes
    @FXML
    private AnchorPane my;
    @FXML
    private AnchorPane otherUser;
    //My profile
    @FXML
    private Label login;
    @FXML
    private TextField name;
    @FXML
    private TextField lastname;
    @FXML
    private ImageView avatar;
    @FXML
    private DatePicker bitrhdate;
    @FXML
    private PasswordField pass;
    @FXML
    private PasswordField retypePass;
    @FXML
    private Label infoString;
    //User profile
    @FXML
    private Label userLogin;
    @FXML
    private Label userName;
    @FXML
    private Label userLastname;
    @FXML
    private ImageView userAvatar;
    @FXML
    private Label userBitrhdate;

    @Override
    @FXML
    protected void initialize() {
        infoString.setText("");
        fill();
    }

    private void fill() {
        if (user.getLogin().equals(ClientApp.currentUser.getLogin())) {
            my.setVisible(true);
            otherUser.setVisible(false);
            showMyInfo();
        } else {
            my.setVisible(false);
            otherUser.setVisible(true);
            showUserInfo();
        }
    }

    private void showMyInfo() {
        login.setText(user.getLogin());
        name.setText(user.getName());
        lastname.setText(user.getLastName());
        bitrhdate.getEditor().setText(user.getBirthDateString());
    }

    private void showUserInfo() {
        userLogin.setText(user.getLogin());
        userName.setText(user.getName());
        userLastname.setText(user.getLastName());
        userBitrhdate.setText(user.getBirthDateString());
    }

    @FXML
    private void close() {
        ((Stage) my.getScene().getWindow()).close();
    }

    @FXML
    private void update() {
        if (checkFields()) {
            infoString.setText("");
            Date bd = Date.from(LocalDateTime.from(
                    bitrhdate.getValue().atStartOfDay()).atZone(ZoneId.systemDefault()).toInstant());

            User updatedUser = pass.getText().equals("") ?
                    new User(login.getText(), name.getText(), lastname.getText(), bd) :
                    new User(login.getText(), pass.getText(), name.getText(), lastname.getText(), bd);

            ClientConnector.updateMe(updatedUser);

            localUpdate(updatedUser);

            infoString.setText("Success! Wait for a minute.");
        } else {
            infoString.setText("Check fields.");
        }
    }

    private boolean checkFields() {
        return pass.getText().equals(retypePass.getText());
    }

    private void localUpdate(User user) {
        ClientApp.currentUser.setName(user.getName());
        ClientApp.currentUser.setLastName(user.getLastName());
        ClientApp.currentUser.setBirthDate(user.getBirthDate());
    }
}
