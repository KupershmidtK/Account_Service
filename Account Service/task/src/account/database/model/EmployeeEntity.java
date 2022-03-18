package account.database.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
public class EmployeeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    private String lastname;
    private String password;

    @Column()
    private Boolean locked;

    @Column(name = "failed_attempt")
    private Integer failedAttemptCnt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "empl_roles",
            joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<RoleEntity> roles = new HashSet<>();

    public EmployeeEntity() { }

    public EmployeeEntity(String name, String lastname, String email, String password, Set<RoleEntity> roles) {
        this.name = name;
        this.lastname = lastname;
        this.email = email.toLowerCase();
        this.password = password;
        this.roles = roles;
        locked = false;
        failedAttemptCnt = 0;
    }

    public Long getId() {
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

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean addRole(RoleEntity role) {
        return roles.add(role);
    }

    public boolean removeRole(RoleEntity role) {
        return roles.remove(role);
    }

    public boolean hasRole(RoleEntity role) {
        return roles.stream().anyMatch(r-> r.getName().equals(role.getName()));
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public Boolean isLocked() {
        return locked;
    }

    public void lockUser() {
        this.locked = true;
    }

    public void unlockUser() {
        this.locked = false;
        failedAttemptCnt = 0;
    }

    public Integer getFailedAttemptCnt() {
        return failedAttemptCnt;
    }

    public void increaseFailedAttemptCnt() {
        failedAttemptCnt++;
    }

    public void clearFailedAttemptCnt() {
        failedAttemptCnt = 0;
    }
}
