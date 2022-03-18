package account.business.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public class ChangeRoleRequest {
    @Email
    String user;
    @Pattern(regexp = "ADMINISTRATOR|USER|ACCOUNTANT|AUDITOR",
            message = "Role must be one of ADMINISTRATOR | USER | ACCOUNTANT | AUDITOR")
    String role;
    @Pattern(regexp = "GRANT|REMOVE", message = "Operation must be one of GRANT | REMOVE")
    String operation;

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public String getOperation() {
        return operation;
    }
}
