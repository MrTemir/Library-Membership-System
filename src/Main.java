import other.Files;
import other.Cipher;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Files fileUtils = new Files();
        Cipher cipher = new Cipher();
        String userFilePath = "resources/users.json"; // Specify the path to the user data file
        String idFilePath = "resources/before.json"; // Specify the path to the ID file

        // Load existing IDs from the before.json file
        Set<String> existingIds = fileUtils.loadIdsFromFile(idFilePath);

        // Load user data
        List<Map<String, Object>> usersData = fileUtils.readJsonFromFile(userFilePath, List.class);
        if (usersData == null) {
            usersData = new ArrayList<>();
        }

        while (true) { // Main program loop
            // Registration or login
            Map<String, Object> currentUser = null;
            while (currentUser == null) {
                System.out.println("===========================================");
                System.out.println(" Welcome to the Library Membership Control!");
                System.out.println("===========================================");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit Program");
                System.out.print("Your choice: ");
                int choice = Integer.parseInt(System.console().readLine());

                switch (choice) {
                    case 1:
                    // Registration
                    System.out.print("Enter your name: ");
                    String name = System.console().readLine();
                    while (name.trim().isEmpty()) {
                        System.out.print("Name cannot be empty. Enter your name: ");
                        name = System.console().readLine();
                    }
                
                    System.out.print("Enter your surname: ");
                    String surname = System.console().readLine();
                
                    System.out.print("Enter your phone: ");
                    String phone = System.console().readLine();
                
                    System.out.print("Enter your age: ");
                    int age = 0;
                    while (true) {
                        String ageInput = System.console().readLine();
                        if (ageInput.trim().isEmpty()) {
                            break; // Leave age empty
                        }
                        try {
                            age = Integer.parseInt(ageInput);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.print("Invalid age. Please enter a valid number: ");
                        }
                    }
                
                    System.out.print("Enter your password: ");
                    String password = System.console().readLine();
                    while (password.trim().isEmpty()) {
                        System.out.print("Password cannot be empty. Enter your password: ");
                        password = System.console().readLine();
                    }
                
                    // Generate a new ID
                    String userId;
                    do {
                        userId = cipher.generateMemberId();
                    } while (existingIds.contains(userId));
                    existingIds.add(userId);
                
                    // Determine if the user is an administrator
                    boolean isAdmin = usersData.isEmpty(); // The first user becomes an administrator
                
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("id", userId);
                    newUser.put("name", name);
                    newUser.put("surname", surname);
                    newUser.put("phone", phone);
                    newUser.put("age", age);
                    newUser.put("password", cipher.hashWithSHA256(password)); // Hash the password
                    newUser.put("isAdmin", isAdmin);
                    usersData.add(newUser);
                
                    fileUtils.writeJsonToFile(userFilePath, usersData); // Save users
                    fileUtils.saveIdsToFile(idFilePath, existingIds); // Save IDs
                    System.out.println("Registration successful! Your ID: " + userId);
                
                    // Automatic login after registration
                    currentUser = newUser;
                    break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

            // Check if the user is an administrator
            boolean isAdmin = (boolean) currentUser.get("isAdmin");

           // Main menu
            boolean isRunning = true;
            while (isRunning) {
                System.out.println("===========================================");
                System.out.println(" Welcome to the Library Membership Control!");
                System.out.println("===========================================");
                System.out.println("Please select an option:");
                System.out.println("-------------------------------------------");
                if (isAdmin) {
                    System.out.println("1. Add a new user (admin only)");
                    System.out.println("6. View all users (admin only)");
                    System.out.println("7. Delete a user (admin only)");
                }
                System.out.println("2. Update your information");
                System.out.println("3. View your information");
                System.out.println("4. Delete your account");
                System.out.println("5. Logout");
                System.out.println("8. Exit Program");
                System.out.println("-------------------------------------------");
                System.out.print("Your choice: ");
                int choice = 0;
                try {
                    choice = Integer.parseInt(System.console().readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        if (!isAdmin) {
                            System.out.println("Access denied. Only admins can add new users.");
                            break;
                        }
                        // Adding a new user
                        System.out.print("Enter new user's name: ");
                        String newName = System.console().readLine();
                        System.out.print("Enter new user's surname: ");
                        String newSurname = System.console().readLine();
                        System.out.print("Enter new user's phone: ");
                        String newPhone = System.console().readLine();
                        System.out.print("Enter new user's age: ");
                        int newAge = Integer.parseInt(System.console().readLine());
                        System.out.print("Enter new user's password: ");
                        String newPassword = System.console().readLine();

                        // Generate a new ID
                        String newUserId;
                        do {
                            newUserId = cipher.generateMemberId();
                        } while (existingIds.contains(newUserId));
                        existingIds.add(newUserId);

                        // Choose account type
                        System.out.print("Is this user an admin? (yes/no): ");
                        boolean newIsAdmin = System.console().readLine().equalsIgnoreCase("yes");

                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("id", newUserId);
                        newUser.put("name", newName);
                        newUser.put("surname", newSurname);
                        newUser.put("phone", newPhone);
                        newUser.put("age", newAge);
                        newUser.put("password", cipher.hashWithSHA256(newPassword)); // Hash the password
                        newUser.put("isAdmin", newIsAdmin);
                        usersData.add(newUser);

                        fileUtils.writeJsonToFile(userFilePath, usersData); // Save users
                        System.out.println("New user created successfully! User ID: " + newUserId);
                        break;

                    case 2:
                        // Update information
                        System.out.print("Enter new name (or press Enter to keep current): ");
                        String updatedName = System.console().readLine();
                        if (!updatedName.trim().isEmpty()) {
                            currentUser.put("name", updatedName);
                        }
                        System.out.print("Enter new surname (or press Enter to keep current): ");
                        String updatedSurname = System.console().readLine();
                        if (!updatedSurname.trim().isEmpty()) {
                            currentUser.put("surname", updatedSurname);
                        }
                        System.out.print("Enter new phone (or press Enter to keep current): ");
                        String updatedPhone = System.console().readLine();
                        if (!updatedPhone.trim().isEmpty()) {
                            currentUser.put("phone", updatedPhone);
                        }
                        System.out.print("Enter new age (or press Enter to keep current): ");
                        String updatedAgeStr = System.console().readLine();
                        if (!updatedAgeStr.trim().isEmpty()) {
                            try {
                                int updatedAge = Integer.parseInt(updatedAgeStr);
                                currentUser.put("age", updatedAge);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid age. Keeping current value.");
                            }
                        }
                        System.out.print("Enter new password (or press Enter to keep current): ");
                        String updatedPassword = System.console().readLine();
                        if (!updatedPassword.trim().isEmpty()) {
                            currentUser.put("password", cipher.hashWithSHA256(updatedPassword));
                        }

                        fileUtils.writeJsonToFile(userFilePath, usersData); // Save updates
                        System.out.println("Your information has been updated.");
                        break;

                    case 3:
                        // View information
                        System.out.println("ID: " + currentUser.get("id"));
                        System.out.println("Name: " + currentUser.get("name"));
                        System.out.println("Surname: " + currentUser.get("surname"));
                        System.out.println("Phone: " + currentUser.get("phone"));
                        System.out.println("Age: " + currentUser.get("age"));
                        System.out.println("Admin: " + currentUser.get("isAdmin"));
                        break;

                    case 4:
                        // Delete account
                        System.out.println("Are you sure you want to delete your account? (yes/no): ");
                        String confirm = System.console().readLine();
                        if (confirm.equalsIgnoreCase("yes")) {
                            usersData.remove(currentUser);
                            fileUtils.writeJsonToFile(userFilePath, usersData); // Save changes
                            System.out.println("Your account has been deleted.");
                            currentUser = null; // Reset current user
                            isRunning = false; // Exit menu
                        }
                        break;

                    case 5:
                        // Logout
                        System.out.println("Logging out...");
                        isRunning = false;
                        break;

                    case 6:
                        if (!isAdmin) {
                            System.out.println("Access denied. Only admins can view all users.");
                            break;
                        }
                        // View all users
                        if (usersData.isEmpty()) {
                            System.out.println("No users found.");
                        } else {
                            for (Map<String, Object> user : usersData) {
                                System.out.println("ID: " + user.get("id"));
                                System.out.println("Name: " + user.get("name"));
                                System.out.println("Surname: " + user.get("surname"));
                                System.out.println("Phone: " + user.get("phone"));
                                System.out.println("Age: " + user.get("age"));
                                System.out.println("Admin: " + user.get("isAdmin"));
                                System.out.println();
                            }
                        }
                        break;

                    case 7:
                        if (!isAdmin) {
                            System.out.println("Access denied. Only admins can delete users.");
                            break;
                        }
                        // Delete user
                        System.out.print("Enter the ID of the user to delete: ");
                        String deleteUserId = System.console().readLine();
                        Map<String, Object> userToDelete = null;
                        for (Map<String, Object> user : usersData) {
                            if (user.get("id").equals(deleteUserId)) {
                                userToDelete = user;
                                break;
                            }
                        }
                        if (userToDelete != null) {
                            usersData.remove(userToDelete);
                            fileUtils.writeJsonToFile(userFilePath, usersData); // Save changes
                            System.out.println("User with ID " + deleteUserId + " has been deleted.");
                        } else {
                            System.out.println("User not found.");
                        }
                        break;

                    case 8:
                        // Exit program
                        System.out.println("Exiting program. Goodbye!");
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }
}