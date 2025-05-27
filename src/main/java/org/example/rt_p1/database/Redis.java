package org.example.rt_p1.database;

import redis.clients.jedis.Jedis;

public class Redis {
    public static void main(String[] args) {
        // Connect to Redis running on Docker (localhost:6379)
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            System.out.println("✅ Connected to Redis!");

            // Store and retrieve a key
            jedis.set("message", "Hello from Java + Redis + Docker!");
            String value = jedis.get("message");
            System.out.println("🔑 message = " + value);
        } catch (Exception e) {
            System.out.println("❌ Failed to connect to Redis:");
            e.printStackTrace();
        }
    }
}
