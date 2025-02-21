public class ContactValidator {
    public static boolean isValid(Contact contact) {
        return contact != null &&
                isValidName(contact.getName()) &&
                isValidEmail(contact.getEmail()) &&
                isValidPhone(contact.getPhone()) &&
                isValidCompany(contact.getCompany()) &&
                isValidComment(contact.getComment());
    }

    private static boolean isValidName(String name) {
        return name != null &&
                !name.trim().isEmpty() &&
                name.length() <= AppConfig.MAX_NAME_LENGTH;
    }

    private static boolean isValidEmail(String email) {
        return email != null &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$") &&
                email.length() <= AppConfig.MAX_EMAIL_LENGTH;
    }

    private static boolean isValidPhone(String phone) {
        return phone != null &&
                phone.matches("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}") &&
                phone.length() <= AppConfig.MAX_PHONE_LENGTH;
    }

    private static boolean isValidCompany(String company) {
        return company == null || company.length() <= AppConfig.MAX_COMPANY_LENGTH;
    }

    private static boolean isValidComment(String comment) {
        return comment == null || comment.length() <= AppConfig.MAX_COMMENT_LENGTH;
    }
}
