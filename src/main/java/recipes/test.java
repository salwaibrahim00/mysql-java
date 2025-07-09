package recipes;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        ArrayList<student> students = new ArrayList<>();

        students.add(new student("Salwa", 45));
        students.add(new student("Yamin", 88));
        students.add(new student("Sami", 74));
         int total= 0;
        for (student String : students) {
        	total+= String.getGrade();
        	double average= total/students.size();
        	System.out.println( String .getName()+" " + "average is" +" "+ average);
    }
    }
}
