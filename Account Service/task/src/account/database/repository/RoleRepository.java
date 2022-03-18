package account.database.repository;

import account.database.model.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    List<RoleEntity> findByName(String roleName);
}
