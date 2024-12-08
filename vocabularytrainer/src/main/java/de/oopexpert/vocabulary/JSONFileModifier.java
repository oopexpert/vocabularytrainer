package de.oopexpert.vocabulary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JSONFileModifier {
    public static void main(String[] args) {
        String directoryPath = "statistics";
        int daysToAdd = 40; // Number of days to add to the timestamps

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    processJSONFile(file.getAbsolutePath(), daysToAdd);
                }
            }
        } else {
            System.out.println("Directory is empty or does not exist.");
        }
    }

    private static void processJSONFile(String filePath, int daysToAdd) {
        try {
            // Read JSON file
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();

            // Get the existing timestamps
            long lastQuestionnaire1 = jsonObject.get("lastQuestionnaire1").getAsLong();
            long lastQuestionnaire2 = jsonObject.get("lastQuestionnaire2").getAsLong();

            // Modify the timestamps by adding the specified number of days
            lastQuestionnaire1 = modifyTimestamp(lastQuestionnaire1, daysToAdd);
            lastQuestionnaire2 = modifyTimestamp(lastQuestionnaire2, daysToAdd);

            // Update the timestamps in the JSON object
            jsonObject.addProperty("lastQuestionnaire1", lastQuestionnaire1);
            jsonObject.addProperty("lastQuestionnaire2", lastQuestionnaire2);

            // Save the modified JSON back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(jsonObject));
            writer.close();

            System.out.println("JSON file modified and saved successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("Invalid JSON file format: " + filePath);
        }
    }

    private static long modifyTimestamp(long timestamp, long daysToAdd) {
		long three_days = daysToAdd * 24l * 60l * 60l * 1000l;
        return timestamp + three_days;
    }
}