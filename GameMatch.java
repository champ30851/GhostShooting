package gamematch;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameMatch {

    public static void main(String[] args) {
        new Manu();
    }

}

class Manu extends JFrame {

    Background p_Manu;
    Manu main;

    public Manu() {
        main = this;
        setTitle("Ghost Shooting"); //ชื่อเกม
        setSize(1024, 720);
        setResizable(false);
        setLocationRelativeTo(null);
//        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        p_Manu = new Background(this);
        add(p_Manu);
        PlaySound bgsound = new PlaySound("music.wav", 16700, -18); //เสียงเกม
        bgsound.start();

        //last line
        setVisible(true);
    }
}

class Background extends JPanel {

    Image bg = Toolkit.getDefaultToolkit().createImage( //ตั้งค่า background
            System.getProperty("user.dir") + File.separator
            + "bgwel.png"
    );

    Image startbutton = Toolkit.getDefaultToolkit().createImage( //ตั้งค่ารูปปุ่ม start
            System.getProperty("user.dir") + File.separator
            + "start.png"
    );
    Background This;
    Manu main;
    JButton b_start = new JButton(new ImageIcon(startbutton));

    public Background(Manu main) {
        setSize(1024, 720);
        setLayout(null);
        This = this;
        this.main = main;
        b_start.setBounds(432, 418, 164, 67); //เซ็ตให้ปุ่มstart อยู่ตรงกลาง
        b_start.setOpaque(false);
        b_start.setContentAreaFilled(false);
        b_start.setBorderPainted(false);
        add(b_start);
        b_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                main.remove(This);
                main.add(new GameZone(main));
                main.repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bg, 0, 0, 1024, 720, 0, 0, 1302, 772, this);
    }

}

class GameZone extends JPanel {

    int stopWatch = 30;
    int hp = 4; //หัวใจ
    int oneHeart = 50;
    boolean hited = false;
    public static int score = 0;
    int mouseX = 0;
    int mouseY = 0;
    GameZone This;
    Manu main;
    Timer timer;
    Image manubutton = Toolkit.getDefaultToolkit().createImage(
            System.getProperty("user.dir") + File.separator
            + "Menubutton.png"
    );

    public GameZone(Manu main) {
        setSize(1024, 720);
        setLayout(null);
        this.main = main;
        This = this;
        timer = new Timer();
        score = 0;
        addMouseListener(new ShootGhost(this));
        JButton b_manu = new JButton(new ImageIcon(manubutton));
        add(b_manu);
        b_manu.setOpaque(false);
        b_manu.setContentAreaFilled(false);
        b_manu.setBorderPainted(false);
        b_manu.setBounds(11, 610, 108, 46);
        b_manu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.cancel();
                main.remove(This);
                main.add(new Background(main));
                main.repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
        timer.scheduleAtFixedRate(new MoveGhost(this), 0, 10); // ความเร็วของผี
        timer.scheduleAtFixedRate(new StopWatch(this, main), 0, 1000);//จำนวนวินาทีที่ลดลงให้เร็ซหรือช้า
        for (int i = 0; i < numberOfGhost; i++) {
            x[i] = new Random().nextInt(900);
            y[i] = new Random().nextInt(500);

            xGoLeft[i] = new Random().nextBoolean();
            yGoUp[i] = new Random().nextBoolean();
        }
        
         for (int i = 0; i < numberOfreaper; i++) {
           
            a[i] = new Random().nextInt(900);
            b[i] = new Random().nextInt(500);

            aGoLeft[i] = new Random().nextBoolean();
            bGoUp[i] = new Random().nextBoolean();
        }

    }

