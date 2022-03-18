package account.business.response;

import account.database.model.PaymentEntity;

import java.util.List;

public class PaymentResponseView {
    String name;
    String lastname;
    String period;
    String salary;

    public PaymentResponseView(PaymentEntity paymentEntity) {
        this.name = paymentEntity.getEmployee().getName();
        this.lastname = paymentEntity.getEmployee().getLastname();
        setPeriod(paymentEntity.getPeriod());
        setSalary(paymentEntity.getSalary());
    }

    public void setPeriod(String period) {
        List<String> months = List.of("January", "February","March","April","May",
                "June","July","August","September","October","November","December");
        int monthIdx = Integer.parseInt(period.split("-")[0]);
        String  month = months.get(monthIdx - 1);
        this.period = period.replaceFirst("\\d{2}", month);
    }

    public void setSalary(long salary) {
        this.salary = String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPeriod() {
        return period;
    }

    public String getSalary() {
        return salary;
    }
}
