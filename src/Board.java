import java.io.Serializable;
import java.util.Observable;
import java.util.Random;


public class Board extends Observable implements Serializable{
   
   private int[][] board, temp;
   private int rows;
   private int cols;
   private int nr,nc, fr, fc, oldR, oldC;
   private boolean found;
   private int[] currentSpot=new int[2];
   //static boolean markedForEnemy;
   public Board(){
      super();
      rows=12;
      cols=12;
      board=new int[rows][cols];
      temp=new int[rows][cols];
      clearBoard();
      setUpBoard();
      
   }
   /**
    * Put enemies and potions to be determined in real time 
    * @param difficulty difficulty level of player game
    */
   public void PutEnemies(int difficulty) {
      Random rand=new Random();
      for(int r = 0; r < rows; r++){
         for(int c = 0; c < cols; c++){
            if(board[r][c]==0 && !(r==0 && c==0)){
               //do stuff
               if(rand.nextInt(10)<difficulty){
                  board[r][c]=3;//3 means enemy
               }
            }
            if(board[r][c]==0 && !(r==0 && c==0)){
               if(rand.nextInt(6)>=5){
                  board[r][c]=4;//4 means potion
               }
            }
            if(board[r][c]==0 && !(r==0 && c==0)){
               if(rand.nextInt(6)>=5){
                  board[r][c]=5;//5 means merchant
               }
            }
         }
         board[rows-1][cols-1]=5;
      }
      /*for(int r = 0; r < rows; r++){
         for(int c = 0; c < cols; c++){
            System.out.print(board[r][c]+" ");
         }
         System.out.println();
      }*/
      
      
   }
   public void clearBoard(){
      for(int r = 0; r < rows; r++){
         for(int c = 0; c < cols; c++){
            board[r][c]=0;
            temp[r][c]=0;
         }
         //spots[r]=0;
      }
            
      setChanged();
      notifyObservers();
   }
   /**
    * Sets up board randomly. 
    * Possible to never create a board
    * Verifies way found through
    * Allows multiple ways through possibly
    */
   public void setUpBoard(){
      Random rand=new Random();
      found=false;
      while(!found){
         for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
               if(r==0 && c==0) continue;//starting point
               board[r][c]=rand.nextInt(2);
               temp[r][c]=board[r][c];
               //System.out.print(board[r][c]+" ");
            }
            //System.out.println();
         }
         FindWay(0,0,rows-1,cols-1);//finds a path or not
         for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
               //System.out.print(temp[r][c]+" ");
               if(board[r][c]==2) board[r][c]=0; 
            }
            //System.out.println();
         }
      }
      currentSpot[0]=0;
      currentSpot[1]=0;
      
      
   }
   
    private void FindWay(int sr, int sc, int dr, int dc){ 
      if(sr==dr && sc==dc){//base case
         board[dr][dc]=2;//set destination as marked
         found=true;//found path
      }
      else{
         temp[sr][sc]=1;//set it as used
         while(!found && PossibleToMove(sr,sc)){//check for places
            FindWay(nr,nc,dr,dc);//recursion
            //if backtracking print
            //if(backtrack) System.out.println("Backtracking from ["+nr+", "+nc+"] to ["+sr+", "+sc+"].");
         }
         if(found) board[sr][sc]=2;//set path if found
      }
   }//end findWay
   /**
    * check if possible to go in any direction
    * otherwise start backtracking
    * sets next row and column 
    * @param sr source row
    * @param sc source column
    * @return
    */
    private boolean PossibleToMove(int sr, int sc){
      if(sc<rows-1 && temp[sr][sc+1]==0){//check east first 
         nr=sr;
         nc=sc+1;
         return true;
      }//east
      else if(sr>0 && temp[sr-1][sc]==0){//north next
         nr=sr-1;
         nc=sc;
         return true;
      }//north
      else if(sc>0 &&  temp[sr][sc-1]==0){//then west
        nr=sr;
        nc=sc-1;
        return true;
      }//west
      else if(sr<cols-1 &&  temp[sr+1][sc]==0){//last is south
        nr=sr+1;
        nc=sc;
        return true;
      }//south
      else{//if no place to go start backtracking
         //if just started backtracking do:
         //if(!backtrack) System.out.println("Start backtracking from ["+sr+", "+sc+"].");
         nr=sr;//set next to current one
         nc=sc;
         return false;//don't enter while
      }//no opening
   }//end possible move
    
    public int setPosition(int oldRow, int oldCol, int r, int c){
       board[oldRow][oldCol]=-1;
       int theReturn=board[r][c];
       board[r][c]=9;
       currentSpot[0]=r;
       currentSpot[1]=c;
       oldR=oldRow;
       oldC=oldCol;
       /*for(int rr = 0; rr < 8; rr++){
          for(int cc = 0; cc < 8; cc++){
             System.out.print(board[rr][cc]+" ");
          }
          System.out.println();
       }
       System.out.println();*/
    
       setChanged();
       notifyObservers();
       return theReturn;
    }
    public int[][] getNumbers(){
       return board;
    }
    public int checkFuturePosition(String e){
       int cr=currentSpot[0];
       int cc=currentSpot[1];
       //System.out.println(cr+"  "+ cc +"\n");
       if(e=="up") {
          if(cr==0) return -1;
          if(board[cr-1][cc]==1) return 1;
          else {
             fr=cr-1;
             return 0;
          }
          /*if(cr==0 || board[cr-1][cc]==1){
             if(board[cr-1][cc]==1) return 1;
             return -1;
          }
          else {
             fr=cr-1;
             return 0;
          }*/
          
       }
      
       if(e=="down") {
          if(cr==rows-1) return -1;
          if(board[cr+1][cc]==1) return 1;
          else {
             fr=cr+1;
             return 0;
          }
          /*if(cr==rows-1 || board[cr+1][cc]==1){
             if(board[cr+1][cc]==1) return 1;
             return -1;
          }
          else {
             fr=cr+1;
             return 0;
          }*/
       }
     
       if(e=="right"){
          if(cc==cols-1) return -1;
          else if( board[cr][cc+1]==1) return 1;
          else{
             fc=cc+1;
             return 0;
          }
          /*if(cc==cols-1 || board[cr][cc+1]==1){
             if(cc==cols-1) 
             if(board[cr][cc+1]==1) return 1;
             return -1;
          }
          else {
             fc=cc+1;
             return 0;
          }*/
       }
      
       if(e=="left"){
          if(cc==0) return -1;
          else if(board[cr][cc-1]==1) return 1;
          else{
             fc=cc-1;
             return 0;
          }
          /*if(cc==0 || board[cr][cc-1]==1){
             if(cc==0) return -1;
             else return 1;
          }
          else {
             fc=cc-1;
             return 0;
          }*/
       }
      
       return -1;
    }
    public int movePlayer(){
       return setPosition(currentSpot[0], currentSpot[1], fr, fc);
    }
    public int[] getCurrentSpot(){
       return currentSpot;
    }
    public int getOldRow() { return oldR;   }
    public int getoldCol() { return oldC;   }
   
    
    
    
    

}
