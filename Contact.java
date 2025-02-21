import java.util.UUID;

public class Contact {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String comment;
    private String company;

    public Contact(String name, String phone, String email, String comment, String company) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.comment = comment;
        this.company = company;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getComment() { return comment; }
    public String getCompany() { return company; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setComment(String comment) { this.comment = comment; }
    public void setCompany(String company) { this.company = company; }

    @Override
    public String toString() {
        return String.format("Name: %s | Phone: %s | Email: %s | Company: %s | Comment: %s",
                name, phone, email, company, comment);
    }

    public String toFileString() {
        return String.join(":", id, name, phone, email, company, comment);
    }
}

