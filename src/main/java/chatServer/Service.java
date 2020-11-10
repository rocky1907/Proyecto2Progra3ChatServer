package chatServer;

import chatProtocol.User;
import chatProtocol.IService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service implements IService{
    private static IService theInstance;
    public static IService instance(){
        if (theInstance==null){ 
            theInstance=new Service();
        }
        return theInstance;
    }
    
    Server srv;
    Map<String,User> users;
    UserDao dao;
    
    public Service() {        
        users =  new HashMap();
        dao = new UserDao();
        List<User> list = dao.findAll();
        for(int i=0;i<list.size();i++){
            users.put(list.get(i).getNombre(),new User(list.get(i).getId(),list.get(i).getClave(),list.get(i).getNombre()));
        }
//        users.put("jperez", new User("jperez","111","Juan"));
//        users.put("mreyes", new User("mreyes","222","Maria"));
//        users.put("parias", new User("parias","333","Pedro"));                
    }
    
    public void setSever(Server srv){
        this.srv=srv;
    }
    
    public void post(String m){
        srv.deliver(m);
        // TODO if the receiver is not active, store it temporarily
    }
    
    public User login(User u) throws Exception{
        User result=users.get(u.getId());
        if(result==null)  throw new Exception("User does not exist");
        if(!result.getClave().equals(u.getClave()))throw new Exception("User does not exist");
        result.conect=true;
        return result;
    } 

    public void logout(User p) throws Exception{
        srv.remove(p);
    }    

//    public boolean serch(String data) {
//        return dao.findByID(data).size()!=0;
//    }
}
