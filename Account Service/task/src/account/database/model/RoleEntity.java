package account.database.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "role_group")
    private String group;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Set<EmployeeEntity> employee;

    public RoleEntity() { }

    public RoleEntity(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public String getName() {
        return name;
    }
}
