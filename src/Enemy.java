import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public  class Enemy extends Thread implements Serializable{
   protected int currentmp,  maxmp, power, speed;
   protected AtomicInteger currenthp,maxhp;
   protected float crit_chance;
   protected AtomicBoolean enemyStun, enemyMiss, enemyDebuff;
   public AtomicInteger getCurrentHp(){ return currenthp;}
   public int getMaxHp(){ return maxhp.get();}
   public int getCurrentMp(){ return currentmp;}
   public int getMaxMp(){ return maxmp;}
   public int getPower(){ return power;}
   public int getSpeed(){ return speed;}
   public void setCurrentHp(int num){ currenthp.set(num);}
   public void setCurrentMp(int num){ currentmp=num ;}
   public void setmaxHp(int num){ maxhp.set(num); ;}
   public void setMaxMp(int num){ maxmp=num ;}
   public void setPower(int num){ power=num ;}
   public void setSpeed(int num){ speed=num ;}
   public void setCrit(float num){ crit_chance=num ;}
   protected Interface face;
   protected Player player;
   //protected int[] ehp;
   private String[] names={"Knight", "Lich", "Archer", "Grim Reaper", "Goblin", "Garwin", "Verpal", "Tarka", "Xiplo", "Hesla", "Levhow", "Grampus", "Julhow", "Rewdog", "Zaraganbah"};
   private String type;
   protected Random random;
   protected Game game;
   protected int eligibleToStun=5, number;
   public Enemy(Game g, Player p, Interface f, int n){
      //make sure different types randomize stats
      enemyStun=new AtomicBoolean();
      enemyMiss=new AtomicBoolean();
      enemyDebuff=new AtomicBoolean();
      enemyStun.set(false);
      enemyMiss.set(false);
      currenthp=new AtomicInteger();
      maxhp=new AtomicInteger();
      face=f;
      player=p;
      game=g;
      random=new Random();
      currenthp.set(1);
      currenthp.set((random.nextInt(100)+50)*game.getDifficulty());
      maxhp.set(currenthp.get());
      //ehp=new int[1];
      type=names[random.nextInt(names.length)];
      number=n;
      face.PassAlongEnemyMessage(type + " HP is "+ currenthp.get()+".\n");
   }
   /*public void setEHP(int[] e){
      ehp=e;
   }*/
   public void run() {
      while(face.isRadioBool()) {};
      while(Game.inFight.get()){
         if(!Game.inFight.get()) return;
         if(game.getEHP(number)<= 0){
            face.PassAlongEnemyMessage("Enemy " + type + " is dead!\n" );
            return;
         }
         if (game.getGodMode()) game.godModeActivate();
         eligibleToStun--;
         if(enemyStun.get()) {
            face.PassAlongEnemyMessage("Enemy is stunned!\n");
            try {
               Thread.sleep(5000);
               enemyStun.set(false);;
            } catch (Exception e) {
               e.printStackTrace();
            }
            if(Game.inFight.get()) face.PassAlongEnemyMessage("Enemy is no longer stunned.\n");
         }
         if(!Game.inFight.get()) return;
        /* boolean permission;
         do{
            permission=Game.playerSemaphore.tryAcquire();//only add if available
         }while(!permission);*/
         try {
            int speed=player.getSpeed()/3;
            if(random.nextInt(100) > speed){
               if(enemyMiss.get() || game.getShielded()){
                  if(enemyMiss.get()){
                     face.PassAlongEnemyMessage(type+" attack Misses. \n");
                     enemyMiss.set(false);
                     //Game.playerSemaphore.release();
                     Thread.sleep(random.nextInt(3)*1000);
                     
                  }
                  else if (game.getShielded()){
                     face.PassAlongMessage(type+" attack is shielded. \n");
                     game.setShielded(false);
                     //Game.playerSemaphore.release();
                     Thread.sleep(random.nextInt(3)*1000);
                  }
         
                  return;
               }
               if(random.nextInt(10) >=9 && eligibleToStun <= 0) {
                  //stun for certain seconds
                  game.setPlayerStun(true);
                  eligibleToStun=30;
                  face.PassAlongEnemyMessage("You have been stunned!\n");
                  //face.playerStun();
               }
               else if (random.nextInt(20) >=19){
                  game.setPlayerMiss(true);
               }
               else if (random.nextInt(20) >=19){
                  game.setPlayerDebuff(true);
               }
            }
            else {
               face.PassAlongEnemyMessage(type+" attack Misses.\n");
               //Game.playerSemaphore.release();
               Thread.sleep(random.nextInt(3)*1000);
               return;
            }
            power=random.nextInt(8+(6*player.getLevel()));
            if(power<9*player.getLevel()) power*=2;
            if(enemyDebuff.get()){
               enemyDebuff.set(false);;
               power=(int) (power*.75);
            }
            power-=(player.getArmor().get()/5);
            if(power<0) power=0;
            face.PassAlongEnemyMessage(type + " hits you with attack and does "+power+" damage.\n");
            player.setCurrentHp(player.getCurrentHp()-power);
            face.updatePlayerHP(Integer.toString(player.getCurrentHp()));
            face.PassAlongEnemyMessage("You now have " + player.getCurrentHp()+ " HP left.\n");
            //Game.playerSemaphore.release();
            Thread.sleep(random.nextInt(3)*1000+1000);
     
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
        
   }
   
   /**
    * @return the enemyStun
    */
   public boolean isEnemyStun() {
      return enemyStun.get();
   }
   /**
    * @param enemyStun the enemyStun to set
    */
   public void setEnemyStun(boolean e) {
      enemyStun.set(e);
   }
   /**
    * @return the enemyMiss
    */
   public boolean isEnemyMiss() {
      return enemyMiss.get();
   }
   /**
    * @param enemyMiss the enemyMiss to set
    */
   public void setEnemyMiss(boolean e) {
      enemyMiss.set(e);;
   }
   /**
    * @return the enemyDebuff
    */
   public boolean isEnemyDebuff() {
      return enemyDebuff.get();
   }
   /**
    * @param enemyDebuff the enemyDebuff to set
    */
   public void setEnemyDebuff(boolean e) {
      enemyDebuff.set(e);;
   }
   public String getType(){
      return type;
   }
   
}
