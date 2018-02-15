import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class DOT extends Thread{
   //while in fight and enemy is alive apply the Dot
   //2 DOT's in array
   private AtomicInteger ehp;
   private int dmg;
   private Interface face;
   private int time, row, col;
   private volatile boolean[][] enemyDOT;
   private String eName;
   private Player player;
   public DOT(AtomicInteger e, int d, Interface f, int t, boolean[][] enemyD, int r, int c, String eN, Player p){
      ehp=e;
      dmg=d;
      face=f;
      time=t;
      enemyDOT=enemyD;
      row=r;
      col=c;
      eName=eN;
      player=p;
   }
   public void run(){
      enemyDOT[row][col]=true;
      Random r=new Random();
      try{
         for(int i=0; i< time; i++){
            if(!Game.inFight.get() || ehp.get()<=0) break;
            player.setCurrentHp(player.getCurrentHp()+dmg/2);
            //if(r.nextInt(100) >= player.getAccuracy()){
               ehp.set(ehp.get()-dmg);
               face.PassAlongMessage("Your DOT effect does "+dmg + " damage to the "+ eName + ".\n");
           // }
           // else{
           //    face.PassAlongMessage("Your DOT has no effect.\n");
           // }
            if(ehp.get()<=0) face.PassAlongMessage("Your DOT effect kills " +eName+".\n");
            sleep(2000);
         }
      }catch(Exception e){
         e.printStackTrace();
      }finally{
         enemyDOT[row][col]=false;
      }
      return;
      
   }

}
