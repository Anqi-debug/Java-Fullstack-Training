package org.example.rt_p1.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDB {
    public static void main(String[] args) {
        // Connection string to MongoDB (default localhost)
        String uri = "mongodb+srv://ac28665n:Qu54tLdBm1evc3Pk@cluster0.pldcl.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("test"); // use your DB name
            System.out.println("✅ Connected to MongoDB!");

            // List collections as a test
            for (String name : database.listCollectionNames()) {
                System.out.println("📦 Collection: " + name);
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to connect:");
            e.printStackTrace();
        }
    }
}
