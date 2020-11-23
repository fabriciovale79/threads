
public class AThread extends Thread
{
    boolean enterSync = false;
    boolean enterSyncAll = false;
    boolean enterNormal = false;
    boolean enterWait = false;
    
    AClass aObject;
    
    TListener listener = new TListener(){ public void update(){}};
    
    public AThread(AClass a, String name, TController tg){
        super(name);
        aObject = a;
        tg.add(this);
    }
    
    public void run(){
        //System.out.println("running "+getName());
        while (true){
            try{
                
                if (enterSync){
                    //System.out.println("calling sync "+getName());
                    aObject.syncMethod();
                    listener.update();
                    //System.out.println("exited sync "+getName());
                }
                if (enterSyncAll){
                    //System.out.println("calling syncAll "+getName());
                    
                    aObject.syncAllMethod();
                    listener.update();
                    //System.out.println("exited syncAll "+getName());
                }
                if (enterNormal){
                    //System.out.println("calling normal "+getName());
                    aObject.normalMethod();
                    listener.update();
                    //System.out.println("exited normal "+getName());
                }
                Thread.sleep(1000);
                
                
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
            
        }
    }
    
    public void callWait(){
        if (enterSync)
            enterWait = true;
    }
    
    //public void wakeUp(){
    //    enterWait = false;
    //    synchronized(this){
    //        notify();
    //    }
    //}
    
    public void exitRunningMethod(){
        enterNormal = enterSync = enterSyncAll = enterWait = false;
        
        //callWait();
    }
    
    public void enterSyncMethod(){        
        exitRunningMethod();
        enterSync = true;
        //wakeUp();
    }
    
    public void enterSyncAllMethod(){        
        exitRunningMethod();
        enterSyncAll = true;
        //wakeUp();
    }
    
    
    public void enterNormalMethod(){
       exitRunningMethod();
       enterNormal = true;
       //wakeUp();
    }
    
    static interface TListener{
        public void update();
    }
    
}
