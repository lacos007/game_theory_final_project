//=============================================================================
//PROGRAMMER1: Luis Acosta
// PANTHER ID1: 1729140
// CLASS: CAP4506
// SEMESTER: Spring 2020
// CLASSTIME: T/TH 6:25-7:40 pm
//
// Project: Series of functions to display and calculate normal form games
// DUE: Sunday, April 19, 2020
//
// CERTIFICATION: I certify that this work is my own and that
// none of it is the work of any other person.
//=============================================================================
package app;

import java.util.Scanner;

public class Controller {
    
    static int min_size = 1;
    static int max_size = 9;    
    static boolean inputState = true;
    static Scanner scan= new Scanner(System.in);
    
    static public int input_dimension(String type)
    {
        int dimension = 0;
        do 
        {
            System.out.printf("Enter the number of %s\n:", type);
            dimension = scan.nextInt();
            if( dimension >= min_size && dimension <= max_size)
                inputState = false;
        }while(inputState); 
        inputState = true;
 
        return dimension;
    }
     
    public static void main(String[] args) {
        Game current_game = null;
        
        do
        {
            System.out.println(" Enter (R)andom or (M)anual payoffs entries");
            char input =  scan.next().charAt(0);
  
            switch(input)
            {
                case 'R':
                case 'r':
                current_game = new Game(input_dimension("rows"), input_dimension("columns"), 'R');
                inputState = false;
                break;
                
                case 'M':
                case 'm':
                current_game = new Game(input_dimension("rows"), input_dimension("columns"), 'M');  
                inputState = false;
                break;
                
                default:
                System.out.println("Please enter R for Random or M for Manual Payoffs");
            }   
        }while(inputState == true);
    
        current_game.print_player_view();
        
        current_game.print_normal_form();
        
        current_game.print_nash_pure();
    }
}
