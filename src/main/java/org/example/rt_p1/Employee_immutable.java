package org.example.rt_p1;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class Employee_immutable {
    public static void main(String[] args) {
        // Create an empty list of sub-employees
        List<Employee_immutable> subordinates = new ArrayList<>();

        // Create a sample subordinate
        Employee_immutable subordinate1 = new Employee_immutable(
                "Smith", "John", "john.smith@example.com", 1234, false, new ArrayList<>());
        Employee_immutable subordinate2 = new Employee_immutable(
                "Smith", "John", "john.smith@example.com", 1234, false, new ArrayList<>());

        //HashSet
        HashSet<Employee_immutable> set = new HashSet<>();
        set.add(subordinate1);
        set.add(subordinate2);

        // Add subordinate to the list
        subordinates.add(subordinate1);
        subordinates.add(subordinate2);

        // Create main employee with the subordinate list
        Employee_immutable manager = new Employee_immutable(
                "Doe", "Jane", "jane.doe@example.com", 5678, true, subordinates);
    }

    private final String lastName;
    private final String firstName;
    private final String email;
    private final Integer password;
    private final Boolean flagged;
    private final ArrayList<Employee_immutable> employees;

    public Employee_immutable(String lastName, String firstName, String email, Integer password, Boolean flagged, List<Employee_immutable> employees) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.flagged = flagged;
        this.employees = new ArrayList<>(employees);
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public Integer getPassword() {
        return password;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public List<Employee_immutable> getE() {
        return Collections.unmodifiableList(employees);
    }
}
