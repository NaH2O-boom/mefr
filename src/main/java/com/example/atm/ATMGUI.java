package com.example.atm;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;
import java.sql.*;

import java.sql.*;





public class ATMGUI extends Application {

    private Stage window;
    private Scene loginScene, mainScene;
    private TextField usernameInput, pinInput;
    private Label welcomeLabel, errorLabel;
    public class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message) {
            super(message);
        }

        public InsufficientFundsException() {

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
// Login Scene
        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setPadding(new Insets(25, 25, 25, 25));
        loginGrid.setStyle("-fx-background-color: #F8F8FF;");

        welcomeLabel = new Label("Welcome to the ATM!");
        welcomeLabel.setStyle("-fx-font-size: 24px;");

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 16px;");

        usernameInput = new TextField();
        usernameInput.setStyle("-fx-font-size: 16px;");

        Label pinLabel = new Label("PIN:");
        pinLabel.setStyle("-fx-font-size: 16px;");

        pinInput = new PasswordField();
        pinInput.setStyle("-fx-font-size: 16px;");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> login());
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");

        errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red;");

        loginGrid.add(welcomeLabel, 0, 0, 2, 1);
        loginGrid.add(usernameLabel, 0, 1);
        loginGrid.add(usernameInput, 1, 1);
        loginGrid.add(pinLabel, 0, 2);
        loginGrid.add(pinInput, 1, 2);
        loginGrid.add(loginButton, 1, 3);
        loginGrid.add(errorLabel, 1, 4);

        loginScene = new Scene(loginGrid, 300, 200);

// Main Scene
        GridPane mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);
        mainGrid.setPadding(new Insets(25, 25, 25, 25));
        mainGrid.setStyle("-fx-background-color: #F8F8FF;");

        Label mainLabel = new Label("Welcome to the ATM!");
        mainLabel.setStyle("-fx-font-size: 24px;");

        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setOnAction(e -> withdraw());
        withdrawButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");

        Button depositButton = new Button("Deposit");
        depositButton.setOnAction(e -> deposit());
        depositButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> logout());
        logoutButton.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-size: 16px;");

        Button showBalanceButton = new Button("Show Balance");
        showBalanceButton.setOnAction(e -> showBalance());
        showBalanceButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px;");

        mainGrid.add(mainLabel, 0, 0, 2, 1);
        mainGrid.add(withdrawButton, 0, 1);
        mainGrid.add(depositButton, 1, 1);
        mainGrid.add(logoutButton, 0, 2, 2, 1);
        mainGrid.add(showBalanceButton, 1, 2, 2, 1);

        mainScene = new Scene(mainGrid, 300, 200);


        window.setScene(loginScene);
        window.setTitle("ATM GUI");
        window.show();
    }
    private double balance = 1000.0; // initial balance

    private void updateBalance(double amount) throws InsufficientFundsException {

    }
    private Label balanceLabel = new Label("Balance: $" + balance);




    private boolean authenticateUser(String username, String pin) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/accounts";
        String dbUsername = "giganigga";
        String dbPassword = "didizangi";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT * FROM account WHERE username = ? AND pin = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, pin);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // User exists if there is a matching row in the result set
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Authentication failed in case of an exception or no matching row
    }

    private void login() {
        String username = usernameInput.getText();
        String pin = pinInput.getText();

        boolean authenticated = authenticateUser(username, pin);

        if (authenticated) {
            window.setScene(mainScene);
        } else {
            errorLabel.setText("Invalid username or PIN");
        }
    }


    private void withdraw() {
        // Create text input field for amount
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setMaxWidth(150);

        // Create buttons for clearing input, canceling withdrawal, and completing withdrawal
        Button clearButton = new Button("Clear");
        Button cancelButton = new Button("Cancel");
        Button withdrawButton = new Button("Withdraw");

        // Create numeric keypad
        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        keypad.setPadding(new Insets(20, 10, 10, 10));
        for (int i = 0; i < 10; i++) {
            int number = i;
            Button button = new Button(Integer.toString(number));
            button.setOnAction(e -> amountField.setText(amountField.getText() + number));
            keypad.add(button, i % 3, i / 3);
        }

        clearButton.setOnAction(e -> amountField.clear());

        Dialog<Boolean> withdrawalDialog = new Dialog<>(); // Create a new instance of Dialog

        cancelButton.setOnAction(e -> withdrawalDialog.setResult(false));
        withdrawButton.setOnAction(e -> {
            try {
                double withdrawalAmount = Double.parseDouble(amountField.getText());
                if (balance < withdrawalAmount) {
                    throw new InsufficientFundsException();
                }
                balance -= withdrawalAmount;

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Withdraw");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Withdrawal successful.");
                successAlert.showAndWait();
                balanceLabel.setText("Balance: $" + balance);

                withdrawalDialog.setResult(true);
            } catch (NumberFormatException ex) {
                // Display error message for invalid amount
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Withdraw");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Invalid withdrawal amount.");
                errorAlert.showAndWait();
            } catch (InsufficientFundsException ex) {
                // Display error message for insufficient funds
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Withdraw");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Insufficient funds.");
                errorAlert.showAndWait();
            }
        });

        // Create layout for withdrawal dialog
        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(20, 10, 10, 10));
        layout.add(new Label("Withdrawal amount:"), 0, 0);
        layout.add(amountField, 1, 0);
        layout.add(clearButton, 2, 0);
        layout.add(cancelButton, 0, 2);
        layout.add(withdrawButton, 1, 2);
        layout.add(keypad, 0, 1, 3, 1);

        withdrawalDialog.setTitle("Withdraw");
        withdrawalDialog.getDialogPane().setContent(layout);

        withdrawalDialog.setResultConverter(buttonType -> buttonType == ButtonType.OK);
        withdrawalDialog.showAndWait();

        if (withdrawalDialog.getResult()) {
            window.setScene(mainScene);
        }
    }



    private void showBalance() {
        Alert balanceAlert = new Alert(Alert.AlertType.INFORMATION);
        balanceAlert.setTitle("Balance");
        balanceAlert.setHeaderText(null);
        balanceAlert.setContentText("Your current balance is: $" + balance);
        balanceAlert.showAndWait();
    }


    private void deposit() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the amount to deposit:");

        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(cancelButton);

        // Create numeric keypad
        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        keypad.setPadding(new Insets(20, 10, 10, 10));
        for (int i = 0; i < 10; i++) {
            int number = i;
            Button button = new Button(Integer.toString(number));
            button.setOnAction(e -> {
                String currentAmount = dialog.getEditor().getText();
                dialog.getEditor().setText(currentAmount + number);
            });
            keypad.add(button, i % 3, i / 3);
        }

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(dialog.getEditor(), keypad);

        dialog.getDialogPane().setContent(vbox);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().equals("Cancel")) {
                // User clicked Cancel
                // Do any necessary actions or simply return
                return;
            }

            try {
                double depositAmount = Double.parseDouble(result.get());
                balance += depositAmount;
                // Display success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Deposit");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Deposit successful.");
                successAlert.showAndWait();
            } catch (NumberFormatException e) {
                // Display error message for invalid amount
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Deposit");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Invalid deposit amount.");
                errorAlert.showAndWait();
            }
        }
    }




    private void logout() {
        window.setScene(loginScene);
        usernameInput.setText("");
        pinInput.setText("");
        errorLabel.setText("");
    }
}

