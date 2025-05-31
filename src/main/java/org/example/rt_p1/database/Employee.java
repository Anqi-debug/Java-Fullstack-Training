package org.example.rt_p1.database;

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

    @ManyToOne(fetch = FetchType.EAGER) // or FetchType.EAGER
    @JoinColumn(name = "department_id")// Foreign key column
    private Department department;

    // Getters and setters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Boolean getFlagged() { return flagged; }
    public Department getDepartment() {return department;}

    public void setId(int id) {this.id = id;}
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFlagged(Boolean flagged) { this.flagged = flagged; }
    public void setDepartment(Department department) {this.department = department;}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
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

    // --- SQL Injection Test Method ---
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

    // --- PreparedStatement (safe version) ---
    public static void runPreparedStatementDemo(SessionFactory factory) {
        Session session = factory.openSession();
        try {
            String safeEmail = "alice.smith@example.com";
            //createQuery: Uses entity class names and fields. inputs are always treated as values, not SQL code
            //Safe with parameters (:param)
            //Employee.class role, This query returns Employee objects — so return a List<Employee>. Defines expected return type of ORM-managed query
            //SELECT omitted.JPQL assumes the query starts with a select clause if not explicitly defined.
            Query<Employee> query = session.createQuery("FROM Employee WHERE email = :email", Employee.class);
            //runtime error: IllegalArgumentException: Named parameter [email OR '1'='1'] not found in the query string
            query.setParameter("email", safeEmail);
            List<Employee> safeResult = query.getResultList();
            System.out.println("✅ Safe result size: " + safeResult.size());
        } finally {
            session.close();
        }
    }

    // --- Transaction and Rollback ---
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

    // --- Stored Procedure Demo ---
    public static void storedProcedureDemo(SessionFactory factory, boolean flagStatus) {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        // PostgreSQL function call
        List<Employee> employees = session
                .createNativeQuery("SELECT * FROM get_employees(:flag)", Employee.class)
                .setParameter("flag", flagStatus)
                .getResultList();

        System.out.println("📋 Employees with flagged = " + flagStatus);
        employees.forEach(System.out::println);

        tx.commit();
        session.close();
    }

    //--- Trigger Demo ---
    public static void showAuditLogs(SessionFactory factory) {
        Session session = factory.openSession();
        List<Object[]> logs = session.createNativeQuery("SELECT * FROM employee_audit").getResultList();
        System.out.println("🕵️ Audit Logs:");
        for (Object[] row : logs) {
            System.out.println("ID: " + row[0] + ", EmployeeID: " + row[1] + ", Action: " + row[2] + ", Time: " + row[3]);
        }
        session.close();
    }

    public static void buffering(SessionFactory factory) {
        Session session = factory.openSession();
        //Same session, same ID
        Employee e1 = session.get(Employee.class, 11); // DB query
        Employee e2 = session.get(Employee.class, 11); // From buffer, no DB hit

        System.out.println(e1 == e2); // true (same object reference)
        session.close();
    }

    public static void buffering2(SessionFactory factory) {
        //Different sessions, same ID
        Session s1 = factory.openSession();
        Employee e1 = s1.get(Employee.class, 11); // From DB or s1 cache
        s1.close();

        Session s2 = factory.openSession();
        Employee e2 = s2.get(Employee.class, 11); // From DB or s2 cache
        s2.close();

        System.out.println(e1 == e2);      // false (different objects)
        //System.out.println(e1.equals(e2)); // true (if equals is overridden)
    }

    public static void lazyLoadingDemo(SessionFactory factory) {
        Session session = factory.openSession();

        // Load employee only (department not yet fetched)
        Employee emp = session.get(Employee.class, 11);
        System.out.println("🔍 Employee loaded: " + emp.getFirstName());

        System.out.println("📦 Accessing department...");
        Department dept = emp.getDepartment(); // ❗ Triggers SQL query here
        System.out.println("🏢 Department: " + dept.getName());

        session.close(); // ❗ Must access department before this or get LazyInitializationException
    }

    public static void eagerLoadingDemo(SessionFactory factory) {
        Session session = factory.openSession();

        // Load employee + department in a JOIN
        //load both Employee and its associated Department when the department field is annotated with in line 33

        // SELECT * FROM EmployeeTable where employeeId=11;
        Employee emp = session.get(Employee.class, 11);
        System.out.println("🔍 Employee loaded: " + emp.getFirstName());


        //
        Department dept = emp.getDepartment(); // Already loaded
        System.out.println("🏢 Department: " + dept.getName());

        session.close();
    }

    public static void main(String[] args) {

        Configuration config = new Configuration();
        config.addAnnotatedClass(Employee.class); //
        config.configure("hibernate.cfg.xml"); //
        SessionFactory factory = config.buildSessionFactory();

        // --- Fetch Department (from DB) ---
//        Session session = factory.openSession();
//        Department dept = session.get(Department.class, 100);
//        session.close();

        // --- Create / Add ---
//        Employee e1 = new Employee();
//        e1.setFirstName("Test12");
//        e1.setLastName("User12");
//        e1.setEmail("test12.user@example.com");
//        e1.setPassword("123");
//        e1.setFlagged(true);
//        e1.setDepartment(dept);
//      addEmployee(factory, e1);

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
//
//        Employee.runSqlInjectionDemo(factory);
//        Employee.runPreparedStatementDemo(factory);
//        Employee.runTransactionWithRollback(factory);
//        Employee.storedProcedureDemo(factory, true);
//        Employee.showAuditLogs(factory);
//        Employee.buffering(factory);
//        Employee.buffering2(factory);
        Employee.eagerLoadingDemo(factory);
//        Employee.lazyLoadingDemo(factory);

        factory.close();
    }
}
