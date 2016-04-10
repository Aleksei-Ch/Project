package com.maleshen.ssm.capp.view;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.ClientConnector;
import com.maleshen.ssm.capp.model.PropertiesLoader;
import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.template.Flags;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.maleshen.ssm.capp.ClientApp.primaryStage;

public class AuthRegSceneController extends DefaultSceneController {
    public static boolean sslErr = false;
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

    public static void sslError() {
        sslErr = true;
    }

    @Override
    @FXML
    protected void initialize() {
        if (ClientApp.properties != null) {
            address.setText(ClientApp.properties.getProperty(Flags.C_SERVER_A));
            port.setText(ClientApp.properties.getProperty(Flags.C_SERVER_P));
            login.setText(ClientApp.properties.getProperty(Flags.C_DEF_LOGIN));
        }
    }

    private void propertiesSave() throws IOException {
        ClientApp.properties.setProperty(Flags.C_SERVER_A, address.getText());
        ClientApp.properties.setProperty(Flags.C_SERVER_P, port.getText());
        ClientApp.properties.setProperty(Flags.C_DEF_LOGIN, login.getText());
        OutputStream os = PropertiesLoader.getOutputStream();
        if (os != null) {
            ClientApp.properties.store(os, null);
            os.flush();
        }
    }

    private boolean chkport(int i) {
        try {
            int p = i == 0 ? Integer.valueOf(port.getText()) : Integer.valueOf(rPort.getText());
            if (p < 0 || p > 65535) {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            authError.setText("Port is wrong.");
            rErrMsg.setText("Port is wrong.");
            return false;
        }
    }

    //Try to connect & auth
    @FXML
    private void connect() throws Exception {
        if (!chkport(0))
            return;
        if (login.getText().equals("") || pass.getText().equals("") || address.getText().equals("")) {
            authError.setText("Please, enter correct values.");
        } else {
            int code = ClientConnector.establishingConnection(new AuthInfo(login.getText(), pass.getText()),
                    address.getText(),
                    Integer.parseInt(port.getText()));

            switch (code) {
                //Authenticated, open Main Window
                case 0:
                    openMain();
                    propertiesSave();
                    break;
                case 1:
                    authError.setText("Auth error. Pls, check fields.");
                    break;
                case 2:
                    authError.setText("(!) SSL CONNECTION WRONG!");
                    break;
                default:
                    authError.setText("Check server and port.");
                    break;
            }
        }
    }

    @FXML
    private void regInit() {
        rBirthDate.setValue(LocalDate.now());
        rAddress.setText(address.getText());
        rPort.setText(port.getText());
        loginPage.setVisible(false);
        registrationPage.setVisible(true);
    }

    @FXML
    private void regCancel() {
        registrationPage.setVisible(false);
        loginPage.setVisible(true);
    }

    //Try to connect & register
    @FXML
    private void regMe() {

        //Validate fields
        if (!chkport(1))
            return;
        if (rLogin.getText().equals("") ||
                rPassword.getText().equals("") ||
                rRePassword.getText().equals("") ||
                !rPassword.getText().equals(rRePassword.getText())) {
            rErrMsg.setText("Pls, check typed values!");
        } else {
            //Convert DatePicker to Data
            Date bd = Date.from(LocalDateTime.from(
                    rBirthDate.getValue().atStartOfDay()).atZone(ZoneId.systemDefault()).toInstant());

            User userForReg = new User(rLogin.getText(), rPassword.getText(), rName.getText(),
                    rLastName.getText(), bd);

            try {
                int code = ClientConnector.establishingConnection(userForReg,
                        rAddress.getText(), Integer.parseInt(rPort.getText()));

                switch (code) {
                    //All is Ok
                    case 0:
                        openMain();
                        propertiesSave();
                        break;
                    //Not registered
                    case 1:
                        rErrMsg.setText("Something wrong. Try another values.");
                        break;
                    case 2:
                        authError.setText("(!) SSL CONNECTION WRONG!");
                        break;
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
                ClientConnector.close();
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
