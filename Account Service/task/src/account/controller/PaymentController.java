package account.controller;

import account.business.request.PaymentRequest;
import account.business.response.PaymentResponseView;
import account.business.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Validated
public class PaymentController {
    @Autowired
    PaymentService service;

    @PostMapping("/api/acct/payments")
    public Map<String, String> uploadPayroll(@RequestBody List<@Valid PaymentRequest> listOfPaymentRequests) {
        return service.save(listOfPaymentRequests);
    }

    @PutMapping("/api/acct/payments")
    public Map<String, String> changeSalary(@Valid @RequestBody PaymentRequest paymentRequest) {
        return service.change(paymentRequest);
    }

    @GetMapping("/api/empl/payment")
    public ResponseEntity<?> getPayment(@RequestParam Map<String, String> params, @AuthenticationPrincipal UserDetails details) {
        String email = details.getUsername();
        String period = params.get("period");

        List<PaymentResponseView> responseViews = service.getListOfPayments(email, period)
                .stream()
                .map(PaymentResponseView::new)
                .collect(Collectors.toList());

        if(responseViews.size() == 1) {
            return ResponseEntity
                    .ok()
                    .body(responseViews.get(0));
        }
        return ResponseEntity
                .ok()
                .body(responseViews);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
