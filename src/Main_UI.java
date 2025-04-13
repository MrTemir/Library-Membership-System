import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import other.Files;
import other.Cipher;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Main_UI extends Application {
    private Files fileUtils = new Files();
    private Cipher cipher = new Cipher();
    private String userFilePath = "resources/users.json";
    private String idFilePath = "resources/before.json";
    private Set<String> existingIds;
    private List<Map<String, Object>> usersData;
    private Map<String, Object> currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load existing IDs and user data
        existingIds = fileUtils.loadIdsFromFile(idFilePath);
        usersData = fileUtils.readJsonFromFile(userFilePath, List.class);
        if (usersData == null) {
            usersData = new ArrayList<>();
        }

        // Show the main menu
        showMainMenu(primaryStage);
    }
    private void Menu(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
    
        Label menuLabel = new Label("Main Menu");
        Button registerButton = new Button("Register");
        Button loginButton = new Button("Login");
        Button exitButton = new Button("Exit");
    
        registerButton.setOnAction(e -> showRegistrationForm(stage));
        loginButton.setOnAction(e -> showLoginForm(stage));
        exitButton.setOnAction(e -> stage.close());
    
        layout.getChildren().addAll(menuLabel, registerButton, loginButton, exitButton);
    
        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Library Membership Control");
        stage.show();
    }
    private void exportAccountData() {
        // Формируем имя файла: name_surname_ID.json
        String fileName = currentUser.get("name") + "_" + currentUser.get("surname") + "_" + currentUser.get("id") + ".json";
    
        try {
            // Сохраняем данные текущего пользователя в файл
            fileUtils.writeJsonToFile(fileName, currentUser);
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Account data exported to file: " + fileName);
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export account data.");
        }
    }
    private void importAccountData(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import Account Data");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
        try {
            // Читаем данные из выбранного файла
            Map<String, Object> importedAccount = fileUtils.readJsonFromFile(selectedFile.getAbsolutePath(), Map.class);

            // Проверяем, есть ли уже пользователь с таким ID
            String importedId = (String) importedAccount.get("id");
            for (Map<String, Object> user : usersData) {
                if (user.get("id").equals(importedId)) {
                    showAlert(Alert.AlertType.ERROR, "Import Failed", "An account with this ID already exists.");
                    return;
                }
            }

            // Добавляем импортированный аккаунт в список пользователей
            usersData.add(importedAccount);
            fileUtils.writeJsonToFile(userFilePath, usersData); // Сохраняем обновлённый список пользователей
            showAlert(Alert.AlertType.INFORMATION, "Import Successful", "Account imported successfully!");
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Import Failed", "Failed to import account data.");
        }
    }
}

    private void showUpdateInfoForm(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
    
        // Поля для обновления данных
        TextField nameField = new TextField((String) currentUser.get("name"));
        TextField surnameField = new TextField((String) currentUser.get("surname"));
        TextField phoneField = new TextField((String) currentUser.get("phone"));
        TextField ageField = new TextField(String.valueOf(currentUser.get("age")));
        PasswordField passwordField = new PasswordField();
    
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Age:"), 0, 3);
        grid.add(ageField, 1, 3);
        grid.add(new Label("Password (leave blank to keep current):"), 0, 4);
        grid.add(passwordField, 1, 4);
    
        Button saveButton = new Button("Save");
        Button backButton = new Button("Back");
    
        saveButton.setOnAction(e -> {
            // Обновление данных пользователя
            currentUser.put("name", nameField.getText().trim());
            currentUser.put("surname", surnameField.getText().trim());
            currentUser.put("phone", phoneField.getText().trim());
    
            String ageText = ageField.getText().trim();
            if (!ageText.isEmpty()) {
                try {
                    currentUser.put("age", Integer.parseInt(ageText));
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid age. Please enter a valid number.");
                    return;
                }
            }
    
            String password = passwordField.getText().trim();
            if (!password.isEmpty() && !cipher.isPasswordStrong(password)) {
                showAlert(Alert.AlertType.ERROR, "Weak Password", 
                    "Password must meet the following requirements:\n" +
                    "- Be at least 8 characters long.\n" +
                    "- Contain at least one digit (0-9).\n" +
                    "- Contain at least one uppercase letter (A-R).\n" +
                    "- Contain at least one special character (e.g., !@#$%^&*()_+-=[]{}|;:'\",.<>?/).");
                return;
            }

            // Сохранение обновлённых данных
            try {
                fileUtils.writeJsonToFile(userFilePath, usersData);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Information updated successfully!");
                Menu(stage);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update information.");
            }
        });
    
        backButton.setOnAction(e -> showUserMenu(stage));
    
        grid.add(saveButton, 0, 5);
        grid.add(backButton, 1, 5);
    
        Scene scene = new Scene(grid, 400, 400);
        stage.setTitle("Update Information");
        stage.setScene(scene);
        stage.show();
    }

    private void showMainMenu(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome to the Library Membership Control!");
        Button registerButton = new Button("Register");
        Button loginButton = new Button("Login");
        Button exitButton = new Button("Exit");

        registerButton.setOnAction(e -> showRegistrationForm(stage));
        loginButton.setOnAction(e -> showLoginForm(stage));
        exitButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(welcomeLabel, registerButton, loginButton, exitButton);

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Library Membership Control");
        stage.setScene(scene);
        stage.show();
    }

    private void showRegistrationForm(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
    
        TextField nameField = new TextField();
        TextField surnameField = new TextField();
        TextField phoneField = new TextField();
        TextField ageField = new TextField();
        PasswordField passwordField = new PasswordField();
    
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Age:"), 0, 3);
        grid.add(ageField, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passwordField, 1, 4);
    
        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");
    
        registerButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String phone = phoneField.getText().trim();
            String ageText = ageField.getText().trim();
            String password = passwordField.getText().trim();
    
            if (name.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Name and Password cannot be empty!");
                return;
            }
    
            if (!cipher.isPasswordStrong(password)) {
                showAlert(Alert.AlertType.ERROR, "Weak Password", 
                    "Password must meet the following requirements:\n" +
                    "- Be at least 8 characters long.\n" +
                    "- Contain at least one digit (0-9).\n" +
                    "- Contain at least one uppercase letter (A-R).\n" +
                    "- Contain at least one special character (e.g., !@#$%^&*()_+-=[]{}|;:'\",.<>?/).");
                return;
            }
    
            int age = 0;
            if (!ageText.isEmpty()) {
                try {
                    age = Integer.parseInt(ageText);
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid age. Please enter a valid number.");
                    return;
                }
            }
    
            // Generate a new ID
            String userId;
            do {
                userId = cipher.generateMemberId();
            } while (existingIds.contains(userId));
            existingIds.add(userId);
    
            // Determine if the user is an administrator
            boolean isAdmin = usersData.isEmpty(); // The first user becomes an administrator
    
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("id", userId);
            newUser.put("name", name);
            newUser.put("surname", surname);
            newUser.put("phone", phone);
            newUser.put("age", age);
    
            try {
                newUser.put("password", cipher.hashWithSHA256(password)); // Hash the password
            } catch (NoSuchAlgorithmException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error hashing password.");
                return;
            }
    
            newUser.put("isAdmin", isAdmin);
            usersData.add(newUser);
    
            try {
                fileUtils.writeJsonToFile(userFilePath, usersData); // Save users
                fileUtils.saveIdsToFile(idFilePath, existingIds); // Save IDs
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error saving user data.");
                return;
            }
    
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Your ID: " + userId);
            currentUser = newUser;
            showUserMenu(stage);
        });
    
        backButton.setOnAction(e -> showMainMenu(stage));
    
        grid.add(registerButton, 0, 5);
        grid.add(backButton, 1, 5);
    
        Scene scene = new Scene(grid, 400, 400);
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }

    private void showLoginForm(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField idField = new TextField();
        PasswordField passwordField = new PasswordField();

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back");

        loginButton.setOnAction(e -> {
            String id = idField.getText().trim();
            String password = passwordField.getText().trim();

            for (Map<String, Object> user : usersData) {
                try {
                    if (user.get("id").equals(id) && user.get("password").equals(cipher.hashWithSHA256(password))) {
                        currentUser = user;
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful! Welcome, " + user.get("name"));
                        showUserMenu(stage);
                        return;
                    }
                } catch (NoSuchAlgorithmException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Error hashing password.");
                    return;
                }
            }

            showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID or password.");
        });

        backButton.setOnAction(e -> showMainMenu(stage));

        grid.add(loginButton, 0, 2);
        grid.add(backButton, 1, 2);

        Scene scene = new Scene(grid, 400, 300);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    private void showUserMenu(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
    
        Label welcomeLabel = new Label("Welcome, " + currentUser.get("name") + "!");
        Button updateInfoButton = new Button("Update Information");
        Button viewInfoButton = new Button("View Information");
        Button deleteAccountButton = new Button("Delete Account");
        Button logoutButton = new Button("Logout");
    
        // Кнопки "Экспорт" и "Импорт" меньшего размера
        Button exportAccountButton = new Button("Export");
        Button importAccountButton = new Button("Import");
        exportAccountButton.setPrefWidth(80);
        importAccountButton.setPrefWidth(80);
    
        // Обработчики для кнопок
        exportAccountButton.setOnAction(e -> exportAccountData());
        importAccountButton.setOnAction(e -> importAccountData(stage));
    
        // Проверяем, является ли пользователь администратором
        boolean isAdmin = (boolean) currentUser.get("isAdmin");
        Button addUserButton = null;
        Button viewAllUsersButton = null;
        Button deleteUserButton = null;
    
        if (isAdmin) {
            addUserButton = new Button("Add New User");
            viewAllUsersButton = new Button("View All Users");
            deleteUserButton = new Button("Delete User");
    
            addUserButton.setOnAction(e -> showAddUserForm(stage));
            viewAllUsersButton.setOnAction(e -> showAllUsers(stage));
            deleteUserButton.setOnAction(e -> showDeleteUserForm(stage));
        }
    
        updateInfoButton.setOnAction(e -> showUpdateInfoForm(stage));
        viewInfoButton.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Your Info", currentUser.toString()));
        deleteAccountButton.setOnAction(e -> {
            usersData.remove(currentUser);
            try {
                fileUtils.writeJsonToFile(userFilePath, usersData);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error deleting account.");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account deleted successfully.");
            currentUser = null;
            showMainMenu(stage);
        });
    
        logoutButton.setOnAction(e -> {
            currentUser = null;
            showMainMenu(stage);
        });
    
        // Добавляем элементы в интерфейс
        root.getChildren().addAll(welcomeLabel, updateInfoButton, viewInfoButton, deleteAccountButton, logoutButton);
    
        // Добавляем кнопки "Экспорт" и "Импорт" в отдельный контейнер
        HBox exportImportBox = new HBox(10, exportAccountButton, importAccountButton);
        root.getChildren().add(exportImportBox);
    
        if (isAdmin) {
            root.getChildren().addAll(addUserButton, viewAllUsersButton, deleteUserButton);
        }
    
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("User Menu");
        stage.setScene(scene);
        stage.show();
    }

    private void showAddUserForm(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField surnameField = new TextField();
        TextField phoneField = new TextField();
        TextField ageField = new TextField();
        PasswordField passwordField = new PasswordField();
        CheckBox isAdminCheckBox = new CheckBox("Is Admin");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Age:"), 0, 3);
        grid.add(ageField, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passwordField, 1, 4);
        grid.add(isAdminCheckBox, 1, 5);

        Button saveButton = new Button("Save");
        Button backButton = new Button("Back");

        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String phone = phoneField.getText().trim();
            String ageText = ageField.getText().trim();
            String password = passwordField.getText().trim();
            boolean isAdmin = isAdminCheckBox.isSelected();

            if (name.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Name and Password cannot be empty!");
                return;
            }

            int age = 0;
            if (!ageText.isEmpty()) {
                try {
                    age = Integer.parseInt(ageText);
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid age. Please enter a valid number.");
                    return;
                }
            }

            // Generate a new ID
            String userId;
            do {
                userId = cipher.generateMemberId();
            } while (existingIds.contains(userId));
            existingIds.add(userId);

            Map<String, Object> newUser = new HashMap<>();
            newUser.put("id", userId);
            newUser.put("name", name);
            newUser.put("surname", surname);
            newUser.put("phone", phone);
            newUser.put("age", age);

            try {
                newUser.put("password", cipher.hashWithSHA256(password)); // Hash the password
            } catch (NoSuchAlgorithmException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error hashing password.");
                return;
            }

            newUser.put("isAdmin", isAdmin);
            usersData.add(newUser);

            try {
                fileUtils.writeJsonToFile(userFilePath, usersData); // Save users
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error saving user data.");
                return;
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "New user added successfully! User ID: " + userId);
            showUserMenu(stage);
        });

        backButton.setOnAction(e -> showUserMenu(stage));

        grid.add(saveButton, 0, 6);
        grid.add(backButton, 1, 6);

        Scene scene = new Scene(grid, 400, 400);
        stage.setTitle("Add New User");
        stage.setScene(scene);
        stage.show();
    }

    private void showAllUsers(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("All Users:");
        root.getChildren().add(titleLabel);

        if (usersData.isEmpty()) {
            root.getChildren().add(new Label("No users found."));
        } else {
            for (Map<String, Object> user : usersData) {
                Label userLabel = new Label("ID: " + user.get("id") + ", Name: " + user.get("name") + ", Admin: " + user.get("isAdmin"));
                root.getChildren().add(userLabel);
            }
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showUserMenu(stage));
        root.getChildren().add(backButton);

        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("View All Users");
        stage.setScene(scene);
        stage.show();
    }

    private void showDeleteUserForm(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField userIdField = new TextField();
        grid.add(new Label("Enter User ID to Delete:"), 0, 0);
        grid.add(userIdField, 1, 0);

        Button deleteButton = new Button("Delete");
        Button backButton = new Button("Back");

        deleteButton.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            Map<String, Object> userToDelete = null;

            for (Map<String, Object> user : usersData) {
                if (user.get("id").equals(userId)) {
                    userToDelete = user;
                    break;
                }
            }

            if (userToDelete != null) {
                usersData.remove(userToDelete);
                try {
                    fileUtils.writeJsonToFile(userFilePath, usersData); // Save changes
                } catch (IOException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Error deleting user.");
                    return;
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "User not found.");
            }

            showUserMenu(stage);
        });

        backButton.setOnAction(e -> showUserMenu(stage));

        grid.add(deleteButton, 0, 1);
        grid.add(backButton, 1, 1);

        Scene scene = new Scene(grid, 400, 200);
        stage.setTitle("Delete User");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}