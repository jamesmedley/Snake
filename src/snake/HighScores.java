package snake;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.drda.NetworkServerControl;

public class HighScores implements Runnable{
    Thread t;
    static String dbLocation = "jdbc:derby://localhost/C://Users//james//AppData//Roaming//NetBeans//Derby//HighScores";
    static String dbUsername = "snakeApp";
    static String dbPassword = "dbpassword";
    private void startServer() throws Exception{
        NetworkServerControl server = new NetworkServerControl(InetAddress.getByName("localhost"),1527);
        server.start(new PrintWriter(System.out,true));
    }
    
    public void closeServer() throws Exception{
        NetworkServerControl server = new NetworkServerControl(InetAddress.getByName("localhost"),1527);
        server.shutdown();
    }   
    public int fetchHighscore() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        ArrayList<Integer> scoresList = new ArrayList<>();
        Connection connection = null;
        Statement statement=null;
        ResultSet results= null;
        String query="SELECT * FROM SNAKEAPP.HIGHSCORES";
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection(dbLocation, dbUsername, dbPassword);
            statement = connection.createStatement();
            results=statement.executeQuery(query);
            while(results.next()){
                int score = results.getInt("SCORES");
                scoresList.add(scoresList.size(), score);
            }
            Collections.sort(scoresList);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return scoresList.get(scoresList.size()-1);
    }
    
    public void addtoDatabase(int score) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        Connection connection = null;
        String query="INSERT INTO SNAKEAPP.HIGHSCORES (ID, SCORES)"+"VALUES ("+Integer.toString(getRecordCount()+1)+","+score+")";
        
        try{           
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection(dbLocation, dbUsername, dbPassword);
            PreparedStatement ps = connection.prepareStatement(query);
            int a = ps.executeUpdate();
            
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    private static int getRecordCount() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        Connection connection = null;
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        connection = DriverManager.getConnection(dbLocation, dbUsername, dbPassword);
        Statement stmt = connection.createStatement();        
        String query = "select count(*) from SNAKEAPP.HIGHSCORES";
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        int count = rs.getInt(1);
        return count;
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (Exception ex) {
            Logger.getLogger(HighScores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void start (){
        if (t == null) {
           t = new Thread (this,"server start");
           t.start ();
        }
    }
    
}
