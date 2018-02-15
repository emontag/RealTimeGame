import java.util.concurrent.atomic.AtomicInteger;


public class EnemyMonitor extends Thread{
   private Enemy[] enemy;
   private AtomicInteger[] ehp; 
   public EnemyMonitor(Enemy[] enemy2, AtomicInteger[] e){
      super();
      enemy=enemy2;
      ehp=e;
   }
   
   public void run(){
      while (Game.inFight.get()){
         try {
            Thread.sleep(500); 
            for(int i=0; i< enemy.length; i++){
               if(!enemy[i].isAlive() && ehp[i].get()>0) {
                  //System.out.println("Resetting enemy "+ i);
                  enemy[i].run();
               }
            }
            
            
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

}
