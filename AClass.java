
public class AClass
{
    String name;
    
    public synchronized void syncMethod(){
        try{
            while (true){                
                AThread t = (AThread)Thread.currentThread();
                if (!t.enterSync) return;
                
                if (t.enterWait) {
                    t.enterWait = false; 
                    wait();
                }
                else Thread.sleep(1000);
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void normalMethod(){
        try{
            while (true){
                Thread.sleep(1000);
                AThread t = (AThread)Thread.currentThread();
                if (!t.enterNormal) return;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void syncAllMethod(){
        synchronized(AClass.class){
            try{
                while (true){                
                    AThread t = (AThread)Thread.currentThread();
                    if (!t.enterSyncAll) return;
                
                    Thread.sleep(1000);                
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
}
