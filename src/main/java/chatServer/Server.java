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
    
    public void deliver(String message){
        workers.stream().filter(wk -> ("111".equals(wk.getUser().getId()))).forEachOrdered(wk -> {
            wk.deliver(message);
            System.out.println("Holasssss");
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
    
}