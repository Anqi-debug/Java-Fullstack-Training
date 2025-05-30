package org.example.rt_p1.database;

import java.sql.*;

//Create table query
//CREATE TABLE Employee (
//  id SERIAL PRIMARY KEY,
//  first_name VARCHAR(50),
//  last_name VARCHAR(50),
//  email VARCHAR(100) UNIQUE,
//  password TEXT,
//  flagged BOOLEAN DEFAULT FALSE,
//  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//);

//Insert data query
//INSERT INTO Employee (first_name, last_name, email, password, flagged)
//VALUES
//  ('Alice', 'Smith', 'alice.smith@example.com', 'pass123', false),
//  ('Bob', 'Johnson', 'bob.johnson@example.com', 'secure456', true),
//  ('Charlie', 'Lee', 'charlie.lee@example.com', 'qwerty789', false);


public class Postgre {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "Anqi218549"; // Replace with your actual password

        try {
            System.out.println("🚀 Trying to connect...");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to PostgreSQL!");

            // --- 1. SQL Injection Example (unsafe) ---
            String maliciousEmail = "alice.smith@example.com' OR '1'='1";
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM Employee WHERE email = '" + maliciousEmail + "'";
            System.out.println("❌ Executing vulnerable query: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("🛑 Got user: " + rs.getString("first_name") + " " + rs.getString("email"));
            }

            // --- 2. PreparedStatement (safe version) ---
            String safeEmail = "alice.smith@example.com";
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM Employee WHERE email = ?");
            pstmt.setString(1, safeEmail);
            ResultSet safeRs = pstmt.executeQuery();
            while (safeRs.next()) {
                System.out.println("✅ Found user (safe): " + safeRs.getString("first_name") + " " + safeRs.getString("email"));
            }

            // --- 3. Transaction and Rollback ---
            try {
                conn.setAutoCommit(false); // begin transaction

                // Insert a new employee
                PreparedStatement insert1 = conn.prepareStatement(
                        "INSERT INTO Employee (first_name, last_name, email, password) VALUES (?, ?, ?, ?)");
                insert1.setString(1, "Test");
                insert1.setString(2, "User");
                insert1.setString(3, "test.user@example.com");
                insert1.setString(4, "test123");
                insert1.executeUpdate();

                // Intentional error: duplicate email
                PreparedStatement insert2 = conn.prepareStatement(
                        "INSERT INTO Employee (first_name, last_name, email, password) VALUES (?, ?, ?, ?)");
                insert2.setString(1, "Hacker");
                insert2.setString(2, "User");
                insert2.setString(3, "test.user@example.com"); // duplicate
                insert2.setString(4, "hackme");
                insert2.executeUpdate();

                conn.commit();
                System.out.println("✅ Transaction committed.");
            } catch (Exception e) {
                conn.rollback();
                System.out.println("❌ Transaction rolled back due to error:");
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true); // reset
            }

            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Failed to connect or execute:");
            e.printStackTrace();
        }
    }
}



