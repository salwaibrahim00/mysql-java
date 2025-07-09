package recipes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import provided.util.DaoBase;
import recipes.exception.DbException;

public class RecipeDao extends DaoBase {

    public void executeBatch(List<String> sqlBatch) {
        try (Connection conn = DbConnection.getConnection()) {
            for (String sql : sqlBatch) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DbException("Error executing batch", e);
        }
    }

    public void insertRecipe(String recipeName, String notes) {
        String sql = "INSERT INTO recipe (recipe_name, notes) VALUES (?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recipeName);
            stmt.setString(2, notes);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DbException("Error adding recipe", e);
        }
    }
}
