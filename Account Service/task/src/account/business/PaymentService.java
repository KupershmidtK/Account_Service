package account.business;

import account.business.request.PaymentRequest;
import account.database.model.EmployeeEntity;
import account.database.model.PaymentEntity;
import account.database.repository.PaymentsRepository;
import account.database.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    PaymentsRepository paymentsRepository;
    @Autowired
    EmployeeRepository userRepository;

    public Map<String, String> save(List<PaymentRequest> listOfPaymentRequest) {
        List<PaymentEntity> paymentEntityList = listOfPaymentRequest.stream()
                .map(this::createNewPaymentFromRequest)
                .collect(Collectors.toList());
        try {
            paymentsRepository.saveAll(paymentEntityList);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error saving user payments");
        }
        return Map.of("status", "Added successfully!");
    }

    public Map<String, String> change(PaymentRequest paymentRequest) {
        try {
            EmployeeEntity emp = userRepository.findByEmail(paymentRequest.getEmployee()).get(0);
            String period = paymentRequest.getPeriod();
            PaymentEntity paymentEntity = paymentsRepository.findByEmployeeEntityAndPeriod(emp, period).get(0);
            paymentEntity.setSalary(paymentRequest.getSalary());

            paymentsRepository.save(paymentEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error updating user payments");
        }
        return Map.of("status", "Updated successfully!");
    }

    public List<PaymentEntity> getListOfPayments(String email, String period) {
        EmployeeEntity emp = userRepository.findByEmail(email).get(0);
        List<PaymentEntity> paymentEntityList;
        if (period == null) {
            paymentEntityList = paymentsRepository.findByEmployeeEntity(emp);
        } else {
            if (!period.matches("(0\\d|1[012])-\\d{4}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect period format " + period);
            }
            paymentEntityList = paymentsRepository.findByEmployeeEntityAndPeriod(emp, period);
        }

        if(paymentEntityList.size() > 1) {
            paymentEntityList.sort((p1, p2) -> {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern("MM-yyyy")
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter();
                LocalDate d1 = LocalDate.parse(p1.getPeriod(), formatter);
                LocalDate d2 = LocalDate.parse(p2.getPeriod(), formatter);
                return d2.compareTo(d1);
            });
        }
        return paymentEntityList;
    }

    private PaymentEntity createNewPaymentFromRequest(PaymentRequest paymentRequest) {
        try {
            EmployeeEntity emp = userRepository.findByEmail(paymentRequest.getEmployee()).get(0);
            PaymentEntity paymentEntity = new PaymentEntity();
            paymentEntity.setEmployee(emp);
            paymentEntity.setPeriod(paymentRequest.getPeriod());
            paymentEntity.setSalary(paymentRequest.getSalary());
            return paymentEntity;
        } catch (IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User " + paymentRequest.getEmployee() + " doesn't exist");
        }
    }
}
