package com.astrog.shootergame.client.controller;

import com.astrog.shootergame.client.lambda.StringParamLambda;
import com.astrog.shootergame.client.lambda.TryLoginLambda;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    public TextField nameField;
    public StringParamLambda showNextScene;
    public TryLoginLambda tryLogin;

    @FXML
    public void onLoginClick() {
        String name = nameField.getText();

        if (name.isBlank())
            return;

        if(tryLogin.tryLogin(name))
            showNextScene.run(name);
    }
}
