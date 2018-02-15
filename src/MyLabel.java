import javax.swing.JLabel;


public class MyLabel extends JLabel implements Runnable{
   private Thread thread;
   private int time;
   public MyLabel(String s){
      super(s);
      thread=new Thread(this);
   }
   public MyLabel(){
      super();
      thread=new Thread(this);
   }
   public void run(){
      String text=getText();
      for(int i=time;i>0;i--){
         setText(Integer.toString(i));
         try {
            sleep(1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      setText(text);
   }
   public void start(int i){
      time=i;
      thread.start();
   }
   public void interrupt(){
      thread.interrupt();
   }
   public boolean isAlive(){
      return thread.isAlive();
      
   }
   public void sleep(long millis) throws InterruptedException{
      Thread.sleep(millis);
   }

}
