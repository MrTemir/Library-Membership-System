package other;

public class Lib_member {
    String id;          // ID
    int age;            // Age
    String name;        // Name
    String surname;     // Surname
    String password;    // Password
    String phone;       // Phone
    boolean isadmin;    // Is admin

    // Constructor
    public Lib_member(String id, int age, String name, String surname, String password, String phone, boolean isadmin) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.isadmin = isadmin;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public boolean isIsadmin() {
        return isadmin;
    }
    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }
}