package org.example.rt_p1.StreamAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//Create an Employee class with two instance fields
class Employee {
    String name;
    int salary;

    public Employee(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        Employee emp = (Employee) obj;
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return this.name.equals(emp.name) && this.salary == emp.salary;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + this.salary;
    }
}

public class Main {
    public static void main(String[] args) {
        Employee emp1 = new Employee("John", 40000);
        Employee emp2 = new Employee("Jane", 20000);
        Employee emp3 = new Employee("Bob", 30000);
        Employee emp4 = new Employee("Annie", 90000);
        Employee emp5 = new Employee("Mary", 81000);

        List<Employee> employees = new ArrayList<Employee>();
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp4);
        employees.add(emp5);

        System.out.println(Ava_salary(employees));
        System.out.println(HighSalary(employees));
        System.out.println(employeeMap(employees));
        EmployeeNames(employees);
        System.out.println(EmployeeNameCon(employees));
    }

    //1. Calculate the average salary of the list of employees
    public static Double Ava_salary(List<Employee> employees) {
        return employees.stream().mapToInt(e -> e.salary).average().getAsDouble();
    }

    //2. Filter for employees whose salary exceeds 80000 and print out their names
    public static List<String> HighSalary(List<Employee> employees) {
        return employees.stream().filter(e -> e.salary > 80000)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    //3. Collect a Map of employees where the key is the name of employee (String) and the value is the employee object (Employee)
    public static Map<String, Employee> employeeMap(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.toMap(Employee::getName, employee -> employee));
    }

    //4. Find any Employee whose name starts with the character X
    // - If such an employee exists then print out the name, if not then print “no such employee exists”
    public static void EmployeeNames(List<Employee> employees) {
        Optional<String> result = employees.stream().map(Employee::getName)
                .filter(name -> name.startsWith("X"))
                .findAny();

        System.out.println(result.orElse("no such employee exists"));
    }

    //5. Create a String that contains the name of every single employee in the list concatenated together
    public static String EmployeeNameCon(List<Employee> employees) {
        return employees.stream()
                .map(Employee::getName)
                .collect(Collectors.joining());
    }
}
