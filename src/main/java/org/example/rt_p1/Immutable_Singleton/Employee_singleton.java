package org.example.rt_p1.Immutable_Singleton;

import java.util.ArrayList;
import java.util.List;

public class Employee_singleton {
    private String lastName;
    private String firstName;
    private String email;
    private Integer password;
    private Boolean flagged;
    private List<Integer> list;

    // Step 1: Create the single static instance
    private static final Employee_immutable INSTANCE = new Employee_immutable("Smith", "John", "john.smith@example.com", 1234, false, new ArrayList<>());

    // Step 2: Make the constructor private
    private Employee_singleton() {
        // Optionally initialize fields
    }

    // Step 3: Provide a public static method to access the instance
    public static Employee_immutable getInstance() {
        return INSTANCE;
    }

    // Getters
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

    public List<Integer> getList() {
        return list;
    }

}
