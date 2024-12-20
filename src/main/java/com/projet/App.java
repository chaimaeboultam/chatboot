package com.projet;

public class App {
    public static void main(String[] args) {
        // Initialize the model
        model Model = new model();

        // Pass the model to the vue class using a static field or a custom init method
        vue.setModel(Model);

        // Launch the JavaFX application
        javafx.application.Application.launch(vue.class, args);

    }
}