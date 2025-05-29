package org.example.rt_p1.database;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean flagged;

    // Getters and setters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Boolean getFlagged() { return flagged; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFlagged(Boolean flagged) { this.flagged = flagged; }

    // --- 1. SQL Injection Test Method ---
    public static void runSqlInjectionDemo(SessionFactory factory) {
        Session session = factory.openSession();
        try {
            String injectedEmail = "foo@example.com' OR '1'='1";
            String sql = "SELECT * FROM employee WHERE email = '" + injectedEmail + "'";
            List<Employee> result = session.createNativeQuery(sql, Employee.class).getResultList();
            System.out.println("🛑 SQL Injection result size: " + result.size());
        } finally {
            session.close();
        }
    }

    // --- 2. PreparedStatement (safe version) ---
    public static void runPreparedStatementDemo(SessionFactory factory) {
        Session session = factory.openSession();
        try {
            String safeEmail = "alice.smith@example.com";
            Query<Employee> query = session.createQuery("FROM Employee WHERE email = :email", Employee.class);
            query.setParameter("email", safeEmail);
            List<Employee> safeResult = query.getResultList();
            System.out.println("✅ Safe result size: " + safeResult.size());
        } finally {
            session.close();
        }
    }

    // --- 3. Transaction and Rollback ---
    public static void runTransactionWithRollback(SessionFactory factory) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Employee e1 = new Employee();
            e1.setFirstName("Test");
            e1.setLastName("User");
            e1.setEmail("test.user@example.com");
            e1.setPassword("secure");
            session.persist(e1);

            Employee e2 = new Employee();
            e2.setFirstName("Hacker");
            e2.setLastName("User");
            e2.setEmail("test.user@example.com"); // duplicate email
            e2.setPassword("badpass");
            session.persist(e2);

            tx.commit(); // will fail due to duplicate email
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            System.out.println("❌ Transaction rolled back due to error:");
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Employee.class)
                .buildSessionFactory();

        Employee.runSqlInjectionDemo(factory);
        Employee.runPreparedStatementDemo(factory);
        Employee.runTransactionWithRollback(factory);

        factory.close();
    }
}



