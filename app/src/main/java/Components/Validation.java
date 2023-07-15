package Components;

public class Validation {
    public static boolean signUpValidation(String fName, String lName, String email, String password, String confirmPassword) {
        if(fName.equals("") || lName.equals("") || email.equals("") || !verifyPassword(password, confirmPassword)) {
            return false;
        }
        return true;
    }

    private static boolean verifyPassword(String password, String confirmPassword) {
        if(password.equals("") || confirmPassword.equals("")) return false;
        return password.equals(confirmPassword);
    }

    public static boolean isValidSignInInput(String email, String password) {
        if(email.equals("") || password.equals("")) return false;
        return true;
    }

}
