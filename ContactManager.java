import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class ContactManager {
    private final Map<String, Contact> contacts = new ConcurrentHashMap<>();
    private final File contactsFile;

    public ContactManager() {
        this.contactsFile = new File(AppConfig.CONTACTS_FILE);
        loadContacts();
    }

    public synchronized void addContact(Contact contact) {
        contacts.put(contact.getId(), contact);
        saveContacts();
    }

    public synchronized void updateContact(Contact contact) {
        if (contacts.containsKey(contact.getId())) {
            contacts.put(contact.getId(), contact);
            saveContacts();
        }
    }

    public synchronized void deleteContact(String id) {
        contacts.remove(id);
        saveContacts();
    }

    public Contact getContact(String id) {
        return contacts.get(id);
    }

    public Collection<Contact> getAllContacts() {
        return new ArrayList<>(contacts.values());
    }

    public List<Contact> searchContacts(String query) {
        String lowercaseQuery = query.toLowerCase();
        return contacts.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(lowercaseQuery) ||
                        c.getEmail().toLowerCase().contains(lowercaseQuery) ||
                        c.getPhone().contains(query) ||
                        (c.getCompany() != null && c.getCompany().toLowerCase().contains(lowercaseQuery)))
                .toList();
    }

    private void loadContacts() {
        if (!contactsFile.exists()) {
            try {
                contactsFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating contacts file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(contactsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 6) {
                    Contact contact = new Contact(parts[1], parts[2], parts[3], parts[5], parts[4]);
                    contacts.put(parts[0], contact);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading contacts: " + e.getMessage());
        }
    }

    private void saveContacts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(contactsFile))) {
            for (Contact contact : contacts.values()) {
                writer.write(contact.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving contacts: " + e.getMessage());
        }
    }
}

