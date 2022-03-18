package account.business.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

public class PaymentRequest {
    @Email
    private String employee;

    @Pattern(regexp = "(0\\d|1[012])-\\d{4}", message = "Wrong date format!")
    private String period;

    @Min(value = 0, message = "Salary must be non negative!")
    private Long salary;

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
