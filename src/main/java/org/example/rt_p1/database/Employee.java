package org.example.rt_p1.database;
//JPA:
//ORM:
//Hibernate:
import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

//In Java (especially with frameworks like Hibernate, Spring, or JPA), annotations are special markers that
// give metadata about classes, methods, fields, or parameters. These annotations tell the framework how to behave at runtime.
//In Hibernate, an entity is a lightweight, persistent domain object that represents a table in a relational database.
//Each instance of an entity class corresponds to a row in that table.
//A Java class annotated with @Entity = a database table
//An object of that class = a row in the table
@Entity //Marks the class as a Hibernate-managed entity, meaning it will be mapped to a database table
@Table(name = "employee") //Maps this entity to the employee table
public class Employee {

    @Id //Marks id as the primary key
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

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", flagged=" + flagged +
                '}';
    }

    // --- ADD ---
    public static void addEmployee(SessionFactory factory, Employee employee) {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(employee); //JPA standard
        tx.commit();
        session.close();
    }

    // --- READ ---
    public static Employee getEmployeeById(SessionFactory factory, int id) {
        Session session = factory.openSession();
        Employee emp = session.get(Employee.class, id);
        session.close();
        return emp;
    }

    // --- UPDATE ---
    public static void updateEmployeeEmail(SessionFactory factory, int id, String newEmail) {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        Employee emp = session.get(Employee.class, id);
        if (emp != null) {
            emp.setEmail(newEmail);
            session.merge(emp);
        }
        tx.commit();
        session.close();
    }

    // --- DELETE ---
    public static void deleteEmployee(SessionFactory factory, int id) {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        Employee emp = session.get(Employee.class, id);
        if (emp != null) {
            session.remove(emp);
        }
        tx.commit();
        session.close();
    }

    // --- LIST ALL ---
    public static void listAllEmployees(SessionFactory factory) {
        Session session = factory.openSession();
        List<Employee> employees = session.createQuery("FROM Employee", Employee.class).list();
        employees.forEach(System.out::println);
        session.close();
    }

    // --- 1. SQL Injection Test Method ---
    public static void runSqlInjectionDemo(SessionFactory factory) {
        Session session = factory.openSession();
        try {
            String injectedEmail = "foo@example.com' OR '1'='1";
            String sql = "SELECT * FROM employee WHERE email = '" + injectedEmail + "'";
            //Uses table/column names, Unsafe if parameters are concatenated directly
            //Employee.class role, Map the results of this raw SQL to the Employee entity
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
            //createQuery: Uses entity class names and fields. inputs are always treated as values, not SQL code
            //Safe with parameters (:param)
            //Employee.class role, This query returns Employee objects — so return a List<Employee>. Defines expected return type of ORM-managed query
            //SELECT omitted.JPQL assumes the query starts with a select clause if not explicitly defined.
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

        Configuration config = new Configuration();
        config.addAnnotatedClass(Employee.class); //
        config.configure("hibernate.cfg.xml"); //
        SessionFactory factory = config.buildSessionFactory();

        // --- Create / Add ---
        Employee e1 = new Employee();
        e1.setFirstName("Test");
        e1.setLastName("User");
        e1.setEmail("test.user@example.com");
        e1.setPassword("123");
        e1.setFlagged(true);
        addEmployee(factory, e1);

//        // --- Read ---
//        Employee fetched = getEmployeeById(factory, e1.getId());
//        System.out.println("Fetched: " + fetched);
//
//        // --- Update ---
//        updateEmployeeEmail(factory, e1.getId(), "updated.user@example.com");
//
//        // --- Read after update ---
//        System.out.println("After update: " + getEmployeeById(factory, e1.getId()));
//
//        // --- List All ---
//        System.out.println("All Employees:");
//        listAllEmployees(factory);
//
//        // --- Delete ---
//        deleteEmployee(factory, e1.getId());
//        System.out.println("After deletion:");
//        listAllEmployees(factory);

//        SessionFactory factory = new Configuration()
//                .configure("hibernate.cfg.xml")
//                .addAnnotatedClass(Employee.class)
//                .buildSessionFactory();
//
//        Employee.runSqlInjectionDemo(factory);
//        Employee.runPreparedStatementDemo(factory);
//        Employee.runTransactionWithRollback(factory);

        factory.close();
    }
}
