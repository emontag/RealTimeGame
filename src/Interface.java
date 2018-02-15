import java.io.Serializable;
import java.util.List;


public abstract class Interface implements Serializable{

   public abstract void howToPlay();
   protected Player player;
   protected  volatile boolean noKeys=false, radioBool; 
   protected  Interface currentGUI;
   public abstract void PassAlongMessage(String m);
   public abstract void dispose(); 
   public abstract void updatePlayerHP(String hp); 
   public abstract void updatePlayerMP(String mp);
   public abstract void updatePlayerPower(String p);
   public abstract void updatePlayerSpeed(String s);
   public abstract void updatePlayerPotion(String p);
   public abstract void swapButtonStatus(int[] arr);
   public abstract void enablePotionUse();
   public abstract void disablePotionUse();
   public abstract void PassAlongLogMessage(String string);
   public abstract int askForUpgrade();
   public abstract void preventMovement();
   public abstract void allowMovement();
   public abstract void playerStun();
   public abstract void shutdown(); 
   public Object getSubclass(){
      return currentGUI;
   }
   public abstract void setUpArchenemy();
   public abstract void WinMessage(String string);
   public abstract void load();
   public abstract void disableMoveButtons(int[] arr); 
   public abstract void enableMoveButtons(int[] arr);
   public abstract void updatePlayerAccuracy(String string);
   public abstract void updatePlayerCrit(String string);
   public abstract void callMerchant(Gear gear);
   public abstract void updateLevelLog();
   public abstract void flipTechniqueButtons();
   public abstract void createEnemyList(List l);
   public abstract boolean isRadioBool();   
   public abstract void setRadioBool(boolean b);
   public abstract void markIfWall(String event);
   public abstract void PassAlongEnemyMessage(String m);
      
   
      
      
        
   
   
   
   
      

}
