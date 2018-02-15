
public class ManaRegeneration extends Thread{
   private Player player;
   private Interface face;
   public ManaRegeneration(Player p, Interface f){
      super();
      player=p;
      face=f;
   }
   
   public void run(){
      while (Game.inFight.get()){
         try {
            Thread.sleep(2000);
            player.setCurrentMp((int) (player.getCurrentMp()+(player.getMaxMp()*.25)));
            face.updatePlayerMP(Integer.toString(player.getCurrentMp()));
            face.flipTechniqueButtons();
            if(player.getArmorBuff().get() > 0){
               player.setArmor(player.getBaseArmor()+player.getLevel()*2);
               player.setArmorBuff(player.getArmorBuff().get()-1);   
            }
            else{
               player.setArmor(player.getBaseArmor());
            }
            
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

}