    Image bg = Toolkit.getDefaultToolkit().createImage( //รูปbackgroundหน้าเกมยิง
            System.getProperty("user.dir") + File.separator
            + "bg2.jpg"
    );
    Image ghost = Toolkit.getDefaultToolkit().createImage(
            System.getProperty("user.dir") + File.separator
            + "ghost.png"
    );
    Image crosshair = Toolkit.getDefaultToolkit().createImage(//รูปเป้า
            System.getProperty("user.dir") + File.separator
            + "crosshair.png"
    );
    Image heart = Toolkit.getDefaultToolkit().createImage( //รูปหัวใจ
            System.getProperty("user.dir") + File.separator
            + "heart.png"
    );
    Image reaper = Toolkit.getDefaultToolkit().createImage( ///
            System.getProperty("user.dir") + File.separator ///
            + "reaper2.png" ///
    );
    int numberOfGhost = 5; //จำนวนผีที่เกิด
    int[] x = new int[numberOfGhost];
    int[] y = new int[numberOfGhost];
    boolean[] xGoLeft = new boolean[numberOfGhost];
    boolean[] yGoUp = new boolean[numberOfGhost];
    int hitbox = (int) Math.sqrt((Math.pow(Math.abs(134 - 0), 2))
            + (Math.pow(Math.abs(119 - 0), 2))); //ค่าhitbox ที่ยิงโดนผี

    int numberOfreaper = 2;
    int[] a = new int[numberOfreaper];
    int[] b = new int[numberOfreaper];
    boolean[] aGoLeft = new boolean[numberOfreaper];
    boolean[] bGoUp = new boolean[numberOfreaper];
    int hitbox2 = (int) Math.sqrt((Math.pow(Math.abs(150 - 0), 2))
            + (Math.pow(Math.abs(119 - 0), 2))); //ค่าhitbox ที่ยิงโดนผี*/

    @Override
    protected void paintComponent(Graphics g) {
        //Img section
        g.drawImage(bg, 0, 0, 1024, 720, 0, 0, 1920, 1126, this); // เซ็ตค่าให้ผีสุ่มเกิดแต่ละครั้งและวนลูป
        for (int i = 0; i < numberOfGhost; i++) {
            g.drawImage(ghost, x[i], y[i], x[i] + 134, y[i]
                    + 119, 0, 0, 1726, 1527, this);

        }
        for (int i = 0; i < numberOfreaper; i++) {
            g.drawImage(reaper, a[i] + 50, b[i] + 50, a[i] + 50 + 134, b[i] + 50
                    + 119, 0, 0, 1726, 1527, this);

        }

        //time section
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        g.setColor(Color.white);
        g.drawString("Time", 474, 46);
        g.setFont(new Font("Tahoma", Font.BOLD, 50));
        g.setColor(Color.white);
        g.drawString(stopWatch + "", 467, 100);
        //Score section
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        g.setColor(Color.white);
        g.drawString("Score:", 804, 46);
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        g.setColor(Color.RED);
        g.drawString(score + "", 877, 46);
        //Crosshair
        g.drawImage(crosshair, mouseX - 50, mouseY - 50, this);
        //heart
        g.drawImage(heart, 68, 41, 68 + (oneHeart * hp), 41 + 41, 0, 0, oneHeart * hp, 41, this);
    }
}

class MoveGhost extends TimerTask {

    GameZone gameZone;

    public MoveGhost(GameZone gameZone) {
        this.gameZone = gameZone;
    }

