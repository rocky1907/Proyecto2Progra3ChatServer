package chatServer;

import chatProtocol.User;
import chatProtocol.Protocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import chatProtocol.IService;
import java.net.Socket;

public class Worker {
    Socket skt;
    ObjectInputStream in;
    ObjectOutputStream out;
    User user;

    public Worker(Socket skt, ObjectInputStream in, ObjectOutputStream out, User user) {
        this.skt=skt;
        this.in=in;
        this.out=out;
        this.user=user;
    }

    boolean continuar;    
    public void start(){
        try {
            System.out.println("Worker atendiendo peticiones...");
            Thread t = new Thread(new Runnable(){
                public void run(){
                    listen();
                }
            });
            continuar = true;
            t.start();
        } catch (Exception ex) {  
        }
    }
    
    public void stop(){
        continuar=false;
    }
    
    public void listen(){
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                switch(method){
                //case Protocol.LOGIN: done on accept
                case Protocol.LOGOUT:
                    try {
                        Service.instance().logout(user);
                    } catch (Exception ex) {}
                    stop();
                    break;                 
                case Protocol.POST:
                    String message=null;
                    try {
                        message = (String)in.readObject();
                        Service.instance().post(user.getId()+": "+message);
                    } catch (ClassNotFoundException ex) {}
                    break;                     
                }
                out.flush();
            } catch (IOException  ex) {
                continuar = false;
            }                        
        }
    }
    
    public void deliver(String message){
        try {
            out.writeInt(Protocol.DELIVER);
            out.writeObject(message);
            out.flush();
        } 
        catch (IOException ex) {}
    }
}
