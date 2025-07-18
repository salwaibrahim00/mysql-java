package recipes.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import recipes.entity.Recipe;
import recipes.dao.DbConnection;
import recipes.exception.DbException;

public class RecipeService {
    private static final String SCHEMA_FILE = "recipe_schema.sql";

    public void createAndPopulatedTables() {
        loadFromFile(SCHEMA_FILE);
    }

    private void loadFromFile(String fileName) {
        String content = readFileContent(fileName);
        List<String> sqlStatements = convertContentToSqlStatements(content);
        sqlStatements.forEach(line -> System.out.println("SQL Statement: " + line));
        executeBatch(sqlStatements);
    }

    private void executeBatch(List<String> sqlBatch) {
        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);
            
            try (Statement stmt = conn.createStatement()) {
                for (String sql : sqlBatch) {
                    stmt.addBatch(sql);
                }
                stmt.executeBatch();
                commitTransaction(conn);
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException("Error executing batch", e);
            }
        } catch (SQLException e) {
            throw new DbException("Error getting connection", e);
        }
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

    public void addRecipe(String name, Integer servings, Double prepTime, Double cookTime, String notes) {
        String sql = "INSERT INTO recipe (recipe_name, num_serving, prep_time, cook_time, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            if (servings != null) stmt.setInt(2, servings);
            else stmt.setNull(2, java.sql.Types.INTEGER);

            if (prepTime != null) stmt.setDouble(3, prepTime);
            else stmt.setNull(3, java.sql.Types.DOUBLE);

            if (cookTime != null) stmt.setDouble(4, cookTime);
            else stmt.setNull(4, java.sql.Types.DOUBLE);

            if (notes != null) stmt.setString(5, notes);
            else stmt.setNull(5, java.sql.Types.VARCHAR);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DbException("Failed to insert recipe.");
            }
        } catch (SQLException e) {
            throw new DbException("Error adding recipe", e);
        }
    }

    public List<Recipe> fetchAllRecipes() {
        String sql = "SELECT * FROM recipe";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Recipe> recipes = new ArrayList<>();
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("recipe_id"));
                recipe.setRecipeName(rs.getString("recipe_name"));
                recipe.setNumServings(rs.getInt("num_serving"));
                recipe.setPrepTime(rs.getDouble("prep_time"));
                recipe.setCookTime(rs.getDouble("cook_time"));
                recipe.setNotes(rs.getString("notes"));
                recipes.add(recipe);
            }
            return recipes;

        } catch (SQLException e) {
            throw new DbException("Error fetching recipes", e);
        }
    }

    public Recipe fetchRecipeById(Integer recipeId) {
        String sql = "SELECT * FROM recipe WHERE recipe_id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("recipe_id"));
                    recipe.setRecipeName(rs.getString("recipe_name"));
                    recipe.setNumServings(rs.getInt("num_serving"));
                    recipe.setPrepTime(rs.getDouble("prep_time"));
                    recipe.setCookTime(rs.getDouble("cook_time"));
                    recipe.setNotes(rs.getString("notes"));
                    return recipe;
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DbException("Error fetching recipe by ID", e);
        }
    }

    public void modifyRecipeDetails(Recipe recipe) {
        if (!updateRecipe(recipe)) {
            throw new DbException("Recipe with ID=" + recipe.getId() + " does not exist.");
        }
    }

    public void deleteRecipe(Integer recipeId) {
        if (!deleteRecipeById(recipeId)) {
            throw new DbException("Recipe with ID=" + recipeId + " does not exist.");
        }
    }

    private boolean updateRecipe(Recipe recipe) {
        String sql = "UPDATE recipe SET recipe_name = ?, notes = ?, num_serving = ?, prep_time = ?, cook_time = ? WHERE recipe_id = ?";
        
        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, recipe.getRecipeName());
                stmt.setString(2, recipe.getNotes());
                
                if (recipe.getNumServings() != null) {
                    stmt.setInt(3, recipe.getNumServings());
                } else {
                    stmt.setNull(3, java.sql.Types.INTEGER);
                }
                
                if (recipe.getPrepTime() != null) {
                    stmt.setDouble(4, recipe.getPrepTime());
                } else {
                    stmt.setNull(4, java.sql.Types.DOUBLE);
                }
                
                if (recipe.getCookTime() != null) {
                    stmt.setDouble(5, recipe.getCookTime());
                } else {
                    stmt.setNull(5, java.sql.Types.DOUBLE);
                }
                
                stmt.setInt(6, recipe.getId());
                
                int rowsAffected = stmt.executeUpdate();
                commitTransaction(conn);
                
                return rowsAffected == 1;
                
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException("Error updating recipe", e);
            }
        } catch (SQLException e) {
            throw new DbException("Error getting connection", e);
        }
    }

    private boolean deleteRecipeById(Integer recipeId) {
        String sql = "DELETE FROM recipe WHERE recipe_id = ?";
        
        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, recipeId);
                
                int rowsAffected = stmt.executeUpdate();
                commitTransaction(conn);
                
                return rowsAffected == 1;
                
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException("Error deleting recipe", e);
            }
        } catch (SQLException e) {
            throw new DbException("Error getting connection", e);
        }
    }

    // Helper methods for transaction management
    private void startTransaction(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    private void commitTransaction(Connection conn) throws SQLException {
        conn.commit();
    }

    private void rollbackTransaction(Connection conn) throws SQLException {
        conn.rollback();
    }
}