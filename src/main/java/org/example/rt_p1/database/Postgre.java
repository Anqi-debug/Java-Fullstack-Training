package org.example.rt_p1.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Postgre {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "Anqi218549";

        try {
            System.out.println("🚀 Trying to connect...");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to PostgreSQL!");

            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Failed to connect:");
            e.printStackTrace();
        }
    }
}


