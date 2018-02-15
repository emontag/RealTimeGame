import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class MyThread extends Thread{
   Interface face;
   Player player;
   Game game;
   AtomicInteger[] ehp;
   int stunCount;
   public MyThread(Game g, Player p, AtomicInteger[] e, Interface f){
      super();
      game=g;
      ehp=e;
      player=p;
      face=f;
   }
   public boolean enemiesDead(){
      for(int i=0; i< ehp.length; i++){
         if(ehp[i].get()> 0) return false;
      }
      return true;
   }
   public void run(){
      int temp=0;
      for(int i=0; i< ehp.length; i++) temp+=ehp[i].get();
      stunCount=8;
      int[] arr={0,1,2,3,4,5,6,7,8,9,10};
      while(!enemiesDead() && player.getCurrentHp() > 0){
         face.flipTechniqueButtons();
         if (game.getGodMode()) game.godModeActivate();
         if(game.getPlayerStun()) {
            face.disableMoveButtons(arr);
            face.disablePotionUse();
            stunCount--;
         }
         if(stunCount==0){
            game.setPlayerStun(false);
            face.enableMoveButtons(arr);
            if(player.getNumberHPotion() > 0) face.enablePotionUse();
            stunCount=8;
         }
         try {
            sleep(500);
               
            
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      Game.inFight.set(false);
      face.disableMoveButtons(arr);
      face.disablePotionUse();
      Random r=new Random();
      player.setArmorBuff(0);
      if(player.getCurrentHp() <=0){
         game.lose();
         return;
      }
      else{
         face.PassAlongMessage("You win the fight! Keep going!\n");
         player.AddEXP(temp/2);
         while(player.getEXP()>=player.getXPLevel()) {//while level up
            player.setXPLevel();
            player.addLevel();
            if(game.getGodMode()) break;
            int temporary=player.getBaseCrit();
            player.setBaseCrit(player.getBaseCrit()+1);
            player.setCrit(player.getCrit()+player.getBaseCrit()-temporary);
            temporary=player.getBaseHp();
            player.setBaseHp((int) (player.getBaseHp()+25));
            player.setMaxHp(player.getMaxHp()+player.getBaseHp()-temporary);
            player.setCurrentHp(player.getMaxHp());
            temporary=player.getBaseMp();
            player.setBaseMp((int) (player.getBaseMp()+25));
            player.setMaxMp(player.getMaxMp()+player.getBaseMp()-temporary);
            player.setCurrentMp(player.getMaxMp());
            temporary=player.getBasePower();
            player.setBasePower((int) (player.getBasePower()+ player.getBasePower()*.25));
            player.setPower(player.getPower()+player.getBasePower()-temporary);//gear takes effect but multiplication on base
            face.updatePlayerPower(Integer.toString(player.getPower()));
            face.updatePlayerCrit(Integer.toString(player.getCrit()));
            player.setBaseArmor(player.getBaseArmor()+20*player.getLevel());
            player.setArmor(player.getBaseArmor());
         }
         if(r.nextInt(5)>=2) {
            Gear g=game.createArmor();
            player.addToInventory(g);
            if(g.getName().endsWith("s")) face.PassAlongMessage("New "+ g.getName() +" have been added to your inventory!\n");
            else face.PassAlongMessage("New "+ g.getName() +" has been added to your inventory!\n");
            Game.addCode();
         }
         else if(r.nextInt(5)>=2){
            Gear g=game.createWeapon();
            player.addToInventory(g);
            if(g.getName().endsWith("s")) face.PassAlongMessage("New "+ g.getName() +" have been added to your inventory!\n");
            else face.PassAlongMessage("New "+ g.getName() +" has been added to your inventory!\n");
            Game.addCode();
         }
         
      }
      int m=player.getMoney()+r.nextInt(temp)+50;
      player.setMoney(m);
      face.PassAlongMessage("You now have $"+ player.getMoney() +"!\n");
      face.allowMovement();
      player.setCurrentHp(player.getMaxHp());
      player.setCurrentMp(player.getMaxMp());
      face.updatePlayerHP(Integer.toString(player.getCurrentHp()));
      face.updatePlayerMP(Integer.toString(player.getCurrentMp()));
      face.updateLevelLog();
   }
}
