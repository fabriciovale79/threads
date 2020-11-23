import java.util.*;
import java.util.stream.*;

public class TController implements AThread.TListener
{

    static List<String> objectNames;

    static List<String> threadNames;

    static{
        objectNames = Stream.of(new String[]{"igreja","padaria","fazenda","parque","ceu","quartel"}).collect(Collectors.toCollection(ArrayList::new));
        //Arrays.asList(new String[]{"igreja","padaria","fazenda","parque","ceu","quartel"});
        threadNames = Stream.of(new String[]{"dorinha","bispo","padreJoao","joaoGrilo","chico","frade","severinoDeAracaju","sacristao","palhaco",
                "eurico","cachorra","major","rosinha","cabo70","vincentao","emanuel","compadecida","diabo" }).collect(Collectors.toCollection(ArrayList::new));
        //Arrays.asList(new String[]{"dorinha","bispo","padreJoao","joaoGrilo","chico","frade","severinoDeAracaju","sacristao","palhaco",
        //"eurico","cachorra","major","rosinha","cabo70","vincentao","emanuel","compadecida","diabo" });
        Collections.shuffle(objectNames);
        Collections.shuffle(threadNames);
    }

    TListener listener;

    boolean goOn = false;

    Map<String,AThread> all = new HashMap<>();
    AClass sharedObject;
    public TController()
    {

    }

    public Map<String,AThread> getAll(){return all;}

    public void setTListener(TListener tl){
        listener = tl;
    }

    public void load(String... names){
        AClass object = new AClass();
        object.name = names[0];
        for (int i=1; i<names.length; i++){
            AThread t = new AThread(object,names[i], this);
            t.listener = this;
        }

    }

    public void load(int nthreads){
        if (nthreads > threadNames.size()) return;
        if (nthreads <= 0) return;
        String[]  parameters = new String[nthreads+1];
        parameters[0] = objectNames.remove(0);
        int index = 1;
        while (index <= nthreads){
            parameters[index] = threadNames.remove(0);
            index++;
        }

        load(parameters);

    }

    public void enterWait(String tname){
        try{
            all.get(tname).callWait();
        }catch(Exception e){}

    }

    public void callNotify(){
        try{
            synchronized (sharedObject) {sharedObject.notify();}
        }catch(Exception e){e.printStackTrace();}

    }

    public void enterNormalMethod(String tname){
        try{
            all.get(tname).enterNormalMethod();
        }catch(Exception e){}
    }

    public void enterSyncMethod(String tname){
        try{
            all.get(tname).enterNormalMethod();
        }catch(Exception e){}
    }

    public void enterSyncAllMethod(String tname){
        try{
            all.get(tname).enterSyncAllMethod();
        }catch(Exception e){}
    }

    public void add(AThread t){all.put(t.getName(),t); sharedObject = t.aObject;}

    public void startAll(){
        for (Thread t:all.values()) 
            t.start(); 

    }

    public void stopAll(){
        System.exit(0);
    }

    List<String[]> getThreadsStates(){
        String threadName="", objectName="", methodName="", status="";
        List<String[]> states = new ArrayList<>();
        for (AThread t: all.values()){
            //no method to call
            if (!t.enterSync && !t.enterSyncAll && !t.enterNormal && !t.enterWait) {
                System.out.println(t.getName() +"not running on "+sharedObject.name);
                continue;
            }

            StackTraceElement[] elements = t.getStackTrace();
            if (elements != null && elements.length>0){

                threadName = t.getName();

                if (elements[0].getMethodName().contains("sleep") ){
                    methodName = elements[1].getMethodName();
                    status = "RUNNING";
                    System.out.println(t.getName() +"."+ elements[1].getMethodName() +" -"+t.getState()+"-");
                }
                else if (elements[0].getMethodName().contains("wait") ){
                    methodName = elements[1].getMethodName();
                    status = t.getState().toString();
                    System.out.println(t.getName() +"."+ elements[1].getMethodName() +" -"+t.getState()+"-");
                }
                else{
                    methodName = elements[0].getMethodName();
                    status = t.getState().toString();
                    System.out.println(t.getName() +"."+ elements[0].getMethodName() +" -"+t.getState()+"-");
                }

                objectName = sharedObject.name;

            }
            states.add(new String[]{threadName,objectName, methodName, status});

        }
        return states;

    }

    public void initShowing(){
        new Thread(new Runnable(){
                public void run(){
                    try{
                        goOn = true;
                        while (goOn){
                            synchronized(TController.this){TController.this.wait();}
                            String threadName="", objectName="", methodName="", status="";
                            List<String[]> states = new ArrayList<>();
                            for (AThread t: all.values()){
                                //no method to call
                                if (!t.enterSync && !t.enterSyncAll && !t.enterNormal && !t.enterWait) {
                                    System.out.println(t.getName() +"not running on "+sharedObject.name);
                                    continue;
                                }

                                StackTraceElement[] elements = t.getStackTrace();
                                if (elements != null && elements.length>0){

                                    threadName = t.getName();

                                    if (elements[0].getMethodName().contains("sleep") ){
                                        methodName = elements[1].getMethodName();
                                        status = "RUNNING";
                                        System.out.println(t.getName() +"."+ elements[1].getMethodName() +" -"+t.getState()+"-");
                                    }
                                    else if (elements[0].getMethodName().contains("wait") ){
                                        methodName = elements[1].getMethodName();
                                        status = t.getState().toString();
                                        System.out.println(t.getName() +"."+ elements[1].getMethodName() +" -"+t.getState()+"-");
                                    }
                                    else{
                                        methodName = elements[0].getMethodName();
                                        status = t.getState().toString();
                                        System.out.println(t.getName() +"."+ elements[0].getMethodName() +" -"+t.getState()+"-");
                                    }

                                    objectName = sharedObject.name;

                                }
                                states.add(new String[]{threadName,objectName, methodName, status});

                            }
                            if (listener != null) listener.update(states, sharedObject.name);

                            //Thread.sleep(3000);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        ).start();

    }

    public synchronized void update(){
        notify();
    }

    public static interface TListener{
        public void update(List<String[]>  states, String objectName);
    }
}
