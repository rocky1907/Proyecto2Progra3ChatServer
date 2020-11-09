package chatServer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import chatProtocol.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserDao {

//    public void create(User o) throws Exception{
//        String sql="insert into Cliente (id, nombre) "+
//                "values(?,?)";
//        PreparedStatement stm = Database.instance().prepareStatement(sql);
//        stm.setString(1, o.getId());
//        stm.setString(2, o.getClave());
//        stm.setString(3, o.getNombre());        
//        int count=Database.instance().executeUpdate(stm);
//        if (count==0){
//            throw new Exception("Cliente ya existe");
//        }
//    }
    
    public User read(String id) throws Exception{
        String sql="select * from usuario where id=?";
        PreparedStatement stm = Database.instance().prepareStatement(sql);
        stm.setString(1, id);
        ResultSet rs =  Database.instance().executeQuery(stm);           
        if (rs.next()) {
            return from(rs);
        }
        else{
            throw new Exception ("usuario no Existe");
        }
    }
    
//    public void update(User o) throws Exception{
//        String sql="update Cliente set nombre=? "+
//                "where id=?";
//        PreparedStatement stm = Database.instance().prepareStatement(sql);
//        stm.setString(1, o.getId());
//        stm.setString(2, o.getClave());
//        stm.setString(3, o.getNombre());               
//        int count=Database.instance().executeUpdate(stm);
//        if (count==0){
//            throw new Exception("Cliente no existe");
//        }
//    }    

//    public void delete(User o) throws Exception{
//        String sql="delete from Cliente where id=?";
//        PreparedStatement stm = Database.instance().prepareStatement(sql);
//        stm.setString(1, o.getId());        
//        int count=Database.instance().executeUpdate(stm);        
//        if (count==0){
//            throw new Exception("Cliente no existe");
//        }
//    }
    
    public List<User> findAll(){
        List<User> r= new ArrayList<>();
        String sql="select * from usuario";
        try {        
            PreparedStatement stm = Database.instance().prepareStatement(sql);
            ResultSet rs =  Database.instance().executeQuery(stm);     
            while (rs.next()) {
                r.add(from(rs));
            }
        } catch (SQLException ex) { }
        return r;
    }

    public List<User> findByNombre(User o){
        List<User> r= new ArrayList<>();
        String sql="select * from usuario where nombre like ?";
        try {        
            PreparedStatement stm = Database.instance().prepareStatement(sql);
            stm.setString(1, "%"+o.getNombre()+"%");   
            ResultSet rs =  Database.instance().executeQuery(stm); 
            while (rs.next()) {
                r.add(from(rs));
            }
        } catch (SQLException ex) { }
        return r;
    }
    
    public User from (ResultSet rs){
        try {
            User r= new User();
            r.setId(rs.getString("id"));
            r.setClave(rs.getString("clave"));
            r.setNombre(rs.getString("nombre"));
            return r;
        } catch (SQLException ex) {
            return null;
        }
    }

    public  void close(){
    }
}
