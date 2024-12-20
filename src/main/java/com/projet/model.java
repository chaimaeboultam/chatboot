package com.projet;

import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import com.mongodb.client.model.Projections;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import java.util.*;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TableView;
import dev.langchain4j.model.output.Response;
import static com.mongodb.client.model.Filters.eq;
public class model {
    private MongoCollection<Document> productCollection;
    private MongoCollection<Document> qaCollesction;
    private MongoCollection<Document> historyCollection;
    private MongoCollection<Document> clientCollection;
    double SIMILARITY_THRESHOLD = 0.45;
    private EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    private TableView<Document> tableView;

    public model(){
        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        this.historyCollection = client.getDatabase("mydb").getCollection("userHistory");
        this.qaCollesction = client.getDatabase("mydb").getCollection("QA");
        this.productCollection = client.getDatabase("mydb").getCollection("product");
        embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        this.clientCollection = client.getDatabase("mydb").getCollection("client");
    }
    public List<Document> getProductsUnderPrice(double priceThreshold) {
        // Query MongoDB to fetch products under the specified price
        return productCollection.find(new Document("price", new Document("$lt", priceThreshold)))
                .into(new ArrayList<>());
    }
    // Add the new searchProducts method
    public List<Document> searchProducts(String query) {
        // Create a regex filter to search for the query in both name and description fields (case-insensitive)
        return productCollection.find(Filters.or(
                Filters.regex("name", query, "i"),
                Filters.regex("description", query, "i")
        )).into(new ArrayList<>());
    }

    public FindIterable<Document>  loadChatHistory(String userId){
        return this.historyCollection.find(new Document("userId", userId));
    }

    public void saveToHistory(String question,String answer, String userId){
        Document historyEntry = new Document("userId",userId).append("question",question)
                .append("answer",answer)
                .append("timestamp",System.currentTimeMillis());
        historyCollection.insertOne(historyEntry);
    }

    public float[] listToFloat(List<Double> list){
        float[] array = new float[list.size()];
        for(int i = 0; i<list.size();i++){
            array[i] = list.get(i).floatValue();
        }
        return array;
    }

    private double cosineSimilarity(float[] vecA, float[] vecB) {
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < vecA.length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += Math.pow(vecA[i], 2);
            normB += Math.pow(vecB[i], 2);
        }

        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        if (normA == 0 || normB == 0) return 0.0;

