package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.SSMConnector;
import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.time.*;
import java.util.Date;

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
    private TextField address;
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
    private DatePicker rBirthDate = new DatePicker();
    @FXML
    private TextField rLogin;
    @FXML
    private PasswordField rPassword;
    @FXML
    private PasswordField rRePassword;
    @FXML
    private Label rErrMsg;
    @FXML
    private TextField rAddress;
    @FXML
    private TextField rPort;

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
        if (login.getText().equals("") || pass.getText().equals("") || address.getText().equals("")){
            authError.setText("Please, enter correct values.");
        } else {
            int code = SSMConnector.establishingConnection(new AuthInfo(login.getText(), pass.getText()),
                    address.getText(),
                    Integer.parseInt(port.getText()));

            switch (code){
                //Authenticated, open Main Window
                case 0:
                    openMain();
                    break;
                case 1:
                    authError.setText("Error. Pls, check fields.");
                    break;
                default:
                    authError.setText("Check server and port.");
                    break;
            }
        }
    }

    @FXML
    private void regInit(){
        rBirthDate.setValue(LocalDate.now());
        rAddress.setText(address.getText());
        rPort.setText(port.getText());
        loginPage.setVisible(false);
        registrationPage.setVisible(true);
    }

    @FXML
    private void regCancel(){
        registrationPage.setVisible(false);
        loginPage.setVisible(true);
    }

    //Try to connect & register
    @FXML
    private void regMe(){

        //Validate fields
        try{
            Integer.parseInt(rPort.getText());
        } catch (Exception e) {
            rErrMsg.setText("Pls, check typed values!");
        }
        if(rLogin.getText().equals("") ||
                rPassword.getText().equals("") ||
                rRePassword.getText().equals("") ||
                !rPassword.getText().equals(rRePassword.getText())){
            rErrMsg.setText("Pls, check typed values!");
        } else {
            //Convert DatePicker to Data
            Date bd = Date.from(LocalDateTime.from(
                    rBirthDate.getValue().atStartOfDay()).atZone(ZoneId.systemDefault()).toInstant());

            User userForReg = new User(rLogin.getText(), rPassword.getText(), rName.getText(),
                    rLastName.getText(), bd);

            try {
                int code = SSMConnector.establishingConnection(userForReg,
                        rAddress.getText(), Integer.parseInt(rPort.getText()));

                switch (code){
                    //All is Ok
                    case 0:
                        openMain();
                        break;
                    //Not registered
                    case 1:
                        rErrMsg.setText("Something wrong. Try another values.");
                        break;
                    //Another problem
                    default:
                        rErrMsg.setText("Server not found.");
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void openMain() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApp.class.getResource("view/MainScene.fxml"));
        Parent parent = loader.load();

        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                SSMConnector.close();
                System.exit(0);
            }
        });
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //TODO. Hide window to tray.
            }
        });
    }
}
