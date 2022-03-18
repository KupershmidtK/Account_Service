package account.business.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public class UnlockUserRequest {
    @Email
    private String user;

    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;

    public String getUser() {
        return user;
    }

    public String getOperation() {
        return operation;
    }
}
