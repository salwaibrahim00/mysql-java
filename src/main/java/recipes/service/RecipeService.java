package recipes.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import recipes.dao.RecipeDao;
import recipes.exception.DbException;

public class RecipeService {
    private static final String SCHEMA_FILE = "recipe_schema.sql"; // Make sure this file is in resources
    private RecipeDao recipeDao = new RecipeDao();

    public void createAndPopulatedTables() {
        loadFromFile(SCHEMA_FILE);
    }

    private void loadFromFile(String fileName) {
        String content = readFileContent(fileName);
        List<String> sqlStatements = convertContentToSqlStatements(content);
        sqlStatements.forEach(line -> System.out.println("SQL Statement: " + line));
        recipeDao.executeBatch(sqlStatements);
    }

    private List<String> convertContentToSqlStatements(String content) {
        content = removeComments(content);
        content = replaceWhitespaceSequenceWithSingleSpace(content);
        return extractLinesContent(content);
    }

    private List<String> extractLinesContent(String content) {
        List<String> lines = new LinkedList<>();
        while (!content.isEmpty()) {
            int semicolon = content.indexOf(";");
            if (semicolon == -1) {
                if (!content.isBlank()) {
                    lines.add(content.trim());
                }
                content = "";
            } else {
                lines.add(content.substring(0, semicolon).trim());
                content = content.substring(semicolon + 1);
            }
        }
        return lines;
    }

    private String replaceWhitespaceSequenceWithSingleSpace(String content) {
        return content.replaceAll("\\s+", " ");
    }

    private String removeComments(String content) {
        StringBuilder builder = new StringBuilder(content);
        int commentPos = 0;
        while ((commentPos = builder.indexOf("-- ", commentPos)) != -1) {
            int eolPos = builder.indexOf("\n", commentPos + 1);
            if (eolPos == -1) {
                builder.replace(commentPos, builder.length(), "");
            } else {
                builder.replace(commentPos, eolPos + 1, "");
            }
        }
        return builder.toString();
    }

    private String readFileContent(String fileName) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
            return java.nio.file.Files.readString(path);
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    // Updated addRecipe method to take 5 params
    public void addRecipe(String name, Integer servings, Double prepTime, Double cookTime, String notes) {
        String sql = "INSERT INTO recipe (recipe_name, num_serving, prep_time, cook_time, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = recipeDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            if (servings != null) {
                stmt.setInt(2, servings);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            if (prepTime != null) {
                stmt.setDouble(3, prepTime);
            } else {
                stmt.setNull(3, java.sql.Types.DOUBLE);
            }
            if (cookTime != null) {
                stmt.setDouble(4, cookTime);
            } else {
                stmt.setNull(4, java.sql.Types.DOUBLE);
            }
            if (notes != null) {
                stmt.setString(5, notes);
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DbException("Failed to insert recipe.");
            }
        } catch (SQLException e) {
            throw new DbException("Error adding recipe", e);
        }
    }
}
