package recipes;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.entity.Recipe;
import recipes.service.RecipeService;
import recipes.exception.DbException;

public class RecipeApp {
    private Scanner scanner = new Scanner(System.in);
    private RecipeService recipeService = new RecipeService();
    private Recipe currentRecipe;

    private List<String> operations = List.of(
        "1) Create and populate all tables",
        "2) Add a recipe",
        "3) List recipes",
        "4) Select current recipe",
        "5) Add ingredient to current recipe",
        "6) Update recipe details",
        "7) Delete a recipe"
    );

    public static void main(String[] args) {
        new RecipeApp().displayMenu();
    }

    private void displayMenu() {
        boolean done = false;

        while (!done) {
            int operation = getOperation();
            try {
                switch (operation) {
                    case -1:
                        done = exitMenu();
                        break;
                    case 1:
                        createTables();
                        break;
                    case 2:
                        addRecipe();
                        break;
                    case 3:
                        listRecipes();
                        break;
                    case 4:
                        setCurrentRecipe();
                        break;
                    case 5:
                        addIngredientToCurrentRecipe();
                        break;
                    case 6:
                        updateRecipeDetails();
                        break;
                    case 7:
                        deleteRecipe();
                        break;
                    default:
                        System.out.println("\n" + operation + " is not valid. Try again.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e.toString() + " Try again.");
            }
        }
    }

    private void createTables() {
        recipeService.createAndPopulatedTables();
        System.out.println("\nTables created and populated!");
    }

    private void addRecipe() {
        System.out.println("\n--- Add a New Recipe ---");

        String name = getStringInput("Enter recipe name");
        if (name == null) {
            System.out.println("Recipe name is required.");
            return;
        }

        Integer servings = null;
        try {
            servings = getIntInput("Enter number of servings");
        } catch (DbException e) {
            System.out.println("Invalid number for servings.");
            return;
        }

        Double prepTime = null;
        try {
            prepTime = getDoubleInput("Enter prep time in minutes");
        } catch (DbException e) {
            System.out.println("Invalid number for prep time.");
            return;
        }

        Double cookTime = null;
        try {
            cookTime = getDoubleInput("Enter cook time in minutes");
        } catch (DbException e) {
            System.out.println("Invalid number for cook time.");
            return;
        }

        String notes = getStringInput("Enter notes (optional)");

        recipeService.addRecipe(name, servings, prepTime, cookTime, notes);
        System.out.println("\nRecipe added successfully!");
    }

    private void listRecipes() {
        List<Recipe> recipes = recipeService.fetchAllRecipes();
        
        if (recipes.isEmpty()) {
            System.out.println("\nNo recipes found. Create some recipes first!");
            return;
        }
        
        System.out.println("\n=== Available Recipes ===");
        for (Recipe recipe : recipes) {
            System.out.printf("ID: %-3d | %-30s", recipe.getId(), recipe.getRecipeName());
            if (recipe.getNumServings() != null) {
                System.out.printf(" | Servings: %-2d", recipe.getNumServings());
            }
            if (recipe.getPrepTime() != null && recipe.getCookTime() != null) {
                System.out.printf(" | Time: %.0f + %.0f min", recipe.getPrepTime(), recipe.getCookTime());
            }
            System.out.println();
        }
        System.out.println("=========================");
    }

    private void setCurrentRecipe() {
        listRecipes();

        Integer recipeId = null;
        try {
            recipeId = getIntInput("\nEnter the ID of the recipe to select");
        } catch (DbException e) {
            System.out.println("Invalid recipe ID.");
            return;
        }

        if (recipeId == null) {
            System.out.println("No recipe selected.");
            return;
        }

        try {
            currentRecipe = recipeService.fetchRecipeById(recipeId);

            if (currentRecipe == null) {
                System.out.println("Recipe with ID " + recipeId + " not found.");
            } else {
                System.out.println("\n*** Recipe '" + currentRecipe.getRecipeName() + "' is now selected ***");
                displayCurrentRecipeDetails();
            }

        } catch (Exception e) {
            System.out.println("Error fetching recipe: " + e.getMessage());
        }
    }

    private void displayCurrentRecipeDetails() {
        if (currentRecipe == null) return;
        
        System.out.println("\n--- Current Recipe Details ---");
        System.out.println("Name: " + currentRecipe.getRecipeName());
        if (currentRecipe.getNumServings() != null) {
            System.out.println("Servings: " + currentRecipe.getNumServings());
        }
        if (currentRecipe.getPrepTime() != null) {
            System.out.println("Prep Time: " + currentRecipe.getPrepTime() + " minutes");
        }
        if (currentRecipe.getCookTime() != null) {
            System.out.println("Cook Time: " + currentRecipe.getCookTime() + " minutes");
        }
        if (currentRecipe.getNotes() != null && !currentRecipe.getNotes().trim().isEmpty()) {
            System.out.println("Notes: " + currentRecipe.getNotes());
        }
        System.out.println("-----------------------------");
    }

    private void addIngredientToCurrentRecipe() {
        if (currentRecipe == null) {
            System.out.println("\nNo recipe selected. Please select a recipe first (option 4).");
            return;
        }

        System.out.println("\n--- Add Ingredient to: " + currentRecipe.getRecipeName() + " ---");
        System.out.println("Feature not fully implemented yet.");
        System.out.println("This would allow you to add ingredients to the current recipe.");
        
        // Placeholder for future ingredient functionality
        String ingredientName = getStringInput("Enter ingredient name");
        if (ingredientName != null) {
            System.out.println("Ingredient '" + ingredientName + "' would be added to " + currentRecipe.getRecipeName());
            System.out.println("(This is just a preview - actual database insertion not implemented yet)");
        }
    }

    private void updateRecipeDetails() {
        if (currentRecipe == null) {
            System.out.println("\nPlease select a recipe first.");
            return;
        }

        System.out.println("\n--- Update Recipe Details ---");
        System.out.println("Current recipe: " + currentRecipe.getRecipeName());
        System.out.println("\nEnter new values (press Enter to keep current value):");
        
        // Show current values and get new ones
        System.out.println("1) Recipe name (" + currentRecipe.getRecipeName() + ")");
        String newName = getStringInput("Enter new recipe name");
        
        System.out.println("2) Number of servings (" + currentRecipe.getNumServings() + ")");
        Integer newServings = null;
        try {
            newServings = getIntInput("Enter new number of servings");
        } catch (DbException e) {
            System.out.println("Invalid number for servings, keeping current value.");
        }
        
        System.out.println("3) Prep time (" + currentRecipe.getPrepTime() + " minutes)");
        Double newPrepTime = null;
        try {
            newPrepTime = getDoubleInput("Enter new prep time in minutes");
        } catch (DbException e) {
            System.out.println("Invalid number for prep time, keeping current value.");
        }
        
        System.out.println("4) Cook time (" + currentRecipe.getCookTime() + " minutes)");
        Double newCookTime = null;
        try {
            newCookTime = getDoubleInput("Enter new cook time in minutes");
        } catch (DbException e) {
            System.out.println("Invalid number for cook time, keeping current value.");
        }
        
        System.out.println("5) Notes (" + currentRecipe.getNotes() + ")");
        String newNotes = getStringInput("Enter new notes");

        // Create updated recipe object
        Recipe updatedRecipe = new Recipe();
        updatedRecipe.setId(currentRecipe.getId());
        updatedRecipe.setRecipeName(newName != null ? newName : currentRecipe.getRecipeName());
        updatedRecipe.setNumServings(newServings != null ? newServings : currentRecipe.getNumServings());
        updatedRecipe.setPrepTime(newPrepTime != null ? newPrepTime : currentRecipe.getPrepTime());
        updatedRecipe.setCookTime(newCookTime != null ? newCookTime : currentRecipe.getCookTime());
        updatedRecipe.setNotes(newNotes != null ? newNotes : currentRecipe.getNotes());

        // Update the recipe
        recipeService.modifyRecipeDetails(updatedRecipe);
        
        // Re-fetch the updated recipe
        currentRecipe = recipeService.fetchRecipeById(currentRecipe.getId());
        
        System.out.println("\nRecipe updated successfully!");
        displayCurrentRecipeDetails();
    }

    private void deleteRecipe() {
        listRecipes();

        Integer recipeId = null;
        try {
            recipeId = getIntInput("\nEnter the ID of the recipe to delete");
        } catch (DbException e) {
            System.out.println("Invalid recipe ID.");
            return;
        }

        if (recipeId == null) {
            System.out.println("No recipe ID entered.");
            return;
        }

        // Confirm deletion
        String confirm = getStringInput("Are you sure you want to delete this recipe? (yes/no)");
        if (!"yes".equalsIgnoreCase(confirm)) {
            System.out.println("Delete operation cancelled.");
            return;
        }

        // Delete the recipe
        recipeService.deleteRecipe(recipeId);
        System.out.println("\nRecipe with ID " + recipeId + " was deleted successfully!");

        // Clear current recipe if it was the one deleted
        if (currentRecipe != null && currentRecipe.getId().equals(recipeId)) {
            currentRecipe = null;
            System.out.println("Current recipe selection cleared.");
        }
    }

    private boolean exitMenu() {
        System.out.println("\nExiting the Recipe Manager. TTFN!");
        return true;
    }

    private int getOperation() {
        printOperation();
        if (currentRecipe != null) {
            System.out.println("\nCurrently selected recipe: " + currentRecipe.getRecipeName());
        }
        Integer op = getIntInput("\nEnter an operation number (press Enter to quit)");
        return Objects.isNull(op) ? -1 : op;
    }

    private void printOperation() {
        System.out.println();
        System.out.println("========== Recipe Manager ==========");
        System.out.println("Here's what you can do:");
        operations.forEach(op -> System.out.println("  " + op));
        System.out.println("====================================");
    }

    private Integer getIntInput(String prompt) {
        String input = getStringInput(prompt);
        if (Objects.isNull(input)) {
            return null;
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number.");
        }
    }

    private Double getDoubleInput(String prompt) {
        String input = getStringInput(prompt);
        if (Objects.isNull(input)) {
            return null;
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number.");
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        String line = scanner.nextLine();
        return line.isBlank() ? null : line.trim();
    }
}