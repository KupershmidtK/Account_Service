package account.database;

import account.database.model.RoleEntity;
import account.database.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            if(roleRepository.count() == 0) {
                roleRepository.save(new RoleEntity("ROLE_ADMINISTRATOR", "ADMIN"));
                roleRepository.save(new RoleEntity("ROLE_USER", "USER"));
                roleRepository.save(new RoleEntity("ROLE_ACCOUNTANT", "USER"));
                roleRepository.save(new RoleEntity("ROLE_AUDITOR", "USER"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
