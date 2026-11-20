import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;
import java.awt.event.*;

public class SimulationPanel extends JPanel implements ActionListener {
    // Screen Dimensions
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    // Game Loop Settings (1000ms / 60 FPS ~= 16 ms per frame)
    private static final int FPS_DELAY = 16;
    private Timer timer;

    // Diagnostic Counters
    private int frameCount = 0;
    private int currentFPS = 0;
    private long lastFPSCheck = System.currentTimeMillis();

    // Declare particles
    private ArrayList<Particle> particles;

    // state variables for mouse
    private int mouseX = -1000;
    private int mouseY = -1000;
    private boolean isMousePressed = false;

    // Environmental Constant for Gravity
    private static final double GRAVITY = 0.25;
    private boolean isGravityOn = true;

    public SimulationPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(new Color(20, 20, 25)); // Dark theme background
        this.setDoubleBuffered(true);             // Prevents screen flickering

        // Initialize timer (calls actionPerformed every 16ms)
        this.timer = new Timer(FPS_DELAY, this);

        particles = new ArrayList<Particle>();

        for (int index = 0; index < 50; index++)
        {
            double randX = Math.random()*WIDTH;
            double randY = Math.random()*HEIGHT;
            double randVX = Math.random()*4 - 2.0;
            double randVY = Math.random()*4 -2.0;

            Color randBlue = new Color(50, 150, (int)(150+Math.random()*105));
            particles.add(new Particle(randX,randY,randVX,randVY,10,randBlue));
        }

        //System.out.println(particles.size());

        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                isMousePressed = true;
                mouseX = e.getX();
                mouseY = e.getY();
            }

            public void mouseReleased(MouseEvent e)
            {
                isMousePressed = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() 
        {
            public void mouseMoved(MouseEvent e){
                mouseX = e.getX();
                mouseY = e.getY();
            }

            public void mouseDragged(MouseEvent e){
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        setFocusable(true);
        addKeyListener( new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_G)
                    isGravityOn = !isGravityOn;
                else if (e.getKeyCode() == KeyEvent.VK_C) 
                    particles.clear(); 
            }
        }
            
        );

    }



    public void startSimulation() {
        timer.start();
    }

    // Called automatically every time timer fires (16ms)
    @Override
    public void actionPerformed(ActionEvent e) {
        updateSimulation();
        repaint(); // Requests Swing to re-render the canvas
    }

    // UPDATE STEP: Math and physics logic go here in future lessons
    private void updateSimulation() 
    {
        double currentGravity = isGravityOn ? 0.25 : 0.0;
        calculateFPS();
        for (int i=0; i < particles.size(); i++){
            particles.get(i).update(currentGravity, HEIGHT, WIDTH);
        }

        // particle collision
        for (int i=0; i < particles.size(); i++)
        {
            for (int j=i+1; j < particles.size(); j++)
            {
                particles.get(i).applyFluidPressure(particles.get(j),35.0,0.15);
                particles.get(i).resolveCollision(particles.get(j));
            }
        }

        // // garbage collector
        // for (int f = particles.size()-1; f >= 0;f--)
        // {
        //     double x = particles.get(f).getX();
        //     double y = particles.get(f).getY();
        //     if (x > WIDTH || y > HEIGHT || x < 0 || y < 0)
        //         particles.remove(f);
        // }

        // particle spawner
        if (isMousePressed)
            particles.add(new Particle(mouseX,mouseY,Math.random()*4 - 2.0,Math.random()*4 - 2.0,10,Color.CYAN));

        for (int i=0; i < particles.size(); i++)
        {
            particles.get(i).applyMouseRepulsion(mouseX, mouseY, 100, 2.5);
            particles.get(i).update(currentGravity,HEIGHT,WIDTH);
        }
    }

    // DRAW STEP: Custom graphics rendering
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clears canvas from previous frame

        g.setColor(Color.CYAN);
        g.drawString("Fluid Engine initialized...",20,30);
        g.drawString("FPS: "+ currentFPS,WIDTH-80,30);
        g.drawString("Particles: "+calculateParticles(),20,45);
        g.setColor(Color.RED);
        g.drawString("[G] Gravity: " + (isGravityOn ? "ON" : "OFF"),20,60);
        g.drawString("[C] Clear Screen | [Click] Spawn Fluid", 20, 75);
        // show particles
        for (int i=0;i<particles.size();i++)
        {
            particles.get(i).draw(g);
        }

    }

    // Helper method to measure performance
    private void calculateFPS() {
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastFPSCheck >= 1000) {
            currentFPS = frameCount;
            frameCount = 0;
            lastFPSCheck = now;
        }
    }

    // Helper method to calculate number of particles
    private int calculateParticles()
    {
        return particles.size();
    }
}