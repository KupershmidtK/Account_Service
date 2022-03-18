package account.business.response;

import account.database.model.EmployeeEntity;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeResponseView {
    private long id;
    private String name;
    private String lastname;
    private String email;
    private List<String> roles;

    public EmployeeResponseView(EmployeeEntity employee) {
        this.id = employee.getId();
        this.name = employee.getName();
        this.lastname = employee.getLastname();
        this.email = employee.getEmail();
        this.roles = employee.getRoles().stream()
                .map(role -> role.getName())
                .sorted()
                .collect(Collectors.toList());
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
