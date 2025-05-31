package org.example.rt_p1.database;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 *     relationship size | access probability          |
 *       1: 1            |     Always / sometime/ rare |   eager/lazy
 *       1:Many          |      sometime               |   lazy
 *       1:Many          |      Always                 |   eager ---> memory usage ----> GC ----> performance decrement
 *
 *
 *     wide table (Medicine)  ---> table with hundreds of columns ----> one of those column (chemical compound) ----> List<ChemicalCompount>
 *
 *     customer: order (manny) 1 c can have 10K historical order
 *     UI -> customer profile:
 *     address,
 *     phone number,
 *     name,
 *     age,
 *     credit card info,
 *     historical orders
 *
 *
 */


@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(nullable = false)
    private int depid;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Employee> employees;

    public int getId() {return id;}
    public String getName() {return name;}
    public List<Employee> getEmployees() {return employees;}
    public int getDepid() {return depid;}

    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setEmployees(List<Employee> employees) {this.employees = employees;}
    public void setDepid(int depid) {this.depid = depid;}

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", depid=" + depid +
                ", employees=" + employees +
                '}';
    }

    public static void lazyLoadingDemo(SessionFactory factory) {
        Session session = factory.openSession();

        Department dept = session.get(Department.class, 100); // Only Department is loaded
        System.out.println("🔍 Department loaded: " + dept.getName());

        System.out.println("📦 Accessing employees...");
        List<Employee> emps = dept.getEmployees(); // Triggers SELECT for employees (lazy)
        for (Employee e : emps) {
            System.out.println("👤 Employee: " + e.getFirstName() + " " + e.getLastName());
        }

        session.close(); // Do NOT access after this if LAZY
    }

    public static void eagerLoadingDemo(SessionFactory factory) {
        Session session = factory.openSession();

        //Because employees are annotated with FetchType.EAGER, Hibernate automatically fetches the associated employees at the same time
        Department dept = session.get(Department.class, 100); // Loads Department + Employees
        System.out.println("🚀 Department loaded: " + dept.getName());

        System.out.println("📦 Employees loaded with department:");
        for (Employee e : dept.getEmployees()) {
            System.out.println("👤 Employee: " + e.getFirstName() + " " + e.getLastName());
        }

        session.close(); // Safe to access because employees were preloaded
    }

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");
        config.addAnnotatedClass(Employee.class);
        config.addAnnotatedClass(Department.class);

        SessionFactory factory = config.buildSessionFactory();

        lazyLoadingDemo(factory);
//        eagerLoadingDemo(factory);
        factory.close();
    }

}

