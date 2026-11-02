import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

class SimulationPanel extends JPanel implements ActionListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private static final int FPS_DELAY = 16;
    private Timer timer;

    private ArrayList<Particle> particles;
    private static final int MAX_PARTICLES = 300;
    private static final double GRAVITY = 0.25;

    // Mouse Input Variables
    private int mouseX = -1000;
    private int mouseY = -1000;
    private boolean isMousePressed = false;

    // FPS Diagnostic Counter
    private int frameCount = 0;
    private int currentFPS = 0;
    private long lastFPSCheck = System.currentTimeMillis();

    public SimulationPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(new Color(15, 18, 26));
        this.setDoubleBuffered(true);

        particles = new ArrayList<>();

        // Initial fluid block spawn (150 particles)
        for (int i = 0; i < 150; i++) {
            double startX = 250 + (i % 15) * 20 + Math.random() * 2;
            double startY = 100 + (i / 15) * 20 + Math.random() * 2;
            particles.add(new Particle(startX, startY, 0, 0, 8));
        }

        // Attach Mouse Motion & Listener Adapters
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isMousePressed = true;
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isMousePressed = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        this.timer = new Timer(FPS_DELAY, this);
    }

    public void startSimulation() {
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateSimulation();
        repaint();
    }

    private void updateSimulation() {
        calculateFPS();

        // 1. Mouse Interaction: Spawn new particles when dragging mouse
        if (isMousePressed && particles.size() < MAX_PARTICLES) {
            double vx = (Math.random() - 0.5) * 4;
            double vy = (Math.random() - 0.5) * 4;
            particles.add(new Particle(mouseX, mouseY, vx, vy, 8));
        }

        // 2. Physics & Kinematics Update
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            p.applyMouseRepulsion(mouseX, mouseY, 100, 2.5);
            p.update(WIDTH, HEIGHT, GRAVITY);
        }

        // 3. Collision Resolution (Pairwise interaction check)
        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                particles.get(i).resolveCollision(particles.get(j));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Fluid Particles
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).draw(g2d);
        }

        // Render Mouse Repulsion Ring
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.drawOval(mouseX - 100, mouseY - 100, 200, 200);

        // Render Diagnostic Information
        g2d.setColor(Color.WHITE);
        g2d.drawString("Coding 1 Fluid Simulation - Final Demo Preview", 20, 30);
        g2d.drawString("Particles: " + particles.size() + " / " + MAX_PARTICLES, 20, 50);
        g2d.drawString("Click & Drag to push fluid or spawn particles!", 20, 70);
        g2d.drawString("FPS: " + currentFPS, WIDTH - 80, 30);
    }

    private void calculateFPS() {
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastFPSCheck >= 1000) {
            currentFPS = frameCount;
            frameCount = 0;
            lastFPSCheck = now;
        }
    }
}