import javax.swing.JButton;


public class MyButtons extends JButton implements Runnable{
   private Thread thread;
   private int time;
   private GUI gui;
   private Game game;
   private Player player;
   public MyButtons(String s, GUI g){
      super(s);
      thread=new Thread(this);
      gui=g;
   }
   public MyButtons(GUI g, Game ga, Player p){
      super();
      thread=new Thread(this);
      gui=g;
      game=ga;
      player=p;
   }
   public void run(){
      //int [] ehp=game.getEHP();
      //PlayerAttack attack=new PlayerAttack(getName(), ehp, gui, game, player);
      //attack.start();
      setEnabled(false);
      String text=getText();
      for(int i=time;i>0;i--){
         if(!Game.inFight.get()) break;
         removeMouseListener(gui);
         setEnabled(false);
         setText(Integer.toString(i));
         sleep(1000);
      }
      addMouseListener(gui);
      
      setText(text);
      if (Game.inFight.get()) 
         setEnabled(true);
      else 
         setEnabled(false);
      
      
   }
   public void start(int i){
      thread=new Thread(this);
      time=i;
      thread.start();
   }
   public void interrupt(){
      thread.interrupt();
   }
   public boolean isAlive(){
      return thread.isAlive();
      
   }
   public void sleep(long millis){
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
   
   

}