    @Override
    public void run() {  //ตั้งค่าให้ผีลอยไปลอยมาในหน้าจอ
        for (int i = 0; i < gameZone.numberOfGhost; i++) {
            if (gameZone.xGoLeft[i]) {
                gameZone.x[i]--;
                if (gameZone.x[i] <= 69) {
                    gameZone.xGoLeft[i] = false;
                }
            } else {
                gameZone.x[i]++;
                if (gameZone.x[i] > 800) {
                    gameZone.xGoLeft[i] = true;
                }
            }
            if (gameZone.yGoUp[i]) {
                gameZone.y[i]--;
                if (gameZone.y[i] <= 30) {
                    gameZone.yGoUp[i] = false;
                }
            } else {
                gameZone.y[i]++;
                if (gameZone.y[i] > 100) {
                    gameZone.yGoUp[i] = true;
                }
            }
//            
//            if (gameZone.aGoLeft[i]) {
//                gameZone.a[i]--;
//                if (gameZone.a[i] <= 69) {
//                    gameZone.aGoLeft[i] = false;
//                }
//            } else {
//                gameZone.a[i]++;
//                if (gameZone.a[i] > 800) {
//                    gameZone.aGoLeft[i] = true;
//                }
//            }
//            if (gameZone.bGoUp[i]) {
//                gameZone.b[i]--;
//                if (gameZone.b[i] <= 30) {
//                    gameZone.bGoUp[i] = false;
//                }
//            } else {
//                gameZone.b[i]++;
//                if (gameZone.b[i] > 100) {
//                    gameZone.bGoUp[i] = true;
//                }
//            }
             gameZone.repaint();
        }
        for (int i = 0; i < gameZone.numberOfreaper; i++) {
            if (gameZone.aGoLeft[i]) {
                gameZone.a[i]--;
                if (gameZone.a[i] <= 69) {
                    gameZone.aGoLeft[i] = false;
                }
            } else {
                gameZone.a[i]++;
                if (gameZone.a[i] > 800) {
                    gameZone.aGoLeft[i] = true;
                }
            }
            if (gameZone.bGoUp[i]) {
                gameZone.b[i]--;
                if (gameZone.b[i] <= 30) {
                    gameZone.bGoUp[i] = false;
                }
            } else {
                gameZone.b[i]++;
                if (gameZone.b[i] > 100) {
                    gameZone.bGoUp[i] = true;
                }
            }
//            
//            if (gameZone.aGoLeft[i]) {
//                gameZone.a[i]--;
//                if (gameZone.a[i] <= 69) {
//                    gameZone.aGoLeft[i] = false;
//                }
//            } else {
//                gameZone.a[i]++;
//                if (gameZone.a[i] > 800) {
//                    gameZone.aGoLeft[i] = true;
//                }
//            }
//            if (gameZone.bGoUp[i]) {
//                gameZone.b[i]--;
//                if (gameZone.b[i] <= 30) {
//                    gameZone.bGoUp[i] = false;
//                }
//            } else {
//                gameZone.b[i]++;
//                if (gameZone.b[i] > 100) {
//                    gameZone.bGoUp[i] = true;
//                }
//            }
             gameZone.repaint();
        }
       
    }

}




class ShootGhost extends MouseAdapter //ตั้งค่าการยิงผีในเกม
{

    GameZone a;
    int shooted = 0;

    public ShootGhost(GameZone gameZone) {
        this.a = gameZone;
    }

    int distance;

    @Override
    public void mousePressed(MouseEvent e) {

        PlaySound gunsound = new PlaySound("soundgun.wav", 1000, -10); //เสียงยืงปืนเมื่อกดยิงให้ทำเป็นลูป
        gunsound.start();
        for (int i = 0; i < a.numberOfGhost; i++) {
            distance = (int) Math.sqrt((Math.pow(Math.abs(a.x[i] - e.getX()), 2))
                    + (Math.pow(Math.abs(a.y[i] - e.getY()), 2)));
            if (distance <= a.hitbox && e.getX() >= a.x[i] && e.getY() >= a.y[i]) {
                a.score += 100; //คะแนนเพิ่มเมื่อทำการยิงโดนผี
                a.hited = true;
                a.x[i] = new Random().nextInt(900);
                a.y[i] = new Random().nextInt(500);
            }
            
        }
         for (int i = 0; i < a.numberOfreaper; i++) {
            distance = (int) Math.sqrt((Math.pow(Math.abs(a.a[i] - e.getX()), 2))
                    + (Math.pow(Math.abs(a.b[i] - e.getY()), 2)));
            if (distance <= a.hitbox && e.getX() >= a.a[i] && e.getY() >= a.b[i]) {
                a.score += 200; //คะแนนเพิ่มเมื่อทำการยิงโดนผี
                a.hited = true;
                a.a[i] = new Random().nextInt(900);
                a.b[i] = new Random().nextInt(500);
        
            }
         }
            
        
        if (!a.hited) {
            a.hp--;
        }
        a.hited = false;
        if (a.hp <= -1) {
            a.timer.cancel();
            a.main.remove(a);
            a.main.add(new GameOver(a.main));
            a.repaint();
            a.main.repaint();

            return;
        }

        a.repaint();

    }
}