        return dotProduct / (normA * normB);
    }

    public List<Document> searchSimilarProducts(Embedding queryEmbedding) {
        List<Document> similarProducts = new ArrayList<>();
        double highestSimilarityQA = -1;
        Document bestMatchQA = null;

        // Search for all similar products
        for (Document product : productCollection.find()) {
            List<Double> storedEmbeddingVector = (List<Double>) product.get("name_description_embedding");
            if (storedEmbeddingVector != null) {
                float[] storedEmbeddingArray = listToFloat(storedEmbeddingVector);
                double similarityScore = cosineSimilarity(queryEmbedding.vector(), storedEmbeddingArray);

                if (similarityScore >= SIMILARITY_THRESHOLD) {
                    // Add product to the list if similarity is above the threshold
                    product.append("similarity", similarityScore); // Append similarity score for reference
                    similarProducts.add(product);
                }
            }
        }

        // Sort products by similarity (optional)
        similarProducts.sort((doc1, doc2) -> Double.compare((double) doc2.get("similarity"), (double) doc1.get("similarity")));

        // Search for the most similar QA entry
        for (Document qa : qaCollesction.find()) {
            List<Double> storedEmbeddingVector = (List<Double>) qa.get("embedding");
            if (storedEmbeddingVector != null) {
                float[] storedEmbeddingArray = listToFloat(storedEmbeddingVector);
                double similarityScore = cosineSimilarity(queryEmbedding.vector(), storedEmbeddingArray);

                if (similarityScore > highestSimilarityQA) {
                    highestSimilarityQA = similarityScore;
                    bestMatchQA = qa;
                }
            }
        }

        // If no products or QA entries meet the similarity threshold, return null
        if (similarProducts.isEmpty() && highestSimilarityQA < SIMILARITY_THRESHOLD) {
            return null; // Return null if no matches are above the threshold
        }

        // Combine product and QA results
        List<Document> result = new ArrayList<>(similarProducts);

        // Add the best QA match (if any) to the result
        if (highestSimilarityQA >= SIMILARITY_THRESHOLD) {
            Document qaResult = new Document();
            qaResult.append("answer", bestMatchQA.getString("answer"));
            result.add(qaResult);
        }

        return result.isEmpty() ? null : result; // Return null if no relevant results found
    }


    public Document findUserByUsernameAndPassword(String username, String password) {
        return this.clientCollection.find(Filters.and(
                Filters.eq("userName", username),
                Filters.eq("userPassword", password)
        )).first();
    }

    public Document retreiveRole(String username, String password){
        return this.clientCollection.find(Filters.and(
                Filters.eq("userName", username),
                Filters.eq("userPassword", password)
        )).projection(Projections.include("role")).first();
    }

    public static Document findUserByUsername(String username) {
        return MongoClients.create("mongodb://localhost:27017")
                .getDatabase("mydb")
                .getCollection("client")
                .find(new Document("userName", username))
                .first();
    }

    public static void insertUser(Document user) {
        MongoClients.create("mongodb://localhost:27017")
                .getDatabase("mydb")
                .getCollection("client")
                .insertOne(user);
    }

    public Document findProductByName(String productName) {
        return MongoClients.create("mongodb://localhost:27017")
                .getDatabase("mydb")
                .getCollection("product")
                .find(new Document("name",productName))
                .first();
    }

    public void deleteProduct(Map<String, Object> rowData) {
        // Assuming your rowData contains a "productId" field
        int productId = (int) rowData.get("id");

        // Delete the product by its ID
        productCollection.deleteOne(Filters.eq("id", productId));
    }
    public void deleteProducts(Map<String, Object> rowData) {
        // Assuming your rowData contains a "productId" field
        int productId = (int) rowData.get("productId");

        // Delete the product by its ID
        productCollection.deleteOne(Filters.eq("id", productId));
    }

    public List<Document> getAllProducts() {
        // Ensure the collection is initialized
        if (productCollection == null) {
            throw new IllegalStateException("MongoDB collection is not initialized.");
        }

        try {
            // Fetch all products with an optional limit to prevent overwhelming memory
            return productCollection.find().limit(100).into(new ArrayList<>());
        } catch (Exception e) {
            // Log the error (you can replace this with proper logging if needed)
            e.printStackTrace();
            // Return an empty list in case of error
            return new ArrayList<>();
        }
    }

    public void addProduct(int id, String name, String description, double price) {
        try {
            String combinedText = name + " " + description;

            Embedding embedding = embeddingModel.embed(combinedText).content();

            List<Double> nameDescriptionEmbedding = new ArrayList<>();
            for (float value : embedding.vector()) {
                nameDescriptionEmbedding.add((double) value);
            }

            Document newProduct = new Document()
                    .append("id", id)
                    .append("name", name)
                    .append("description", description)
                    .append("price", price)
                    .append("name_description_embedding", nameDescriptionEmbedding);

            productCollection.insertOne(newProduct);

            System.out.println("Product added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to add product.");
        }
    }

    public void updateProduct(int id, String name, String description, double price) {
        try {
            // Query filter to find the product
            Document filter = new Document("id", id);
            Document existingProduct = productCollection.find(filter).first();

            if (existingProduct == null) {
                throw new RuntimeException("Product not found with ID: " + id);
            }

            // Fallback to existing values if new values are null/empty
            String newName = (name != null && !name.isEmpty()) ? name : existingProduct.getString("name");
            String newDescription = (description != null && !description.isEmpty()) ? description : existingProduct.getString("description");
            double newPrice = (price != -1) ? price : existingProduct.getDouble("price");

            // Generate combined embedding for name and description
            String combinedText = newName + " " + newDescription;
            Response<Embedding> embeddingResponse = embeddingModel.embed(combinedText);
            Embedding updatedEmbedding = embeddingResponse.content();
            float[] embeddingVector = updatedEmbedding.vector();

            // Convert embedding vector to List<Double>
            List<Double> updatedEmbeddingList = new ArrayList<>();
            for (float value : embeddingVector) {
                updatedEmbeddingList.add((double) value);
            }

            // Combine all updates in a single $set document
            Document updateFields = new Document();
            updateFields.append("price", newPrice);
            updateFields.append("name_description_embedding", updatedEmbeddingList);

            if (!newName.equals(existingProduct.getString("name"))) {
                updateFields.append("name", newName);
            }
            if (!newDescription.equals(existingProduct.getString("description"))) {
                updateFields.append("description", newDescription);
            }

            // Final update document
            Document updateData = new Document("$set", updateFields);

            // Perform the update
            UpdateResult result = productCollection.updateOne(filter, updateData);

            // Log the result
            System.out.println("Update result: " + result.getMatchedCount() + " document(s) matched");
            System.out.println("Update result: " + result.getModifiedCount() + " document(s) updated");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update product embedding", e);
        }
    }
    public List<Document> searchProductByName(String productName) {
        try {
            // Use MongoDB regex query to search for products with matching names
            Bson query = Filters.regex("productName", "." + productName + ".", "i"); // "i" for case-insensitive
            return productCollection.find(query).into(new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace(); // Log error for debugging
            return new ArrayList<>(); // Return an empty list in case of error
        }
    }
    public int fetchProductIdByName(String productName) {
        Document document = productCollection.find(eq("name", productName)).first(); // Query for the document

        if (document != null) {
            Object productIdObj = document.get("id"); // Get the "id" field

            // Check if it's an Integer
            if (productIdObj instanceof Integer) {
                int productId = (Integer) productIdObj;
                System.out.println("productId fetched: " + productId);
                return productId;
            } else if (productIdObj instanceof Number) {
                // Handle other numeric types (e.g., Long, Double)
                Number number = (Number) productIdObj;
                int productId = number.intValue();
                System.out.println("productId fetched as Number: " + productId);
                return productId;
            } else {
                System.out.println("The 'id' field is not an integer or numeric type.");
                return -1;
            }
        }
        return -1; // Product not found
    }
}