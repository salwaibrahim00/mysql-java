package recipes;

import java.util.ArrayList;

public class foodfavorte {
	   public static void main(String[] args) {
	   
		ArrayList<String>foods= new ArrayList<>();
		foods.add ("bread");
		foods.add("salt");
		foods.add("milk");
		for(String food:foods) {
			System.out.println(" i love " + food);
		}
		
		   System.out.println(" number of items" + foods.get(1));
		   
	   }
}
