import javax.swing.*;
import javax.swing.border.*;
//import java.awt.*;
import java.awt.event.*;
import java.awt.GridLayout;
import java.util.*;
import java.util.stream.*;
import java.io.*;

public class ThreadsUI
{
    static String html1;

    static String html2 = "<tr> "+
        "<td><span style='font-weight:bold'>%s</span></td>"+
        "<td><span style='color:blue;font-weight:bold'>%s</span>.<span style='color:red;font-weight:bold'>%s</span></td>"+
        "<td><span style='font-weight:bold'>%s</span></td>"+    
        "</tr>";

    static String html3 = "</table>"+
        "</body>"+
        "</html>";

    static{
        try{
            File file = new File("html1.txt");
            byte[] bytes = new byte[(int)file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(bytes);
            in.close();
            html1 = new String(bytes,"UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    JFrame frame = new JFrame("Explaining sync methods");
    
    Map<String,ATextPane> textPanes = new HashMap<>();

    JMenuBar bar = new JMenuBar();

    JMenu menuObjects = new JMenu("Shared Objects");

    JMenu menuThreads = new JMenu("Threads");

    java.util.Map<String, TController> models = new HashMap<>();

    public ThreadsUI()
    {
        initModel();
        initUIComponents();
        startModel();
    }

    void startModel(){
        models.values().forEach((m)->{
                m.startAll();
                //m.initShowing();
            });
    }

    synchronized void refreshTables(){
        textPanes.forEach((k,pane) ->{
                StringBuilder builder = new StringBuilder(html1);
                List<String[]> states = models.get(k).getThreadsStates();
                states.forEach((s)->{
                        String line = String.format(html2,s[0],s[1],s[2],s[3]);
                        builder.append(line);
                    });
                builder.append(html3);
                String toShow = builder.toString();
                pane.setText(toShow);

            });
    }

    void sheduleRefresh(){
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    refreshTables();
                }
            }, 
            1500 
        );
    }

    void initModel(){
        TController model1 = new TController(), model2 = new TController();
        model1.load(3);
        model2.load(3);
        models.put(model1.sharedObject.name, model1);
        models.put(model2.sharedObject.name, model2);
        //model1.startAll();
        //model2.startAll();
        //model1.initShowing();
        //model2.initShowing();

    }

    void clickedNotify(TController model){
        model.callNotify();
        sheduleRefresh();
    }

    void clickedCallWait(AThread t){
        t.callWait();
        sheduleRefresh();
    }

    void clickedExitRunningMethod(AThread t){
        t.exitRunningMethod();
        sheduleRefresh();
    }

    void clickedCallNormal(AThread t){
        t.enterNormalMethod();
        sheduleRefresh();
    }
    
    void clickedCallNormal(TController tc){
        tc.all.forEach((k,t) -> t.enterNormalMethod());
        sheduleRefresh();
    }
    
    void clickedCallSync(TController tc){
        tc.all.forEach((k,t) -> t.enterSyncMethod());
        sheduleRefresh();
    }
    
    void clickedCallSyncAll(TController tc){
        tc.all.forEach((k,t) -> t.enterSyncAllMethod());
        sheduleRefresh();
    }

    void clickedCallSync(AThread t){
        t.enterSyncMethod();
        sheduleRefresh();
    }

    void clickedCallSyncAll(AThread t){
        t.enterSyncAllMethod();
        sheduleRefresh();
    }
 

    void initUIComponents(){
        //List<JMenuItem> itemsObjects = new ArrayList;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        frame.setSize(800, 500);   
        frame.setVisible(true);
        //textPane = new JEditorPane();    
        //textPane.setContentType("text/html");    
        //frame.setContentPane(textPane);    

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new GridLayout(1,models.size()));

        models.forEach((k,v)->{
                ATextPane p = new ATextPane();
                p.setContentType("text/html");
                p.setEditable(false);
                textPanes.put(k,p);            
                v.setTListener(p);
                TitledBorder title = BorderFactory.createTitledBorder("Threads on "+k);
                p.setBorder(title);
                mainPane.add(p);

            });

        frame.setContentPane(mainPane);

        models.values().stream().forEach( tc -> {
                final String name = tc.sharedObject.name;
                JMenu m = new JMenu(name);
                JMenuItem i = new JMenuItem("call notify");
                i.addActionListener(e -> {clickedNotify(tc);});
                m.add(i);
                menuObjects.add(m);

                JMenu otm = new JMenu("on "+name);

                tc.all.forEach( (n,t) -> {
                        JMenu mt = new JMenu(n);

                        JMenuItem it = new JMenuItem("wait (on "+name+")");
                        it.addActionListener(e ->{clickedCallWait(t); });
                        mt.add(it);

                        it = new JMenuItem("exit running method");
                        it.addActionListener(e ->{clickedExitRunningMethod(t); });
                        mt.add(it);

                        it = new JMenuItem("enter normal method");
                        it.addActionListener(e ->{clickedCallNormal(t); });
                        mt.add(it);

                        it = new JMenuItem("enter sync method");
                        it.addActionListener(e ->{clickedCallSync(t); });
                        mt.add(it);

                        it = new JMenuItem("enter sync ALL method");
                        it.addActionListener(e ->{clickedCallSyncAll(t); });
                        mt.add(it);

                        //it = new JMenuItem("call notify");
                        //it.addActionListener(e ->{clickedCallNotify(t); refreshTables();});
                        //mt.add(it);

                        otm.add(mt);
                        menuThreads.add(otm);

                        ;} 
                );

                JMenuItem itemAllNormal = new JMenuItem("enter normal in all threads");
                itemAllNormal.addActionListener((e)-> clickedCallNormal(tc));
                JMenuItem itemAllSync = new JMenuItem("enter sync in all threads");
                itemAllNormal.addActionListener((e)-> clickedCallSync(tc));
                JMenuItem itemAllAll = new JMenuItem("enter sync ALL in all threads");
                itemAllNormal.addActionListener((e)-> clickedCallSyncAll(tc));
                
                otm.add(itemAllNormal);
                otm.add(itemAllSync);
                otm.add(itemAllAll);

            });
        JMenu exit = new JMenu("Exit");
        JMenuItem exit2 = new JMenuItem("exit");
        exit2.addActionListener(e -> {for (TController con: models.values()) con.stopAll();});
        exit.add(exit2);
        JMenuItem itemRefresh = new JMenuItem("refresh states");
        itemRefresh.addActionListener((e) -> refreshTables());
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);

        itemRefresh.setAccelerator(keyStroke);
        menuThreads.add(itemRefresh);
        bar.add(menuObjects);
        bar.add(menuThreads);
        bar.add(exit);
        frame.setJMenuBar(bar);

    }

    class ATextPane extends JEditorPane implements TController.TListener{
        public synchronized void update(List<String[]> states, String objectName){

            StringBuilder builder = new StringBuilder(html1);
            states.forEach((p)->{
                    String line = String.format(html2,p[0],p[1],p[2],p[3]);
                    builder.append(line);
                });
            builder.append(html3);
            String toShow = builder.toString();
            //System.out.println(toShow);
            if (toShow.equals(getText())) return;
            setText(toShow);
            //repaint();
        }
    }

}
