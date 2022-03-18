package account.database.repository;

import account.database.model.EmployeeEntity;
import account.database.model.PaymentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentsRepository extends CrudRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByEmployeeEntityAndPeriod(EmployeeEntity empl, String period);
    List<PaymentEntity> findByEmployeeEntity(EmployeeEntity empl);
}