class StopWatch extends TimerTask {

    GameZone a;
    Manu main;
    GameOver end;
    StopWatch This;

    public StopWatch(GameZone gameZone, Manu main) {
        this.a = gameZone;
//        a.stopWatch=10;
        this.main = main;
        This = this;

    }

    @Override
    public void run() { //ตั้งค่าปุ่ม
        if (a.stopWatch > 0) {
            a.stopWatch--;
        }
        if (a.stopWatch <= 0) {
            This.cancel();
            main.remove(a);
            end = new GameOver(main);
            main.add(end);
            main.repaint();
            return;
        }
        a.repaint();
    }
}

class GameOver extends JPanel {

    Image bg = Toolkit.getDefaultToolkit().createImage(
            System.getProperty("user.dir") + File.separator
            + "gameover.png"
    );
    Image restart = Toolkit.getDefaultToolkit().createImage(
            System.getProperty("user.dir") + File.separator
            + "restart.png"
    );
    Image exit = Toolkit.getDefaultToolkit().createImage(
            System.getProperty("user.dir") + File.separator
            + "exit.png"
    );
    Image manubutton = Toolkit.getDefaultToolkit().createImage(
            System.getProperty("user.dir") + File.separator
            + "Menubutton.png"
    );
    GameOver This;
    boolean in = false;

    public GameOver(Manu main) {
        PlaySound overSound = new PlaySound("gameover.wav", 3000, 0);
        overSound.start();
        setSize(1024, 720);
        setLayout(null);
        This = this;
        JButton b_restart = new JButton(new ImageIcon(restart));
        b_restart.setBounds(175, 369, 305, 152);
        add(b_restart);
        b_restart.setOpaque(false);
        b_restart.setContentAreaFilled(false);
        b_restart.setBorderPainted(false);
        JButton b_exit = new JButton(new ImageIcon(exit));
        add(b_exit);
        b_exit.setOpaque(false);
        b_exit.setBorderPainted(false);
        b_exit.setContentAreaFilled(false);
        b_exit.setSize(269, 116);
        b_exit.setLocation(549, 404);
        JButton b_manu = new JButton(new ImageIcon(manubutton));
        b_manu.setBounds(860, 596, 108, 46);
        add(b_manu);
        b_manu.setOpaque(false);
        b_manu.setContentAreaFilled(false);
        b_manu.setBorderPainted(false);
        b_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        b_restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.remove(This);
                GameZone.score = 0;
                main.add(new GameZone(main));
                main.repaint();
            }
        });
        b_manu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.remove(This);
//                System.out.println("click");
                main.add(new Background(main));
                main.repaint();
            }
        });
        repaint();

    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bg, 0, 0, 1024, 720, 0, 0, 1447, 967, this);
        //Score section
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        g.setColor(Color.white);
        g.drawString("Your Score:", 730, 46);
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        g.setColor(Color.RED);
        g.drawString(GameZone.score + "", 877, 46);

    }
}

class PlaySound extends Thread {

    String name; //ขื่อไฟล์เพลง
    int howlongMs; //ความยาวและความดังของเพลง
    int decreaseSound;

    public PlaySound(String name, int howlongMs, int decreaseSound) {
        this.name = name;
        this.howlongMs = howlongMs;
        this.decreaseSound = decreaseSound;
    }

    @Override
    public void run() {
        try {
            File sound = new File(System.getProperty("user.dir")
                    + File.separator + name);
            AudioInputStream stream = AudioSystem.getAudioInputStream(sound);
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            FloatControl gainControl
                    = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(decreaseSound);
            clip.start();
            Thread.sleep(howlongMs);

        } catch (Exception e) {
        }
    }

}
