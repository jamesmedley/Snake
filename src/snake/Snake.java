package snake;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.Timer;

public class Snake extends Canvas {
    static int score=0, highscore;
    static boolean Applefound = true;
    static Timer t;
    static double gameloop = 0;
    static int[] head_position = new int[]{19, 14};
    static ArrayList<int[]> body = new ArrayList<int[]>();
    static int[] Applelocation = new int[2];
    static int heading = 0;
    static boolean gameover=false, pause=false,showgameover=false,paint=false;
   
    
    private static void gameTime(Canvas canvas) {
        t = new Timer(80, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pause|gameover){
                    gameloop += 1;
                    if(gameloop%5==0){
                        paint=!paint;
                        canvas.repaint();
                    }
                    return;
                }
                gameloop += 1;
                move();
                
                String[] copy = new String[body.size()-1];
                for(int i=0;i<copy.length;i++){
                    copy[i]="["+body.get(i)[0]+", "+body.get(i)[1]+"]";
                }
                if(((head_position[0]>39)|(head_position[1]>29)|(head_position[0]<0)|(head_position[1]<0))|(check(Arrays.toString(head_position),copy))){
                    gameover=true;
                    HighScores hs = new HighScores();
                    try {
                        hs.addtoDatabase(score);
                    } catch (SQLException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        highscore = hs.fetchHighscore();
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                canvas.repaint();
            }
        });
        t.start();
    }

    private static int[] getlocation(int[] position) {
        int x = position[0];
        int y = position[1];
        x = x * 20;
        y = y * 20;
        int[] location = new int[]{x, y};
        return location;
    }

    @Override
    public void paint(Graphics g) {
        if(pause){
            drawPause(g);
        }
        for(int i=0;i<body.size();i++){
            drawSnake(g,i);
        }
        drawApple(g);
        drawScore(g);
        if((gameover)){
            drawRestart(g);
            drawHighScore(g);
            if(paint){
              drawGameOver(g);  
            }
        }
        

    }

    private void drawSnake(Graphics g,int index) {
        int[] location = getlocation(body.get(index));
        int x = location[0];
        int y = location[1];
        g.setColor(Color.red);
        g.fillRect(x, y, 20, 20); 
    }

    private void drawApple(Graphics g) {
        boolean availiableSpace=true;
        String[] copy = new String[body.size()-1];
        for(int i=0;i<copy.length;i++){
            copy[i]="["+body.get(i)[0]+", "+body.get(i)[1]+"]";
        }
        if (Applefound == true) {
            while(availiableSpace){
                Random rand = new Random();
                int xco = rand.nextInt(40);
                int yco = rand.nextInt(30);
                int[] Appleposition = new int[]{xco, yco};
                String ApplepositionString = Arrays.toString(Appleposition);
                availiableSpace = check(ApplepositionString,copy);
                Applelocation = getlocation(Appleposition);
                Applefound = false; 
            }
            
        }
        g.setColor(Color.green);
        g.fillRect(Applelocation[0], Applelocation[1], 20, 20);
    }
    
    private void drawScore(Graphics g){
        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("Score: "+score, 5, 20);
    }
    
    private void drawPause(Graphics g){
        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("PAUSED", 715, 20);
    }
    
    private void drawGameOver(Graphics g){
        g.setColor(Color.red);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 120));
        g.drawString("GAME OVER", 30, 300);
    }
    
    private void drawRestart(Graphics g){
        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("Press 'r' to play again", 600, 20);
    }
    
    private void drawHighScore(Graphics g){
        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("Highscore: "+highscore, 5, 40);
    }
    
    private static void move(){
        switch (heading) {
            case 0:
                head_position = new int[]{head_position[0],head_position[1]-1}; 
                break;
            case 90:
                head_position = new int[]{head_position[0]+1,head_position[1]}; 
                break;
            case 180:
                head_position = new int[]{head_position[0],head_position[1]+1}; 
                break;
            case 270:
                head_position = new int[]{head_position[0]-1,head_position[1]}; 
                break;
        }
        if(Arrays.toString(getlocation(head_position)).equals(Arrays.toString(Applelocation))) {
            score++;
            Applefound = true;
            body.add(0, head_position);
            
        }else{
            body.remove(body.size()-1);
            body.add(0, head_position);
        }
      
    }
    
    private static boolean check(String head, String[] body){
        for(int i=1;i<body.length;i++){
            if(body[i].equals(head)){
               return true;
            }
            
        }
        return false;
    }
    
    private static void restart() throws SQLException{
        head_position = new int[]{19, 14};
        gameloop = 0;
        score=0;
        Applefound = true;
        body = new ArrayList<int[]>();
        Applelocation = new int[2];
        heading = 0;
        gameover=false; pause=false;showgameover=false;paint=false;
        
        
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                HighScores server = new HighScores();
                try {
                    server.closeServer();
                } catch (Exception ex) {
                    Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, "Shutdown-thread"));
        HighScores server = new HighScores();
        server.start();
        JFrame mainframe = new JFrame("Snake");
        mainframe.setSize(815, 639);
        mainframe.setResizable(false);
        mainframe.setVisible(true);
        mainframe.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Canvas canvas = new Snake();
        mainframe.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
                
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }
            @Override
            public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    switch (keyCode) {
                        case KeyEvent.VK_R:
                            if(gameover){
                                try {
                                    restart();
                                } catch (SQLException ex) {
                                    Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                body.add(0, head_position);
                                t.stop();
                                gameTime(canvas);                    
                            }
                            break;
                        case KeyEvent.VK_P:
                            if(!gameover){
                                pause = !pause;
                            }
                            break;
                        case KeyEvent.VK_UP:
                            if(((heading==180)&(body.size()>1))|pause){
                                break;
                            }
                            heading = 0;
                            break;
                        case KeyEvent.VK_DOWN:
                            if(((heading==0)&(body.size()>1))|pause){
                                break;
                            }
                            heading = 180;
                            break;
                        case KeyEvent.VK_LEFT:
                            if(((heading==90)&(body.size()>1))|pause){
                                break;
                            }
                            heading = 270;
                            break;
                        case KeyEvent.VK_RIGHT:
                            if(((heading==270)&(body.size()>1))|pause){
                                break;
                            }
                            heading = 90;
                            break;
                    }
                    
            }

        });
        
        canvas.setSize(800, 600);
        canvas.setBackground(Color.black);
        mainframe.add(canvas);
        body.add(0, head_position);
        gameTime(canvas);
    }

}
