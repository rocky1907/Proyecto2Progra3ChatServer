package chatServer;

import chatProtocol.Protocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import chatProtocol.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JOptionPane;

public class Server {
    ServerSocket srv;
    List<Worker> workers; 
    
    public Server() {
        try {
            srv = new ServerSocket(Protocol.PORT);
            workers =  Collections.synchronizedList(new ArrayList<Worker>());
        } 
        catch (IOException ex) {}
    }
    
    public void run(){
        Service localService = (Service)(Service.instance());
        localService.setSever(this);
        boolean continuar = true;
        while (continuar) {
            try {
                Socket skt = srv.accept();
                ObjectInputStream in = new ObjectInputStream(skt.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(skt.getOutputStream() );
                try {
                    int method = in.readInt(); // should be Protocol.LOGIN                    
                    User user=(User)in.readObject();                          
                    try {
                        user=Service.instance().login(user);
                        out.writeInt(Protocol.ERROR_NO_ERROR);
                        out.writeObject(user);
                        out.flush();
                        Worker worker = new Worker(skt,in,out,user);
                        workers.add(worker);  
                        worker.start();
                    } catch (Exception ex) {
                       out.writeInt(Protocol.ERROR_LOGIN);
                       out.flush();
                    }                          
                } 
                catch (ClassNotFoundException ex) {}                

            } 
            catch (IOException ex) {}
        }
    }
    
    public void deliver(String message,String id){
        workers.stream().filter(wk -> (id.equals(wk.getUser().getId()))).forEachOrdered(wk -> {
            wk.deliver(message);
        }); 
//        for(Worker wk:workers){
//          wk.deliver(message);
//        }   
    } 
    
    public void remove(User u){
        for(Worker wk:workers) {
            if(wk.user.equals(u)){
                workers.remove(wk);
                try { wk.skt.close();} catch (IOException ex) {}
                break;
            }
        }
    }
    
    public Worker findByUser(String us){
        for (Worker worker : workers) {
            if(worker.getUser().getId().equals(us)){
                return worker;
            }
        }
        return null;
    }
    public void mostrarLogout(String s){
        for (int i = 0; i < workers.size(); i++) {
            JOptionPane.showMessageDialog(null, "El usuario "+s+" se ha desconectado");
        }
    }
    public void mostrarLogin(String s){
        for (int i = 0; i < workers.size(); i++) {
            if(workers.get(i).getUser().conect==true){
                JOptionPane.showMessageDialog(null, "El usuario "+s+" se ha conectado");
            }   
        }
    }
    
}