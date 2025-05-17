package org.example.rt_p1;

import java.util.List;

// 1. convert class to an immutable class
// 2. make Employee class as a singleton
// 3. Docker installment and database (Postgres MongoDB Redis ElasticSearch Cassandra) volume attached
// 4. Maven
// 5. Version manager SDKman/Jenv
// 6. Homebrew

public class Employee {
    String lastName;
    String firstName;
    String email;
    Integer password;
    Boolean flagged;
    List<Integer> list;

    public String getLastName() {
        return lastName;
    }
}