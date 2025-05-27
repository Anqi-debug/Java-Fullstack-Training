package org.example.rt_p1.database;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import java.net.InetSocketAddress;

public class Cassandra {
    public static void main(String[] args) {
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("localhost", 9042))
                .withLocalDatacenter("datacenter1") // default DC for Cassandra docker
                .build()) {

            System.out.println("✅ Connected to Cassandra");

            // Create keyspace and table (if needed)
            session.execute("CREATE KEYSPACE IF NOT EXISTS demo WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};");
            session.execute("CREATE TABLE IF NOT EXISTS demo.messages (id UUID PRIMARY KEY, message text);");

            // Insert data
            session.execute("INSERT INTO demo.messages (id, message) VALUES (uuid(), 'Hello from Java!');");

            // Read data
            ResultSet rs = session.execute("SELECT * FROM demo.messages;");
            rs.forEach(row -> System.out.println("📝 " + row.getString("message")));
        }
    }
}

