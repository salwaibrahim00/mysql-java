package recipes;

public class student {
    private String name;
    private int grade;

    public student(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getGrade() {
       return grade;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}


