package app;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom; 

public class Game {
    static Scanner scan = new Scanner(System.in);
    String input;
    
    public int generate_random(int bottom, int top)
    { return ThreadLocalRandom.current().nextInt(bottom, top); }
    
    private final int[][] one_board;
    private final int[][] two_board;
        
    private final int[][] one_nash;
    private final int[][] two_nash;
    
    private final ArrayList<ArrayList<Integer>> nash_pure_strats;

    private final ArrayList<Integer> mix_list_one;
    private final ArrayList<Integer> mix_list_two;
    
    int rows = 0;
    int columns = 0;
    
    Game(int input_rows, int input_columns, char type)
    {
        rows = input_rows;
        columns = input_columns;
        one_board = new int[rows][columns];
        two_board = new int[rows][columns];
        
        one_nash = new int[rows][columns];
        two_nash = new int[rows][columns];
        
        nash_pure_strats = new ArrayList<>();
        
        mix_list_one = new ArrayList();
        mix_list_two = new ArrayList();
        
        if(type == 'R')
        {
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < columns; j++)
            {
                one_board[i][j] = generate_random(-99, 99);
                two_board[i][j] = generate_random(-99, 99);
            }
        }
        else if(type == 'M')
        {
            System.out.println("Manual Entries");
            for (int i = 0; i < rows; i++) 
            {
                for(int j = 0; j < columns; j++)
                {
                    System.out.printf("Enter payoff for ( A%d,B%d ) = ", i, j);
                    input = scan.next();
                    one_board[i][j] = ( (int) input.charAt(0) ) - 48;
                    two_board[i][j] = ( (int) input.charAt(2) ) - 48;
                    //ASCII offset in case you're wondering 
                }
                System.out.println("---------------------------");
            }
        }
    }

    public void print_strategy_profile(char player)
    {
        System.out.printf("{");
            for( int i=0; i < rows; i++)
                System.out.printf("%c%d,", player, i);
        System.out.println("}");
    }
    
    public void print_payoffs(int[][] board)
    {
        for(int[] row: board)
            {
            System.out.println("");
            if (row != null)
                for (int col: row)
                    System.out.printf("%3d, ", col);
            }
    }
    
    public void print_player_view()
    {
        System.out.println("------------------------------------\n"
                         + "Player: Player1's strategies\n" +
                           "------------------------------------\n");
        print_strategy_profile('A');               
        System.out.println("\n------------------------------------\n" +
                           "Player: Player1's payoffs\n" +
                           "------------------------------------");
        print_payoffs(one_board);
        
        System.out.println("\n\n------------------------------------\n"
                         + "Player: Player2's strategies\n" +
                           "------------------------------------\n");
        print_strategy_profile('B');               
        System.out.println("\n------------------------------------\n" +
                           "Player: Player2's payoffs\n" +
                           "------------------------------------");
        print_payoffs(two_board); 
    }
    
    private void print_strategy_rows()
    {
        for (int i = 0; i < rows; i++) {
            System.out.printf("       B%d      ", i);
        }
        System.out.println("");
    }
    
    private void print_spacers()
    {
        for (int i = 0; i < columns; i++) {
            System.out.print("   ------------");
        }
        System.out.println("");
    }
    
    private void print_board(int[][] first_board, int[][] second_board)
    {
        for (int i = 0; i < rows; i++)        
        {
            print_spacers();
            System.out.printf("A%d", i);
            for (int j = 0; j < columns; j++) 
            {
                if(first_board[i][j]== 100 && second_board[i][j] == 100)
                   System.out.printf("|  (  H,  H)  ");
                else if(first_board[i][j]== 100)
                   System.out.printf("|  (  H,%3d)  ", second_board[i][j]);
                else if(second_board[i][j] == 100)
                   System.out.printf("|  (%3d,  H)  ", first_board[i][j]);    
                else
                   System.out.printf("|  (%3d,%3d)  ", first_board[i][j], second_board[i][j]);
            }
            System.out.print("|");
            System.out.println("");
        }
        print_spacers();
    }
    
    public void print_normal_form()
    {
        System.out.println("\n=======================================\n" +
                             "Display Normal Form\n" +
                             "=======================================");
        print_strategy_rows();
        print_board(one_board, two_board);
    }
    
    private void copy_payoffs(int[][] origional, int[][] destination)
    {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                destination[i][j] = origional[i][j];
    }

    private void calculate_nash_pure()
    {
        copy_payoffs(one_board, one_nash);
        copy_payoffs(two_board, two_nash);

        //minimal sentinel value, should never show 
        int reset_find = -100;
        int nash_max = reset_find;
        
        //maximal sentinel value, should never show
        final int mark_nash = 100;
        
        //player one search
        for (int i = 0; i < columns; i++) {
            nash_max = reset_find;  
            for (int j = 0; j < rows; j++) {
                if(one_nash[j][i] > nash_max){
                    nash_max = one_nash[j][i];
                    //System.out.println(one_nash[j][i]);
                }
            }
            for (int k = 0; k < rows; k++) {
                if (one_nash[k][i] == nash_max) {
                    one_nash[k][i] = mark_nash;  
                    //System.out.println(one_nash[i][k]);
                }
            }
        }
        
        //player two search     
        for (int x = 0; x < rows; x++) {
            nash_max = reset_find; 
            for (int y = 0; y < columns; y++) {
                if(two_nash[x][y] > nash_max)
                    nash_max = two_nash[x][y]; 
            }
            for (int z = 0; z < columns; z++) {
                if (two_nash[x][z] == nash_max) {
                    two_nash[x][z] = mark_nash;
                }
            }
        }
        
        //populate nash eq strategies 
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(one_nash[i][j] == mark_nash && two_nash[i][j] == mark_nash)
                    nash_pure_strats.add(new ArrayList<>(Arrays.asList(i,j)));
            }
        }
        
    }
    
    public void print_nash_pure()
    {
        System.out.println("\n=======================================\n" +
                           "Nash Pure Equilibrium Locations\n" +
                           "=======================================");
        calculate_nash_pure();
        print_strategy_rows();
        print_board(one_nash,two_nash);
        System.out.print("Nash Pure Equilibrium(s): ");
        System.out.println(" " + nash_pure_strats.toString());
    }
    
    private void populate_list(ArrayList<Integer> input_list)
    {
        int sum = 0;
        for (int i = 0; i < rows; i++) {
            input_list.add(generate_random(0,1));
            sum += input_list.get(i);
        }
    }
    
    private void generate_mixing_probs()
    {
        
    }
    
    public void print_expected_payoffs()
    {
        
    }
    
    public void print_expected_values()
    {
    }
    
    public void print_mixed_nash()
    {
    
    }
    
}