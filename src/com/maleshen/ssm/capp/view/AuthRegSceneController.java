package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.SSMConnector;
import com.maleshen.ssm.entity.AuthInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import static com.maleshen.ssm.capp.ClientApp.primaryStage;

public class AuthRegSceneController extends DefaultSceneController {
    //Login page
    @FXML
    private AnchorPane loginPage;
    @FXML
    private TextField login;
    @FXML
    private PasswordField pass;
    @FXML
    private TextField addess;
    @FXML
    private TextField port;
    @FXML
    private Label authError;
    @FXML
    private Button auth;
    @FXML
    private Button registration;

    //Registration page
    @FXML
    private AnchorPane registrationPage;
    @FXML
    private TextField rName;
    @FXML
    private TextField rLastName;
    @FXML
    private DatePicker rBirthDate;
    @FXML
    private TextField rLogin;
    @FXML
    private PasswordField rPassword;
    @FXML
    private PasswordField rRePassword;

    @Override
    @FXML
    protected void initialize(){}

    //Try to connect & auth
    @FXML
    private void connect() throws Exception {
        try{
            Integer.parseInt(port.getText());
        } catch (Exception e) {
            authError.setText("Please, enter correct values.");
        }
        if (login.getText().equals("") || pass.getText().equals("") || addess.getText().equals("")){
            authError.setText("Please, enter correct values.");
        } else {
            int code = SSMConnector.establishingConnection(new AuthInfo(login.getText(), pass.getText()),
                    addess.getText(),
                    Integer.parseInt(port.getText()));
            if (code == 1) {
                authError.setText("Error. Pls, check fields.");
            } else if (code == 2) {
                authError.setText("Check server and port.");
            } else if (code == 0) {
                //Authenticated, open Main Window
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(ClientApp.class.getResource("view/MainScene.fxml"));
                Parent parent = loader.load();

                primaryStage.setScene(new Scene(parent));
                primaryStage.show();
            }
        }
    }
}
