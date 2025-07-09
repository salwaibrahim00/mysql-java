package recipes;

import java.util.ArrayList;

public class scores {
    public static void main(String[] args) {
       ArrayList <Integer> score= new ArrayList<>();
       score.add (37);
       score.add(65);
       score.add(76);
      
    
       int total=0;
       for (int scores:score) {
       total += scores;
    }
       System.out.println(total);
       
       System.out.println(" my scores are"+ score);

        
}
}


    
