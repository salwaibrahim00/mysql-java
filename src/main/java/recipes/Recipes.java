package recipes;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.service.RecipeService;
import recipes.exception.DbException;

public class Recipes {
    private Scanner scanner = new Scanner(System.in);
    private RecipeService recipeService = new RecipeService();

    private List<String> operations = List.of(
        "1) Create and populate all tables",
        "2) Add a recipe"
    );

    public static void main(String[] args) {
        new Recipes().displayMenu();
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
        System.out.println("\nTables created and populated");
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

    private boolean exitMenu() {
        System.out.println("\nExiting the menu. TTFN!");
        return true;
    }

    private int getOperation() {
        printOperation();
        Integer op = getIntInput("\nEnter an operation number (press Enter to quit)");
        return Objects.isNull(op) ? -1 : op;
    }

    private void printOperation() {
        System.out.println();
        System.out.println("Here's what you can do:");
        operations.forEach(op -> System.out.println("  " + op));
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
