package chatServer;

import java.io.File;
import java.util.Scanner;

public class Application {
    
    public static void main(String[] args) throws Exception {
        
        Server server = new Server();
        server.run();
    }
}
