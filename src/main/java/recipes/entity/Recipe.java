package recipes.entity;

public class Recipe {
    private Integer id;
    private String recipeName;
    private Integer numServings;
    private Double prepTime;
    private Double cookTime;
    private String notes;
    
    // Default constructor
    public Recipe() {
    }
    
    // Constructor with parameters
    public Recipe(String recipeName, Integer numServings, Double prepTime, Double cookTime, String notes) {
        this.recipeName = recipeName;
        this.numServings = numServings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.notes = notes;
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getRecipeName() {
        return recipeName;
    }
    
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
    
    public Integer getNumServings() {
        return numServings;
    }
    
    public void setNumServings(Integer numServings) {
        this.numServings = numServings;
    }
    
    public Double getPrepTime() {
        return prepTime;
    }
    
    public void setPrepTime(Double prepTime) {
        this.prepTime = prepTime;
    }
    
    public Double getCookTime() {
        return cookTime;
    }
    
    public void setCookTime(Double cookTime) {
        this.cookTime = cookTime;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", recipeName='" + recipeName + '\'' +
                ", numServings=" + numServings +
                ", prepTime=" + prepTime +
                ", cookTime=" + cookTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}