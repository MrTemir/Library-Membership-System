package other;
// Class for working with files: writing, reading, updating, and deleting data or files.

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Files { // JSON

    private final ObjectMapper mapper;

    public Files() {
        this.mapper = new ObjectMapper();
        // Configure the mapper (optional but useful)
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT); // For pretty JSON formatting
        // Additional settings can be added as needed
    }

    /**
     * Writes a Java object to a JSON file.
     *
     * @param filePath Path to the file.
     * @param data     Object to write in JSON format.
     * @param <T>      Type of the object.
     * @throws IOException If an I/O error occurs.
     */
    public <T> void writeJsonToFile(String filePath, T data) throws IOException {
        mapper.writeValue(new File(filePath), data);
    }

    /**
     * Reads JSON from a file and converts it to a Java object of the specified class.
     *
     * @param filePath  Path to the file.
     * @param valueType Class of the object to convert the JSON into.
     * @param <T>       Type of the object.
     * @return Object read from the JSON file, or null if the file is not found or an error occurs.
     * @throws IOException If an I/O error occurs during reading or conversion.
     */
    public <T> T readJsonFromFile(String filePath, Class<T> valueType) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            return mapper.readValue(file, valueType);
        }
        return null; // Or throw a FileNotFoundException
    }

    /**
     * Updates an existing JSON file by modifying the specified field.
     * Assumes the root element of the JSON is an object (Map).
     *
     * @param filePath  Path to the file.
     * @param fieldName Name of the field to update.
     * @param newValue  New value for the field.
     * @throws IOException If an I/O error occurs.
     */
    public void updateJsonFile(String filePath, String fieldName, Object newValue) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                // Read JSON as Map
                Map<String, Object> data = mapper.readValue(file, Map.class);
                // Update the value
                data.put(fieldName, newValue);
                // Write back to the file
                mapper.writeValue(file, data);
            } catch (IOException e) {
                throw new IOException("Error reading or updating the JSON file: " + e.getMessage(), e);
            }
        } else {
            throw new IOException("File not found: " + filePath);
        }
    }

    /**
     * Deletes the specified field from a JSON file.
     * Assumes the root element of the JSON is an object (Map).
     *
     * @param filePath  Path to the file.
     * @param fieldName Name of the field to delete.
     * @throws IOException If an I/O error occurs.
     */
    public void deleteFieldFromJsonFile(String filePath, String fieldName) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                // Read JSON as Map
                Map<String, Object> data = mapper.readValue(file, Map.class);
                // Remove the field
                data.remove(fieldName);
                // Write back to the file
                mapper.writeValue(file, data);
            } catch (IOException e) {
                throw new IOException("Error reading or deleting the field from the JSON file: " + e.getMessage(), e);
            }
        } else {
            throw new IOException("File not found: " + filePath);
        }
    }

    /**
     * Loads IDs from a JSON file.
     *
     * @param filePath Path to the file.
     * @return A set of unique IDs (Set<String>).
     * @throws IOException If an error occurs while reading the file.
     */
    public Set<String> loadIdsFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && file.length() > 0) { // Check if the file exists and is not empty
            List<String> idList = mapper.readValue(file, List.class);
            return new HashSet<>(idList); // Convert the list to a Set
        }
        return new HashSet<>(); // Return an empty Set if the file does not exist or is empty
    }

    /**
     * Saves IDs to a JSON file.
     *
     * @param filePath Path to the file.
     * @param ids      A set of unique IDs (Set<String>).
     * @throws IOException If an error occurs while writing to the file.
     */
    public void saveIdsToFile(String filePath, Set<String> ids) throws IOException {
        mapper.writeValue(new File(filePath), ids); // Write the Set as a JSON array
    }

    /**
     * Deletes a file.
     *
     * @param filePath Path to the file to delete.
     * @return true if the file was successfully deleted, false otherwise.
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }
}