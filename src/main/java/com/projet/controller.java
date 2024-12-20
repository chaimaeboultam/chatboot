package com.projet;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import org.bson.types.ObjectId;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.mongodb.client.FindIterable;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Properties;
import java.time.Duration;
import java.util.Collections;
import java.util.Arrays;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
public class controller {
    private static model Model;
    private static vue Vue;
    private static final Map<String, Document> pendingConfirmation = new HashMap<>();
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();  // For concurrent Kafka message consumption
    private EmbeddingModel embeddingModel;
    public controller(model Model, vue Vue){
        this.Model = Model;
        this.Vue = Vue;
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        initializeKafka();
    }
    private void initializeKafka() {
        Properties producerProps = createProducerProperties();
        producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = createConsumerProperties();
        consumer = new KafkaConsumer<>(consumerProps);
    }

    private Properties createProducerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        return properties;
    }

    private Properties createConsumerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "product-group");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
        return properties;
    }

    private void sendMessageToKafka(String userId, String message, boolean isAnswer) {
        String topic = isAnswer ? "answer_topic_" + userId : "question_topic_" + userId;
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, userId, message);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.println("Message sent to Kafka: " + message + " to topic: " + topic);
            }
        });
    }


    private void consumeMessagesFromKafka(String userId) {
        // Create a new thread each time you switch users to reset the consumer properly
        executorService.submit(() -> {
            // Define topics based on the userId
            String questionTopic = "question_topic_" + userId;
            String answerTopic = "answer_topic_" + userId;

            // Print to verify the topics being subscribed to
            System.out.println("Subscribing to topics: " + questionTopic + ", " + answerTopic);

            // Create a new consumer using the properties from the createConsumerProperties method
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(createConsumerProperties());

            // Unsubscribe from any previous topics (if any)
            consumer.unsubscribe();

            // Subscribe to the new topics for the current user
            consumer.subscribe(Arrays.asList(questionTopic, answerTopic));

            // Poll the messages for the topics indefinitely
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Consumed message from Kafka: " + record.value() + " from topic: " + record.topic());

                    if (record.topic().equals(answerTopic)) {
                        // Handle the answer topic
                        Platform.runLater(() -> Vue.addBotMessage(record.value()));
                    } else if (record.topic().equals(questionTopic)) {
                        // Handle the question topic
                        Platform.runLater(() -> Vue.addUserMessage(record.value()));
                    }
                }
            }
        });
    }






    public void HandleUserInput(String question, String userId) {
        if (question == null || question.trim().isEmpty()) {
            //Vue.addBotMessage("Please ask a valid question.");
            sendMessageToKafka(userId,"Please ask a valid question.",true);
            return;
        }
        // Check for confirmation
        if (handlePendingConfirmation(question, userId)) {
            return;
        }
        sendMessageToKafka(userId,question,false);
        consumeMessagesFromKafka(userId);
        //Vue.addUserMessage(question);
        String skinType = Vue.extractSkinType(question);
        int prix = Vue.extractPrix(question);
        if (skinType != null) {
            //Vue.addBotMessage("I see you're asking about " + skinType + " skin.");
            sendMessageToKafka(userId, "I see you're asking about " + skinType + " skin.", true);
        }

        try {
            if (question.toLowerCase().matches(".*product.*(under|below|max|maximum) \\d+.*")) {
                handlePriceQuery(question, userId);

                return;
            }


            // Generate embedding and find similar products
            Embedding queryEmbedding = embeddingModel.embed(question).content();
            List<Document> matches = Model.searchSimilarProducts(queryEmbedding);

            if (matches == null || matches.isEmpty()) {
                //Vue.addBotMessage("Sorry, I couldn't find any relevant suggestions.");
                sendMessageToKafka(userId,"Sorry, I couldn't find any relevant suggestions.",true);
                return;
            }
            if (prix!= 0){
                processProductMatchesWithPrix(matches, userId, skinType, question,prix);
            }
            else {
                processProductMatches(matches, userId, skinType, question);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handlePriceQuery(String question, String userId) {
        try {
            // Extract the price from the question
            String priceString = question.replaceAll("\\D+", ""); // Keep only digits
            if (priceString.isEmpty()) {
                //Vue.addBotMessage("Please specify a valid price.");
                sendMessageToKafka(userId,"Please specify a valid price.",true);
                return;
            }

            double priceThreshold = Double.parseDouble(priceString);
            String skinType = Vue.extractSkinType(question);

            // Fetch products under the specified price
            List<Document> affordableProducts = Model.getProductsUnderPrice(priceThreshold);

            if (affordableProducts == null || affordableProducts.isEmpty()) {
                //Vue.addBotMessage("Sorry, no products found under " + priceThreshold + ".");
                sendMessageToKafka(userId,"Sorry, no products found under " + priceThreshold + ".",true);
                return;
            }

            // Filter products based on skin type if provided
            List<Document> filteredProducts = new ArrayList<>();
            for (Document product : affordableProducts) {
                String productSkinType = product.getString("skinType");
                if (skinType == null || (productSkinType != null && productSkinType.equalsIgnoreCase(skinType))) {
                    filteredProducts.add(product);
                }
            }

            if (filteredProducts.isEmpty()) {
               // Vue.addBotMessage("Sorry, no products matching your criteria were found under " + priceThreshold + ".");
                sendMessageToKafka(userId,"Sorry, no products matching your criteria were found under " + priceThreshold + ".",true);
                return;
            }

            // Build a response for the user
            StringBuilder response = new StringBuilder("Here are some products under " + priceThreshold + ":\n");
            for (Document product : filteredProducts) {
                String name = product.getString("name");
                Object priceObj = product.get("price");
                String priceStr = (priceObj instanceof Number) ? priceObj.toString() : "N/A";
                String description = product.getString("description");
                response.append("- ").append(name)
                        .append(" (Price: ").append(priceStr).append(")\n");
                if (skinType != null) {
                    response.append("  Suitable for ").append(skinType).append(" skin.\n");
                }
                if (description != null) {
                    response.append("  Description: ").append(description).append("\n");
                }
            }

            //Vue.addBotMessage(response.toString().trim());
            sendMessageToKafka(userId,response.toString().trim(),true);
        } catch (NumberFormatException e) {
            //Vue.addBotMessage("Could not understand the price in your query.");
            sendMessageToKafka(userId,"Could not understand the price in your query.",true);
        } catch (Exception e) {
            e.printStackTrace();
            //Vue.addBotMessage("An error occurred while processing your request.");
            sendMessageToKafka(userId,"An error occurred while processing your request.",true);
        }
    }

    private boolean handlePendingConfirmation(String question, String userId) {
        if (question.equalsIgnoreCase("yes") && pendingConfirmation.containsKey(userId)) {
            Document product = pendingConfirmation.remove(userId);
            displayProductDetails(product,userId);
            return true;
        }
        return false;
    }
    private void displayProductDetails(Document product, String userId) {
        String description = product.getString("description");
        Object priceObj = product.get("price");
        String priceStr = (priceObj instanceof Integer)
                ? String.valueOf(priceObj)
                : (priceObj instanceof Double) ? String.format("%.2f", (Double) priceObj) : "Price not available";

        //Vue.addBotMessage("Here are more details about the product:\n" +
              //  "- Description: " + (description != null ? description : "No description available.") + "\n" +
              //  "- Price: " + priceStr);
        sendMessageToKafka(userId,"Here are more details about the product:\n" +
                "- Description: " + (description != null ? description : "No description available.") + "\n" +
                "- Price: " + priceStr,true);
    }
    private void processProductMatches(List<Document> matches, String userId, String skinType, String question) {
        if (matches == null || matches.isEmpty()) {
            //Vue.addBotMessage("Sorry, I couldn't find any relevant suggestions.");
            sendMessageToKafka(userId,"Sorry, I couldn't find any relevant suggestions.",true);
            return;
        }

        StringBuilder responseBuilder = new StringBuilder();

        // Handle the best match (first item in the list)
        Document bestMatch = matches.get(0);
        String productName = bestMatch.getString("name");
        String description = bestMatch.getString("description");
        String answer = bestMatch.getString("answer");

        if (productName != null && description != null) {
            responseBuilder.append("Our top suggestion:\n");
            responseBuilder.append("- Product: ").append(productName).append("\n");
            //responseBuilder.append("  Description: ").append(description).append("\n");
            if (skinType != null) {
                responseBuilder.append("  Suitable for ").append(skinType).append(" skin.\n");
            }
            if (answer != null) {
                responseBuilder.append("  Overview: ").append(answer).append("\n");
            }
            responseBuilder.append("If you'd like more details about this product, type 'yes'.\n");
            pendingConfirmation.put(userId, bestMatch);

        } else if (answer != null) {
            responseBuilder.append(answer).append("\n");
        } else {
            responseBuilder.append("Sorry, I couldn't find a matching response.\n");
        }

        // Handle additional matches (if any)
        if (matches.size() > 2) {
            responseBuilder.append("\nOther suggestions:\n");
            for (int i = 1; i < matches.size(); i++) {
                Document match = matches.get(i);
                String otherProductName = match.getString("name");
                if (otherProductName != null) {
                    responseBuilder.append("- ").append(otherProductName).append("\n");
                }
            }
        }

        String finalResponse = responseBuilder.toString().trim();

        // Save the question and response to history
        Model.saveToHistory(question, finalResponse, userId);
        sendMessageToKafka(userId, finalResponse, true);

        // Show the bot's response in the chat
        //Vue.addBotMessage(finalResponse);
    }
    private void processProductMatchesWithPrix(List<Document> matches, String userId, String skinType, String question, int prix) {
        if (matches == null || matches.isEmpty()) {
            //Vue.addBotMessage("Sorry, I couldn't find any relevant suggestions.");
            sendMessageToKafka(userId,"Sorry, I couldn't find any relevant suggestions.",true);
            return;
        }

        StringBuilder responseBuilder = new StringBuilder();
        boolean foundProduct = false;

        // Handle the best match (first item in the list)
        for (Document match : matches) {
            Object priceObj = match.get("price");
            int priceInt = 0;

            // Convert price to an integer if possible
            if (priceObj instanceof Integer) {
                priceInt = (Integer) priceObj;
            } else if (priceObj instanceof Double) {
                priceInt = (int) Math.round((Double) priceObj);
            } else {
                continue; // Skip items with invalid or unavailable prices
            }
            System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+priceInt);
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+prix);
            if (priceInt <= prix) {
                foundProduct = true;
                String productName = match.getString("name");
                String description = match.getString("description");
                String answer = match.getString("answer");

                if (productName != null && description != null) {
                    responseBuilder.append("Our top suggestion:\n");
                    responseBuilder.append("- Product: ").append(productName).append("\n");
                    responseBuilder.append("  Price: ").append(priceInt).append("DH").append("\n");

                    if (skinType != null) {
                        responseBuilder.append("  Suitable for ").append(skinType).append(" skin.\n");
                    }
                    if (answer != null) {
                        responseBuilder.append("  Overview: ").append(answer).append("\n");
                    }
                    responseBuilder.append("\n");
                }
                break; // Stop after finding the first suitable product
            }
        }

        if (!foundProduct) {
            responseBuilder.append("Sorry, we don't have a product under this price.\n");
        } else if (matches.size() > 3) {
            responseBuilder.append("\nOther suggestions:\n");
            for (int i = 1; i < matches.size(); i++) {
                Document match = matches.get(i);
                Object otherPriceObj = match.get("price");
                int otherPriceInt = 0;

                // Convert price to an integer if possible
                if (otherPriceObj instanceof Integer) {
                    otherPriceInt = (Integer) otherPriceObj;
                } else if (otherPriceObj instanceof Double) {
                    otherPriceInt = (int) Math.round((Double) otherPriceObj);
                } else {
                    continue; // Skip items with invalid or unavailable prices
                }

                if (otherPriceInt <= prix) {
                    String otherProductName = match.getString("name");
                    if (otherProductName != null) {
                        responseBuilder.append("- ").append(otherProductName)
                                .append(":").append(otherPriceInt).append("DH").append("\n");
                    }
                }
            }
        }

        String finalResponse = responseBuilder.toString().trim();

        // Save the question and response to history
        Model.saveToHistory(question, finalResponse, userId);
        //sendMessageToKafka(userId, finalResponse, true);

        // Show the bot's response in the chat
        //Vue.addBotMessage(finalResponse);
        sendMessageToKafka(userId,finalResponse,true);
    }

    public void loadChatHistory(String userId) {
        FindIterable<Document> chat = Model.loadChatHistory(userId);
        Vue.loadChatHistory(chat);
    }
    public void addProduct(int id, String name, String description, double price) {
        try {
            Model.addProduct(id, name, description, price); // Call the model's method
            Vue.showSuccessAlert("Success","Product added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            Vue.showAlert("Error", "Failed to add product. Please try again.", Alert.AlertType.ERROR);
        }
    }

    public void updateProduct(int id, String name, String description, double price) {
        try {
            // Call the model to update the product
            Model.updateProduct(id, name, description, price);
            Vue.showSuccessAlert("Success","Product deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            Vue.showAlert("Error", "Failed to update product.", Alert.AlertType.ERROR);
        }
    }
    public static void deleteProduct(Map<String, Object> rowData) {
        try {
            // Call the model to delete the product using the row data
            Model.deleteProduct(rowData);
            Vue.showSuccessAlert("Success","Product deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            Vue.showAlert("Error", "Failed to delete product.", Alert.AlertType.ERROR);
        }
    }
    public static void deleteProducts(Map<String, Object> rowData) {
        try {
            // Call the model to delete the product using the row data
            Model.deleteProducts(rowData);
            Vue.showSuccessAlert("Success", "Product deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            Vue.showAlert("Error", "Failed to delete product.", Alert.AlertType.ERROR);
        }
    }
    public void showProducts() {
        try {
            // Fetch list of products from the model
            List<Document> products = Model.getAllProducts();  // Assuming getAllProducts() returns List<Document>

            // Pass the list of products to the view to display
            Vue.showProductsWindow(products);
        } catch (Exception e) {
            e.printStackTrace();
            Vue.showAlert("Error", "Failed to load products.", Alert.AlertType.ERROR);
        }
    }

    private void handleSignUp(TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField) {
        // Get values from the input fields
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Check if any field is empty
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "Sign Up Failed", "Please fill in all fields.");
        } else if (!password.equals(confirmPassword)) {
            showAlert(AlertType.WARNING, "Password Mismatch", "The passwords do not match.");
        } else {
            // Create a new user document
            Document user = new Document("username", username)
                    .append("password", password)
                    .append("userId", new ObjectId()); // MongoDB ObjectId

            // Insert user into MongoDB (you can call your method here)
            Model.insertUser(user);

            // Display a success message or navigate to another screen
            showAlert(AlertType.INFORMATION, "Sign Up Successful", "You have successfully signed up!");
        }
    }
    private void showAlert(AlertType alertType, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Alert");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}