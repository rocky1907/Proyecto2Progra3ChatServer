package chatServer;

import chatProtocol.User;
import chatProtocol.Protocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import chatProtocol.IService;
import java.net.Socket;
import javax.swing.JOptionPane;

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
    boolean mensajeMostrado;
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
    
    public void listenByUser(){
        continuar=true;
    }
    
    public User getUser(){
        return user;
    }
    
    public void listen(){
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                switch(method){
                case Protocol.LOGIN: //done on accept
                    try {
                        Service.instance().login(user);
                    } catch (Exception ex) {}
                    start();
                    break;  
                case Protocol.LOGOUT:
                    try {
                        user.conect=false;
                        Service.instance().logout(user);
                    } catch (Exception ex) {}
                    stop();
                    break;                 
                case Protocol.POST:
                    String message=null;
                    try {
                        message = (String)in.readObject();
                        String id = (String)in.readObject();  
                        System.out.println("Id "+id);
                        System.out.println("User Id "+user.getId());
                        Service.instance().post(user.getId()+"->"+message,id);
                        Service.instance().post(user.getId()+"-->"+message,user.getId());  

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
