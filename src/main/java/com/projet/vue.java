package com.projet;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.property.SimpleObjectProperty;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import com.mongodb.client.FindIterable;
import java.util.Optional;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.util.HashMap;
public class vue extends Application {
    private TextArea chatArea;
    private TextField inputField;
    private BorderPane mainLayout;
    private TableView<Map<String, Object>> tableView = new TableView<>();

    private controller Controller; // Your controller instance
    private String userId = "1";
    private VBox chatContainer;
    private TextArea questionTextArea;
    private static model Model; // Static field to store the model
    public static void setModel(model model) {
        Model = model;
    }
    private TableView<Document> productTable;
    private static final List<String> SKIN_TYPES = Arrays.asList("oily", "dry", "combination", "sensitive", "normal");
    private static final List<String> PRICE_TYPES = Arrays.asList("under","max","maximux", "below", "inferior than");
    @Override
    public void init() {
        mainLayout = new BorderPane();

        // Initialize the controller and associate it with this instance
        Controller = new controller(Model, this);
        setController(Controller);
    }

    @Override
    public void start(Stage primaryStage) {
        // Root pane with gradient background
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #8E44AD, #3498DB);");

        // Left Side: Image and Cloud Section
        VBox leftPane = new VBox();
        leftPane.setPrefWidth(400);
        leftPane.setStyle("-fx-background-color: white; -fx-background-radius: 20 0 0 20;");
        leftPane.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView(new Image("file:/home/chaimae/my-app/src/main/java/com/projet/chatbots.jpeg"));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        Label slogan = new Label("Chat smarter with Giga Girls!");
        slogan.setStyle("-fx-font-size: 14px; -fx-text-fill: #5B2C6F;");

        leftPane.getChildren().addAll(logo, slogan);

        // Right Side: Login Form
        VBox rightPane = new VBox(15);
        rightPane.setPadding(new Insets(40));
        rightPane.setStyle("-fx-background-color: white; -fx-background-radius: 0 20 20 0;");
        rightPane.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("Welcome Back!");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: #333333; -fx-font-weight: bold;");

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        VBox titleBox = new VBox(5, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);

        // Username field with clear light border
        TextField usernameField = new TextField();
        usernameField.setPromptText("E-mail");
        usernameField.setStyle("-fx-background-color: #F4F4F4; -fx-background-radius: 20; -fx-pref-height: 40px; -fx-border-color: #A6A6A6; -fx-border-width: 1px;");

        // Password field with clear light border
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-color: #F4F4F4; -fx-background-radius: 20; -fx-pref-height: 40px; -fx-border-color: #A6A6A6; -fx-border-width: 1px;");

        // Create a TextField for showing the password when "Show Password" is checked
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setStyle("-fx-background-color: #F4F4F4; -fx-background-radius: 20; -fx-pref-height: 40px; -fx-border-color: #A6A6A6; -fx-border-width: 1px;");

        // Toggle visibility between PasswordField and TextField
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        showPasswordCheckBox.setOnAction(event -> {
            if (showPasswordCheckBox.isSelected()) {
                visiblePasswordField.setText(passwordField.getText());
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
            }
        });

        // Group password field and show password checkbox together
        VBox passwordBox = new VBox(5, passwordField, visiblePasswordField, showPasswordCheckBox);
        passwordBox.setAlignment(Pos.CENTER_LEFT);

        // Forgot password link
        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setStyle("-fx-text-fill: #8E44AD; -fx-font-size: 12px; -fx-underline: false;");
        forgotPassword.setOnAction(e -> System.out.println("Forgot Password clicked"));

        // Login Button with fixed size on hover
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #8E44AD; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 14px; -fx-pref-width: 250px;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #5B2C6F; -fx-text-fill: white; -fx-background-radius: 20; -fx-pref-width: 250px;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #8E44AD; -fx-text-fill: white; -fx-background-radius: 20; -fx-pref-width: 250px;"));

        // Create Account Link (instead of sign-up button)
        Label createAccountText = new Label("Don't have an account?");
        createAccountText.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

        Hyperlink createAccountLink = new Hyperlink("Create Account");
        createAccountLink.setStyle("-fx-text-fill: #8E44AD; -fx-font-size: 12px; -fx-underline: false;");
        createAccountLink.setOnAction(e -> openSignUpWindow(primaryStage));

        HBox createAccountBox = new HBox(5, createAccountText, createAccountLink);
        createAccountBox.setAlignment(Pos.CENTER);

        // Add components to right pane
        rightPane.getChildren().addAll(titleBox, usernameField, passwordBox, forgotPassword, loginButton, createAccountBox);

        // Combine both panes
        HBox content = new HBox(leftPane, rightPane);
        content.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        content.setMaxSize(800, 400);

        root.setCenter(content);

        // Footer
        Label footer = new Label("© 2024 Giga Girls Chatbot. All rights reserved.");
        footer.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        footer.setAlignment(Pos.CENTER);

        VBox layout = new VBox(content, footer);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        root.setCenter(layout);

        // Scene Configuration
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Login Form");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Login button action
        loginButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Login Failed", "Missing Credentials", "Please enter both username and password.");
            } else {
                Document user = Model.findUserByUsernameAndPassword(username, password);
                Document role = Model.retreiveRole(username, password);
                if (user != null) {
                    String rolee = role.getString("role");
                    this.userId = user.getString("userId");

                    if ("admin".equalsIgnoreCase(rolee)) {
                        showScreenAdmin(primaryStage);
                    } else {
                        showChatScreen(primaryStage);
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Credentials", "The username or password you entered is incorrect.");
                }
            }
        });
    }
    // Utility method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    // Method to open Sign Up window
    private void openSignUpWindow(Stage primaryStage) {
        // Create a new window for signing up
        Stage signUpWindow = new Stage();
        signUpWindow.setTitle("Sign Up - Giga Girls Chatbot");

        // Create a layout for the sign-up window
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1abc9c, #16a085);");

        // Create a card-like form layout
        VBox formLayout = new VBox(20);
        formLayout.setStyle("""
        -fx-background-color: white; 
        -fx-padding: 90; 
        -fx-border-radius: 15; 
        -fx-background-radius: 15;
        -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 5);
    """);
        formLayout.setAlignment(Pos.CENTER);

        // Title for the form
        Label formTitle = new Label("Create Your Account");
        formTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your Username");
        usernameField.setStyle("""
        -fx-pref-width: 300px; 
        -fx-font-size: 14px; 
        -fx-padding: 5; 
        -fx-border-color: #bdc3c7; 
        -fx-border-radius: 5;
        -fx-background-radius: 5;
    """);

        VBox usernameBox = new VBox(5, usernameLabel, usernameField);
        usernameBox.setAlignment(Pos.CENTER);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your Password");
        passwordField.setStyle(usernameField.getStyle());

        TextField passwordTextField = new TextField();
        passwordTextField.setPromptText("Enter your Password");
        passwordTextField.setStyle(usernameField.getStyle());

        StackPane passwordStack = new StackPane(passwordField, passwordTextField);
        passwordTextField.setVisible(false);

        VBox passwordBox = new VBox(5, passwordLabel, passwordStack);
        passwordBox.setAlignment(Pos.CENTER);

        // Confirm Password field
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter your Password");
        confirmPasswordField.setStyle(usernameField.getStyle());

        TextField confirmPasswordTextField = new TextField();
        confirmPasswordTextField.setPromptText("Re-enter your Password");
        confirmPasswordTextField.setStyle(usernameField.getStyle());

        StackPane confirmPasswordStack = new StackPane(confirmPasswordField, confirmPasswordTextField);
        confirmPasswordTextField.setVisible(false);

        VBox confirmPasswordBox = new VBox(5, confirmPasswordLabel, confirmPasswordStack);
        confirmPasswordBox.setAlignment(Pos.CENTER);

        // Show Password Checkbox
        CheckBox showPasswordCheckBox = new CheckBox("Show Passwords");
        showPasswordCheckBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        showPasswordCheckBox.setOnAction(event -> {
            boolean isSelected = showPasswordCheckBox.isSelected();
            passwordField.setVisible(!isSelected);
            passwordTextField.setVisible(isSelected);
            confirmPasswordField.setVisible(!isSelected);
            confirmPasswordTextField.setVisible(isSelected);
        });

        // Bind text between password fields and text fields
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        // Buttons: Sign Up and Back
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("""
        -fx-background-color: #27ae60; 
        -fx-text-fill: white; 
        -fx-font-size: 14px; 
        -fx-padding: 10 20; 
        -fx-border-radius: 10; 
        -fx-background-radius: 10;
    """);

        Button backButton = new Button("Back");
        backButton.setStyle("""
        -fx-background-color: #e74c3c; 
        -fx-text-fill: white; 
        -fx-font-size: 14px; 
        -fx-padding: 10 20; 
        -fx-border-radius: 10; 
        -fx-background-radius: 10;
    """);

        // Handle sign-up logic
        signUpButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String password = showPasswordCheckBox.isSelected() ? passwordTextField.getText().trim() : passwordField.getText().trim();
            String confirmPassword = showPasswordCheckBox.isSelected() ? confirmPasswordTextField.getText().trim() : confirmPasswordField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all fields.");
            } else if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Password Mismatch", "The passwords do not match. Please try again.");
            } else {
                handleSignUp(usernameField, passwordField, confirmPasswordField); // Pass all fields
                signUpWindow.close();
            }
        });

        // Back button action to close the sign-up window
        backButton.setOnAction(event -> signUpWindow.close());

        // Button layout
        HBox buttonLayout = new HBox(15, backButton, signUpButton);
        buttonLayout.setAlignment(Pos.CENTER);

        // Add all components to the form layout
        formLayout.getChildren().addAll(formTitle, usernameBox, passwordBox, confirmPasswordBox, showPasswordCheckBox, buttonLayout);

        // Center the form layout
        root.setCenter(formLayout);

        // Configure and show the sign-up scene
        Scene signUpScene = new Scene(root, 600, 500);
        signUpWindow.setScene(signUpScene);
        signUpWindow.show();
    }


    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void showScreenAdmin(Stage primaryStage) {
        mainLayout = new BorderPane();

        mainLayout.setTop(header(primaryStage)); // Set the header with updated gradient style
        mainLayout.setLeft(sidebar()); // Set the sidebar with updated gradient style

        // Welcome message with updated styling
        Label welcomeLabel = new Label("Welcome to your Admin Dashboard.");
        welcomeLabel.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #2c3e50; " + // Dark grayish blue
                        "-fx-background-color: #ecf0f1; " + // Light gray background
                        "-fx-padding: 20px; " +
                        "-fx-background-radius: 10px; " // Rounded corners for background
        );
        welcomeLabel.setAlignment(Pos.CENTER);

        mainLayout.setCenter(welcomeLabel);

        Scene scene = new Scene(mainLayout, 800, 600);
        scene.getRoot().setStyle(
                "-fx-background-color: #f4f6f9;" // Light background for the main layout
        );

        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();
    }

    public VBox header(Stage primaryStage) {
        VBox header = new VBox();
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #9b59b6, #3498db); " + // Gradient from purple to blue
                        "-fx-padding: 20px; " +
                        "-fx-background-radius: 0 0 0 0;" // Rounded corners at the top
        );

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);

        // Title Label for the header
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setStyle(
                "-fx-font-size: 22px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white;"
        );

        // Logout button with a purple color and sharp corners
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-background-color: #9b59b6; " + // Purple background for logout
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 0; " + // No rounded corners
                        "-fx-padding: 10px 20px; " +
                        "-fx-font-size: 14px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 2);" // Subtle shadow
        );
        logoutButton.setOnAction(e -> handleLogout(primaryStage));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(titleLabel, spacer, logoutButton);

        // Refined search bar with improved design
        TextField searchField = new TextField();
        searchField.setPromptText("Search by product name...");
        searchField.setPrefWidth(350);
        searchField.setStyle(
                "-fx-background-color: #ffffff; " + // White background for search field
                        "-fx-border-radius: 30px; " +       // More rounded corners
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 12px 20px; " +
                        "-fx-border: 2px solid #3498db; " + // Matching border color
                        "-fx-focus-color: #3498db; " + // Focus color for the search bar
                        "-fx-text-fill: #2c3e50; "  // Dark text for contrast
        );

        Button searchButton = new Button("Search");
        searchButton.setStyle(
                "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 30px; " + // Rounded corners for button
                        "-fx-padding: 12px 25px; " +       // Padding for better button feel
                        "-fx-font-size: 16px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 3);" // Shadow effect
        );
        searchButton.setOnAction(e -> handleSearch(searchField.getText()));

        HBox searchBar = new HBox(20, searchField, searchButton);
        searchBar.setAlignment(Pos.CENTER);

        header.getChildren().addAll(topBar, searchBar);

        return header;
    }

    public VBox sidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setStyle(
                "-fx-background-color: linear-gradient(to right, #9b59b6, #3498db); " + // Gradient from purple to blue
                        "-fx-padding: 25px; " +
                        "-fx-spacing: 20px; " +
                        "-fx-background-radius: 0 0 0 0;" // Rounded corner for the sidebar
        );
        sidebar.setAlignment(Pos.TOP_CENTER);

        // Sidebar buttons with a sleek design
        Button addProductButton = createSidebarButton("Add Product");
        addProductButton.setOnAction(e -> showtheAddForm());

        Button showProductsButton = createSidebarButton("Show Products");
        showProductsButton.setOnAction(e -> showthetable());

        sidebar.getChildren().addAll(addProductButton, showProductsButton);

        return sidebar;
    }



    // Helper method for creating sidebar buttons
    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #2980b9; " + // Slightly darker blue for the button
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 30px; " + // Rounded corners for sidebar buttons
                        "-fx-padding: 12px 25px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 2);" // Shadow effect for the buttons
        );
        button.setPrefWidth(250);
        return button;
    }
    private void handleLogout(Stage primaryStage) {
        System.out.println("Logging out...");

        // Create an alert to confirm logout
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        // Handle user response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user clicks OK, show login screen
            showLoginScreen(primaryStage);
        }
    }

    private void handleSearch(String productName) {
        if (productName == null || productName.isBlank()) {
            showAlert("Search Error", "Please enter a valid product name.");
            return;
        }


        // Perform the product search (assuming 'controller.findProductByName' returns a list or Document of results)
        Document products = Model.findProductByName(productName);

        if (products != null) {
            showSearchResults(products);
        } else {
            // If no product is found, show a "No results found" message on the UI
            showNoResultsMessage(productName);
        }
    }
    public void showtheAddForm() {
        VBox addform = new VBox(20); // Increased spacing for a more spacious layout
        addform.setPadding(new Insets(30)); // More padding for a cleaner look
        addform.setStyle(
                "-fx-background-color: #f8f9fa; " + // Light grey background for a modern, professional look
                        "-fx-border-width: 0px; " + // Outer border invisible
                        "-fx-border-radius: 12px; " + // Rounded corners for a modern, soft feel
                        "-fx-background-radius: 12px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 15, 0, 0, 10); " + // Subtle shadow for depth
                        "-fx-alignment: center;"
        );

        // Form title


        // Name input
        Label nameLabel = new Label("Enter the new product name:");
        nameLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 16px;");
        TextField nameField = new TextField();
        nameField.setMaxWidth(450); // Increased width for a more spacious input field
        nameField.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #BDC3C7; " + // Lighter border for a subtle effect
                        "-fx-border-width: 1px; " + // Lighter border width
                        "-fx-border-radius: 8px; " + // Rounded corners for consistency
                        "-fx-background-radius: 8px; " +
                        "-fx-font-size: 16px; " + // Larger font for better readability
                        "-fx-padding: 10px 15px;"
        );

        // Description input
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 16px;");
        TextField descriptionField = new TextField();
        descriptionField.setMaxWidth(450); // Increased width for consistency
        descriptionField.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #BDC3C7; " + // Lighter border for a subtle effect
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10px 15px;"
        );
        descriptionField.setPrefHeight(200);
        // Price input
        Label priceLabel = new Label("Price:");
        priceLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 16px;");
        TextField priceField = new TextField();
        priceField.setMaxWidth(450); // Increased width for consistency
        priceField.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #BDC3C7; " + // Lighter border for a subtle effect
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10px 15px;"
        );

        // Confirm button
        Button confirmAdd = new Button("Confirm");
        confirmAdd.setStyle(
                "-fx-background-color: #4caf50; " + // Professional green for positive actions
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 15px 30px; " + // Larger padding for a more prominent button
                        "-fx-border-radius: 12px; " + // Rounded button for consistency with form elements
                        "-fx-background-radius: 12px; " +
                        "-fx-font-size: 16px;"
        );
        confirmAdd.setOnAction(e -> {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String priceText = priceField.getText().trim();

            // Input validation
            if (name.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
                showAlert("Error", "All fields must be filled!", Alert.AlertType.ERROR);
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                if (price < 0) {
                    showAlert("Error", "Price must be a positive number!", Alert.AlertType.ERROR);
                    return;
                }

                // Generate a unique ID for the product (or use another method if needed)
                int id = UUID.randomUUID().toString().hashCode();

                // Add the product directly to the database (or your data layer)
                Model.addProduct(id, name, description, price);

                showSuccessAlert("Success", "Product added successfully!");

                // Refresh the product list and clear the form
                showAllProducts();
                nameField.clear();
                descriptionField.clear();
                priceField.clear();

            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number for price!", Alert.AlertType.ERROR);
            }
        });

        // Add components to the form layout
        addform.getChildren().addAll(
                //titleform,
                nameLabel, nameField,
                descriptionLabel, descriptionField,
                priceLabel, priceField,
                confirmAdd
        );

        // Set the form in the main layout
        mainLayout.setCenter(addform);
    }


    private void showAllProducts() {
        // Reload the product data into the table
        showthetable();
    }
    public void showSearchResults(Document document) {
        VBox tableLayout = new VBox(10);
        tableLayout.setPadding(new Insets(20));
        tableLayout.setStyle("-fx-background-color:  #dfcbe8 ; -fx-border-radius: 10px; -fx-alignment: center;");

        // Create the TableView
        TableView<Map<String, Object>> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #f06292;");

        // Define columns for the TableView
        TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().get("name"))));

        TableColumn<Map<String, Object>, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().get("description"))));

        TableColumn<Map<String, Object>, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(Double.parseDouble(String.valueOf(cellData.getValue().get("price")))));

        // Define an action column with "Delete" and "Update" buttons
        TableColumn<Map<String, Object>, Void> actionColumn = new TableColumn<>("Actions");

        actionColumn.setCellFactory(col -> new TableCell<Map<String, Object>, Void>() {
            private final Button deleteButton = new Button("Delete");
            private final Button updateButton = new Button("Update");

            {
                deleteButton.setStyle("-fx-background-color: #f06292; -fx-text-fill: white;");
                updateButton.setStyle("-fx-background-color: #80e27e; -fx-text-fill: white;");

                // Delete Button Action
                // Delete Button Action
                deleteButton.setOnAction(event -> {
                    Map<String, Object> rowData = getTableView().getItems().get(getIndex());
                    int productId = (int) rowData.get("productId"); // Retrieve the productId
                    System.out.println("deleting"+productId);
                    if (productId != -1) { // Check if the productId is valid
                        if (showDeleteConfirmationDialog(rowData)) {
                            controller.deleteProducts(rowData); // Call delete method with productId
                            getTableView().getItems().remove(rowData); // Remove the row from the TableView
                        }
                    } else {
                        showAlert("Error", "Unable to delete. Product ID is invalid.");
                    }
                });


                // Update Button Action
                updateButton.setOnAction(event -> {
                    Map<String, Object> rowData = getTableView().getItems().get(getIndex());
                    String productName = (String) rowData.get("name");
                    int productId = (int) rowData.get("productId"); // Fetching productId directly from the rowData
                    String description = (String) rowData.get("description");
                    double price = Double.parseDouble(rowData.get("price").toString());

                    // Check if productId is valid before updating
                    if (productId != -1) {
                        Model.updateProduct(productId, productName, description, price);
                        showUpdateeForm(rowData);
                    } else {
                        showAlert("Error", "Product not found in the database.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Add the buttons to the row if not empty
                    HBox buttonBox = new HBox(10, updateButton, deleteButton);
                    buttonBox.setSpacing(5);
                    setGraphic(buttonBox); // Add the buttons to the cell
                }
            }
        });

        tableView.getColumns().addAll(nameColumn, descriptionColumn, priceColumn, actionColumn);

        // Convert the Document to a Map
        Map<String, Object> rowData = new HashMap<>();
        if (document != null) {
            rowData.put("name", document.getString("name"));
            rowData.put("description", document.getString("description"));
            rowData.put("price", document.getDouble("price"));
            rowData.put("productId", document.getInteger("id", -1)); // Default to -1 if "id" doesn't exist
        }

        // Create an ObservableList with only this single row
        ObservableList<Map<String, Object>> observableData = FXCollections.observableArrayList(rowData);

        // Update the TableView with this single row
        Platform.runLater(() -> {
            tableView.setItems(observableData);
        });

        // Add the TableView to the layout
        tableLayout.getChildren().add(tableView);
        mainLayout.setCenter(tableLayout);
    }


    public void loadDocumentData(TableView<Map<String, Object>> tableView) {
        // Fetch all products from MongoDB using the Model
        List<Document> products = Model.getAllProducts(); // Assuming this method fetches the products

        // Convert List<Document> to ObservableList<Map<String, Object>> for the TableView
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();

        for (Document product : products) {
            // Convert each Document to a Map<String, Object>
            Map<String, Object> rowData = new HashMap<>();
            rowData.put("productId", product.getInteger("id"));  // Example: Assuming the product document contains an "id" field
            rowData.put("productName", product.getString("name")); // Example: Assuming the document contains a "name" field
            rowData.put("price", product.getDouble("price"));
            rowData.put("description", product.getString("description"));// Example: Assuming the document contains a "price" field
            // Add any other fields you want to display in the table
            data.add(rowData);
        }

        // Set the data to the table view
        tableView.setItems(data);
    }
    private void showUpdateeForm(Map<String, Object> productData) {
        VBox updateForm = new VBox(20); // Increased spacing for better readability
        updateForm.setPadding(new Insets(30));
        updateForm.setAlignment(Pos.CENTER);
        updateForm.setStyle(
                "-fx-background-color: #f5f5f5; " + // Light background for a modern look
                        "-fx-border-radius: 12px; " + // Rounded corners for a soft appearance
                        "-fx-background-radius: 12px; " +
                        "-fx-border-color: #BDC3C7; " + // Subtle light border for a clean look
                        "-fx-border-width: 1px; " + // Thin border for a modern touch
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 15, 0, 0, 10); " // Subtle shadow for depth
        );

        // Styled text fields pre-populated with the existing data
        TextField productNameField = new TextField(String.valueOf(productData.get("name")));
        productNameField.setPromptText("Enter product name");
        productNameField.setStyle(
                "-fx-padding: 10px; " +
                        "-fx-border-color: #cccccc; " + // Lighter border for consistency
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: normal; " +
                        "-fx-background-color: #ffffff;"
        );
        productNameField.setMaxWidth(450);

        // Use TextArea for description to support multiline text
        TextArea descriptionField = new TextArea(String.valueOf(productData.get("description")));
        descriptionField.setPromptText("Enter description");
        descriptionField.setWrapText(true); // Enable text wrapping
        descriptionField.setStyle(
                "-fx-padding: 10px; " +
                        "-fx-border-color: #cccccc; " + // Lighter border for consistency
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: normal; " +
                        "-fx-background-color: #ffffff;"
        );
        descriptionField.setMaxWidth(450);
        descriptionField.setPrefHeight(100); // Adjust height for multiline input

        TextField priceField = new TextField(String.valueOf(productData.get("price")));
        priceField.setPromptText("Enter price");
        priceField.setStyle(
                "-fx-padding: 10px; " +
                        "-fx-border-color: #cccccc; " + // Lighter border for consistency
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: normal; " +
                        "-fx-background-color: #ffffff;"
        );
        priceField.setMaxWidth(450);
        // Fetch the productId before the event listener (use the current product name)
        String productName = String.valueOf(productData.get("name"));
        int productId = Model.fetchProductIdByName(productName);

        // Make sure the productId was found before proceeding
        if (productId == -1) {
            showAlert("Error", "Product ID not found for the given name.");
            return;
        }

        // Update Button
        Button updateButton = new Button("Update");
        updateButton.setStyle(
                "-fx-background-color: #80e27e; " + // Green for positive actions
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12px 25px; " + // Slightly smaller padding for a more compact button
                        "-fx-border-radius: 8px; " + // Consistent with text fields
                        "-fx-background-radius: 8px; " +
                        "-fx-font-size: 16px;"
        );
        updateButton.setOnMouseEntered(e -> updateButton.setStyle(
                "-fx-background-color: #388e3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12px 25px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;"
        ));
        updateButton.setOnMouseExited(e -> updateButton.setStyle(
                "-fx-background-color: #80e27e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12px 25px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;"
        ));
        updateButton.setPrefWidth(150);

        updateButton.setOnAction(e -> {
            try {
                // Collect updated values from the form
                String updatedName = productNameField.getText();
                String updatedDescription = descriptionField.getText();
                double updatedPrice = Double.parseDouble(priceField.getText());

                // Call the updateProduct method from the Model class with the fetched productId
                Model.updateProduct(productId, updatedName, updatedDescription, updatedPrice);

                // Show success alert and reload table data
                showSuccessAlert("Success", "Product updated!");
                showthetable();

            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid price.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });

        // Cancel Button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle(
                "-fx-background-color: #f06292; " + // Red for cancel actions
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12px 25px; " + // Slightly smaller padding for a more compact button
                        "-fx-border-radius: 8px; " + // Consistent with text fields
                        "-fx-background-radius: 8px; " +
                        "-fx-font-size: 16px;"
        );
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(
                "-fx-background-color: #d81b60; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12px 25px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;"
        ));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(
                "-fx-background-color: #f06292; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12px 25px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;"
        ));
        cancelButton.setPrefWidth(150);
        cancelButton.setOnAction(e -> showthetable());

        // Add all components to the form
        updateForm.getChildren().addAll(
                createStyledLabel("Product Name:"), productNameField,
                createStyledLabel("Description:"), descriptionField,
                createStyledLabel("Price:"), priceField,
                updateButton, cancelButton
        );

        // Display the update form in the main layout
        mainLayout.setCenter(updateForm);
    }

    // Helper method to create styled labels
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-weight: bold;");
        return label;
    }


    public void showSuccessAlert(String title, String message) {
        Stage successStage = new Stage();
        successStage.setTitle(title);
        successStage.initModality(Modality.APPLICATION_MODAL);
        successStage.setResizable(false);

        // Layout for the dialog
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Success Icon (Unicode checkmark ✔️)
        Label iconLabel = new Label("\u2714"); // Unicode checkmark
        iconLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: #4caf50;");

        // Message Text
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

        // OK Button
        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 15px;");
        okButton.setOnAction(e -> successStage.close());

        // Add elements to the layout
        layout.getChildren().addAll(iconLabel, messageLabel, okButton);

        // Scene and Stage
        Scene scene = new Scene(layout, 300, 200);
        successStage.setScene(scene);
        successStage.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean showDeleteConfirmationDialog(Map<String, Object> productData) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");

        // Assuming productData contains the product name
        String productName = (String) productData.get("name");
        alert.setHeaderText("Are you sure you want to delete " + productName + "?");

        ButtonType confirmButton = new ButtonType("Yes");
        ButtonType cancelButton = new ButtonType("No");

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == confirmButton;
    }
    public void showthetable() {
        VBox tableLayout = new VBox(10);
        tableLayout.setPadding(new Insets(20));
        tableLayout.setStyle("-fx-background-color: #f5f5f5; -fx-border-radius: 10px; -fx-alignment: center; -fx-background-radius: 10px;");

        // Create a TableView
        TableView<Map<String, Object>> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-border-color: #e0e0e0;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Selection Listener - Capturing both name and price
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String name = (String) newValue.get("name");
                Double price = (Double) newValue.get("price");
                System.out.println("Selected Product: " + name + ", Price: " + price + " DH");
            }
        });

        // Product Name Column
        TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("name")));
        nameColumn.setStyle("-fx-alignment: center-left; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        nameColumn.setPrefWidth(150);

        // Description Column
        TableColumn<Map<String, Object>, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("description")));
        descriptionColumn.setStyle("-fx-alignment: center-left; -fx-font-size: 14px; -fx-font-family: 'Arial';");
        descriptionColumn.setPrefWidth(300);

        // Price Column
        TableColumn<Map<String, Object>, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>((Double) data.getValue().get("price")));
        priceColumn.setStyle("-fx-alignment: center-right; -fx-font-size: 14px; -fx-font-family: 'Arial';");
        priceColumn.setPrefWidth(80);

        // Properly handle cell updates
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f DH", item));
                    setStyle("-fx-background-color: #fafafa; -fx-text-fill: green; -fx-font-weight: bold;");
                }
            }
        });

        // Actions Column
        TableColumn<Map<String, Object>, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(180);
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button updateButton = new Button("Update");

            {
                // Style buttons
                deleteButton.setStyle("-fx-background-color: #f06292; -fx-text-fill: white;");
                updateButton.setStyle("-fx-background-color: #80e27e; -fx-text-fill: white;");

                // Button actions
                deleteButton.setOnAction(event -> {
                    Map<String, Object> product = getTableView().getItems().get(getIndex());
                    String name = (String) product.get("name");

                    Document products = Model.findProductByName(name);
                    if (products == null) {
                        showAlert("Error", "Product not found.");
                        return;
                    }

                    if (showDeleteConfirmationDialog(products)) {
                        controller.deleteProduct(products);
                        getTableView().getItems().remove(product);
                    } else {
                        showAlert("Error", "Failed to delete the product.");
                    }
                });

                updateButton.setOnAction(event -> {
                    Map<String, Object> product = getTableView().getItems().get(getIndex());
                    System.out.println("Updating: " + product.get("name"));
                    showUpdateeForm(product);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Create an HBox to align buttons horizontally at the bottom of the cell
                    HBox actionButtons = new HBox(10, updateButton, deleteButton);
                    actionButtons.setAlignment(Pos.BOTTOM_CENTER); // Align buttons at the bottom
                    setGraphic(actionButtons);
                }
            }
        });

        // Add columns to the TableView
        tableView.getColumns().addAll(nameColumn, descriptionColumn, priceColumn, actionColumn);

        // Load data into the table
        loadProductData(tableView);

        // Add TableView to the layout
        tableLayout.getChildren().add(tableView);
        mainLayout.setCenter(tableLayout);
    }




    private void loadProductData(TableView<Map<String, Object>> tableView) {
        List<Document> products = Model.getAllProducts(); // Fetch data from DB or source
        if (products == null || products.isEmpty()) {
            System.out.println("No products found.");
            tableView.setItems(FXCollections.observableArrayList()); // Clear table
            return;
        }

        List<Map<String, Object>> productMaps = new ArrayList<>();

        for (Document product : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", product.getString("name") != null ? product.getString("name") : "N/A");
            map.put("description", product.getString("description") != null ? product.getString("description") : "N/A");

            // Handling price as Integer, converting to Double
            Object priceObject = product.get("price");
            if (priceObject instanceof Integer) {
                map.put("price", ((Integer) priceObject).doubleValue()); // Convert Integer to Double
            } else if (priceObject instanceof Double) {
                map.put("price", (Double) priceObject); // Keep as Double
            } else {
                map.put("price", 0.0); // Default if price is not found or is not a number
            }

            productMaps.add(map);
        }

        Platform.runLater(() -> {
            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList(productMaps);
            tableView.setItems(data);
        });
    }


    private void showNoResultsMessage(String productName) {
        // Create a VBox for the layout
        VBox noResultsLayout = new VBox(10);
        noResultsLayout.setPadding(new Insets(20));
        noResultsLayout.setStyle("-fx-background-color: #ffffff; -fx-alignment: center;");

        // Create a Label to display the "No results found" message
        Label noResultsLabel = new Label("No products found with the name: " + productName);
        noResultsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f44336; -fx-font-weight: bold;");

        // Add the label to the layout
        noResultsLayout.getChildren().add(noResultsLabel);

        // Set the layout to the main content area (assuming 'mainLayout' is your root layout)
        mainLayout.setCenter(noResultsLayout);
    }




    public void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showLoginScreen(Stage primaryStage) {
        try {
            start(primaryStage); // Call the start method to reset to the login screen
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showUpdateProductWindow() {
        // New Stage for updating a product
        Stage updateProductStage = new Stage();
        updateProductStage.setTitle("Update Product");

        // Input fields with Labels and rounded corners
        Label idLabel = new Label("Product ID (to update):");
        TextField idField = new TextField();
        idField.setPromptText("Product ID");
        idField.setStyle("-fx-background-radius: 15px;");

        Label nameLabel = new Label("New Product Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("New Product Name");
        nameField.setStyle("-fx-background-radius: 15px;");

        Label descriptionLabel = new Label("New Product Description:");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("New Product Description");
        descriptionField.setStyle("-fx-background-radius: 15px;");

        Label priceLabel = new Label("New Product Price:");
        TextField priceField = new TextField();
        priceField.setPromptText("New Product Price");
        priceField.setStyle("-fx-background-radius: 15px;");

        // Update Button with style
        Button updateButton = new Button("Update Product");
        updateButton.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;");
        updateButton.setOnAction(event -> {
            try {
                // Read input values
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());

                // Pass data to the controller
                Controller.updateProduct(id, name, description, price);

                // Close the window on success
                updateProductStage.close();
            } catch (NumberFormatException e) {
                // Show error if inputs are invalid
                showAlert("Error", "Please enter valid product details.", Alert.AlertType.ERROR);
            }
        });

        // Layout for the Update Product window
        VBox layout = new VBox(10, idLabel, idField, nameLabel, nameField, descriptionLabel, descriptionField, priceLabel, priceField, updateButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 500, 400); // Larger window size
        updateProductStage.setScene(scene);
        updateProductStage.show();
    }


    public void showProductsWindow(List<Document> products) {
        // New Stage for showing products
        Stage productsStage = new Stage();
        productsStage.setTitle("Products");

        // Create a VBox layout to hold the list of products
        VBox productLayout = new VBox(15);  // Increased spacing
        productLayout.setAlignment(Pos.TOP_CENTER); // Align top to give more space for content
        productLayout.setPadding(new Insets(20));

        // Iterate through each product and display it in a styled layout
        for (Document product : products) {
            String id = product.get("id") != null ? product.get("id").toString() : "N/A";
            String name = product.getString("name") != null ? product.getString("name") : "N/A";
            String price = product.getDouble("price") != null ? product.getDouble("price").toString() : "N/A";

            // Create a VBox for each product with styled labels
            VBox productInfo = new VBox(10); // Space between info lines
            productInfo.setStyle("-fx-background-color: #f5f5f5; -fx-border-radius: 10px; -fx-padding: 10px;");
            productInfo.setPrefWidth(450); // Set width for better alignment

            // Add a Label for each product attribute
            Label idLabel = new Label("Product ID: " + id);
            idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Label nameLabel = new Label("Name: " + name);
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8e44ad;");

            Label priceLabel = new Label("Price: $" + price);
            priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

            // Add labels to the VBox
            productInfo.getChildren().addAll(idLabel, nameLabel, priceLabel);

            // Add a separator line to separate product entries
            Separator separator = new Separator();
            separator.setStyle("-fx-background-color: #8e44ad; -fx-background-width: 2px;");

            // Add product info and separator to the main layout
            productLayout.getChildren().addAll(productInfo, separator);
        }

        // Create a ScrollPane and set the VBox as its content
        ScrollPane scrollPane = new ScrollPane(productLayout);
        scrollPane.setFitToWidth(true);  // Make sure the content stretches to fit the width
        scrollPane.setFitToHeight(true); // Enable scrolling when content exceeds height

        // Scene setup with ScrollPane
        Scene scene = new Scene(scrollPane, 500, 400); // Larger window size for better presentation
        productsStage.setScene(scene);
        productsStage.show();
    }






   /* private void updateProduct(int index, TableView<Document> tableView) {
        Document selectedProduct = tableView.getItems().get(index);
        if (selectedProduct != null) {
            // Open a dialog to edit the product fields
            Dialog<Document> dialog = new Dialog<>();
            dialog.setTitle("Update Product");

            TextField productNameField = new TextField(selectedProduct.getString("productName"));
            TextField descriptionField = new TextField(selectedProduct.getString("description"));
            TextField priceField = new TextField(selectedProduct.getString("price"));

            VBox dialogContent = new VBox(10, new Label("Product Name:"), productNameField, new Label("Description:"), descriptionField, new Label("Price:"), priceField);
            dialog.getDialogPane().setContent(dialogContent);

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == updateButtonType) {
                    selectedProduct.put("productName", productNameField.getText());
                    selectedProduct.put("description", descriptionField.getText());
                    selectedProduct.put("price", priceField.getText());
                    return selectedProduct;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(updatedProduct -> {
                Model.updateProduct(selectedProduct.getObjectId("_id"), updatedProduct);
                refreshTable(tableView);
            });
        }
    }*/

    public void refreshTable(TableView<Document> tableView) {
        List<Document> products = Model.getAllProducts();
        tableView.getItems().setAll(products);
    }
    /* private void deleteProduct(int index, TableView<Document> tableView) {
         Document selectedProduct = tableView.getItems().get(index);
         if (selectedProduct != null) {
             Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this product?");
             confirmDialog.showAndWait().ifPresent(response -> {
                 if (response == ButtonType.OK) {
                     Model.deleteProduct(selectedProduct.getObjectId("_id"));
                     refreshTable(tableView);
                 }
             });
         }
     }*/
    private void handleSignUp(TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField) {
        // Apply modern styling to the TextFields
        usernameField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dcdcdc; -fx-border-radius: 8px; -fx-font-size: 14px; -fx-pref-height: 35px; -fx-padding: 5px;");
        passwordField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dcdcdc; -fx-border-radius: 8px; -fx-font-size: 14px; -fx-pref-height: 35px; -fx-padding: 5px;");
        confirmPasswordField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dcdcdc; -fx-border-radius: 8px; -fx-font-size: 14px; -fx-pref-height: 35px; -fx-padding: 5px;");

        // Focus effect for the fields when clicked
        usernameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #FF6F61; -fx-border-radius: 8px;");
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #FF6F61; -fx-border-radius: 8px;");
        confirmPasswordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #FF6F61; -fx-border-radius: 8px;");

        // Email Regex validation
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!username.matches(emailRegex)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sign Up Failed");
            alert.setHeaderText("Invalid Email");
            alert.setContentText("Please enter a valid email address.");
            alert.showAndWait();
            return;
        }

        // Check if any field is empty
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sign Up Failed");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign Up Failed");
            alert.setHeaderText("Password Mismatch");
            alert.setContentText("The passwords you entered do not match. Please try again.");
            alert.showAndWait();
            return;
        }

        // Check if the username already exists in the database
        Document existingUser = model.findUserByUsername(username);
        if (existingUser != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sign Up Failed");
            alert.setHeaderText("User Already Exists");
            alert.setContentText("You already have an account. Please log in.");
            alert.showAndWait();
            return;
        } else {
            // Create a new user in the database
            String userId = generateRandomId();
            Document newUser = new Document("userName", username)
                    .append("userPassword", password)
                    .append("userId", userId);
            model.insertUser(newUser);
            showSuccessAlert("Sign Up Successful","Account created successfully!");

        }
    }


    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }
    public void setController(controller controller) {
        this.Controller = controller;
    }
    private void showChatScreen(Stage primaryStage) {
        primaryStage.setTitle("GigaGirls-Chatbot");

        // Chat container setup
        chatContainer = new VBox(10);
        chatContainer.setPadding(new Insets(10));
        chatContainer.setStyle("-fx-background-color: #fafafa; -fx-border-color: #c9a0dc; -fx-border-width: 2;");
        ScrollPane chatScrollPane = new ScrollPane(chatContainer);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setStyle("-fx-background-color: #fafafa;");

        // Input Area
        questionTextArea = new TextArea();
        questionTextArea.setPromptText("Ask me something...");
        questionTextArea.setWrapText(true);
        questionTextArea.setStyle(
                "-fx-control-inner-background: #f4e1d2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10; " +
                        "-fx-font-size: 14px; " +
                        "-fx-border-color: #d6a6ff; " +
                        "-fx-border-width: 2px;"
        );
        questionTextArea.setPrefHeight(80);

        Button sendButton = new Button("Send");
        sendButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #8e44ad, #d6a6ff); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2);"
        );
        sendButton.setOnAction(event -> {
            String userQuestion = questionTextArea.getText().trim();
            if (!userQuestion.isEmpty()) {
                Controller.HandleUserInput(userQuestion, userId);
                questionTextArea.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a question.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-background-color: #8e44ad; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-radius: 10;"
        );
        logoutButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                showLoginScreen(primaryStage);
            }
        });

        // Add logo to the top-left corner
        Image logo = new Image("file:/home/chaimae/my-app/src/main/java/com/projet/logo.jpeg"); // Replace with your logo file path
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(50); // Set the logo size
        logoView.setFitHeight(50);
        logoView.setPreserveRatio(true);

        // Top container layout
        HBox topContainer = new HBox(10, logoView, logoutButton);
        topContainer.setAlignment(Pos.CENTER_LEFT); // Align logo and logout button to the left
        topContainer.setPadding(new Insets(10));
        topContainer.setStyle("-fx-background-color: #fafafa;");

        // Input area layout
        HBox inputBox = new HBox(10, questionTextArea, sendButton);
        inputBox.setAlignment(Pos.CENTER);

        // Main chat layout
        VBox chatLayout = new VBox(10, chatScrollPane, inputBox);
        chatLayout.setAlignment(Pos.TOP_CENTER);
        chatLayout.setPadding(new Insets(10));

        // BorderPane for overall layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topContainer);
        mainLayout.setCenter(chatLayout);

        // Scene setup
        Scene chatScene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(chatScene);
        primaryStage.show();

        // Load chat history for the logged-in user
        Controller.loadChatHistory(userId);
    }

    public void addUserMessage(String message) {
        HBox messageBox = createMessageBox(message, Color.PURPLE);
        Label messageLabel = (Label) messageBox.getChildren().get(0);
        messageLabel.setTextFill(Color.WHITE);
        chatContainer.getChildren().add(messageBox);
        scrollToBottom();

    }

    public void addBotMessage(String message) {
        HBox messageBox = createMessageBox(message, Color.LAVENDER);
        chatContainer.getChildren().add(messageBox);
        scrollToBottom();
    }

    public HBox createMessageBox(String message, Color bubbleColor) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-background-color: " + toHex(bubbleColor) + "; -fx-background-radius: 15; -fx-padding: 12; -fx-font-size: 14;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(500);

        HBox messageBox = new HBox();
        messageBox.setAlignment(bubbleColor == Color.PURPLE ? Pos.BASELINE_RIGHT : Pos.BASELINE_LEFT);
        messageBox.getChildren().add(messageLabel);

        return messageBox;
    }

    public void scrollToBottom() {
        chatContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            ((ScrollPane) chatContainer.getParent()).setVvalue(1.0);
        });
    }
    public void loadChatHistory(FindIterable<Document> chat) {
        // Clear chat container before loading history to prevent duplication
        chatContainer.getChildren().clear();
        for (Document doc : chat) {
            String question = doc.getString("question");
            String answer = doc.getString("answer");

            addUserMessage(question);
            addBotMessage(answer);
        }
    }
    public String toHex(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }
    public int extractPrix(String question) {
        if (question == null || question.isEmpty()) {
            return 0; // Handle empty or null input gracefully
        }

        String lowerCaseQuestion = question.toLowerCase();
        for (String priceKeyword : PRICE_TYPES) {
            if (lowerCaseQuestion.contains(priceKeyword)) {
                // Find the index of the keyword
                int keywordIndex = lowerCaseQuestion.indexOf(priceKeyword);

                // Extract the substring after the keyword
                String priceSubstring = lowerCaseQuestion.substring(keywordIndex + priceKeyword.length()).trim();

                // Use regex to extract the first number after the keyword
                String[] words = priceSubstring.split("\\s+");
                for (String word : words) {
                    try {
                        // Parse and return the first valid integer
                        return Integer.parseInt(word);
                    } catch (NumberFormatException e) {
                        // Continue searching if the word is not a number
                    }
                }
            }
        }
        return 0; // Return 0 if no valid price is found
    }


    public String extractSkinType(String question) {
        if (question == null || question.isEmpty()) {
            return null; // Handle empty or null input gracefully
        }
        String lowerCaseQuestion = question.toLowerCase();
        for (String skinType : SKIN_TYPES) {
            if (lowerCaseQuestion.contains(skinType)) {
                return skinType; // Return the first matched skin type
            }
        }
        return null; // Return null if no skin type is found
    }
}