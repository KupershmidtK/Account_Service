package account.business;

import java.util.Map;
import java.util.Set;

public class EmployeeRequestValidator {
    public static boolean isValidRequest(Map<String, String> request) {
        final int PASSWORD_LENGTH = 12;

        if (!request.keySet().equals(Set.of("name", "lastname", "email", "password"))) {
            return false;
        }

        if (request.values().stream().anyMatch(String::isEmpty)) {
            return false;
        }

        if (request.get("password").trim().length() < PASSWORD_LENGTH) {
            return false;
        }

        if(!request.get("email").matches(".*@acme.com$")) {
            return false;
        }

        return true;
    }

    public static boolean isValidPasswordLen(String password) {
        final int PASSWORD_LENGTH = 12;
        if (password.trim().length() < PASSWORD_LENGTH) {
            return false;
        }
        return true;
    }
}
