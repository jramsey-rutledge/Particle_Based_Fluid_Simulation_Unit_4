import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Particle {
    // TO DO: Add Instance Variables (State)
    private Color color;
    private int radius;
    private double x;
    private double y;
    private double vx;
    private double vy;

    // TO DO: Constructor
    public Particle(double x, double y, double vx, double vy, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.color = color;
    }

    // TO DO: Behavior - Update physics position based on velocity
    public void update(double gravity, int height, int width) {
        this.vy += gravity;
        this.x += this.vx;
        this.y += this.vy;

        double damping = 0.75; // 75% velocity retention 25% energy loss

        // left and right wall checks
        if (this.x -radius < 0) 
        {
            this.x = radius;
            this.vx *= -damping;
        }
        else if (this.x + radius > width)
        {
            this.x = width - radius;
            this.vx *= -damping;
        }

        // Top and Bottom Wall 
        if (this.y - radius < 0) {
            this.y = radius;
            this.vy *= -damping;
        }
        else if (this.y + radius > height)
        {
            this.y = height - radius;
            this.vy *= -damping;
        }
    }

    // Behavior: Render particle onto canvas
    public void draw(Graphics g) {
        g.setColor(this.color);
        // Cast double coordinates to int pixels for drawing, centering on (x, y)
        int drawX = (int) (this.x - this.radius);
        int drawY = (int) (this.y - this.radius);
        int diameter = this.radius * 2;
        
        g.fillOval(drawX, drawY, diameter, diameter);
        double speed = Math.sqrt(vx*vx + vy*vy);
        double maxSpeed = 12.0;
        double speedRatio = Math.min(speed / maxSpeed, 1.0);
        double hue = 0.66 - (speedRatio * 0.66);
        Color myColor = Color.getHSBColor((float) hue,(float) 0.85,(float) 1.0);
        g.setColor(myColor);
        g.fillOval((int) x-radius,(int) y-radius, radius*2,radius*2);
    }

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }

    // add that repulsion
    public void applyMouseRepulsion(int mouseX, int mouseY, double radius, double strength)
    {
        double dx = this.x - mouseX;
        double dy = this.y - mouseY;
        double distSq = dx * dx + dy * dy;

        if (distSq < radius * radius && distSq > 0 )
        {
            double dist = Math.sqrt(distSq);
            double force = (1.0 - dist / radius ) * strength;
            this.vx += (dx / dist) * force;
            this.vy += (dy / dist) * force;
        }
    }

    public void resolveCollision(Particle other)
    {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double distance = Math.sqrt(dx*dx + dy*dy);
        double minDistance = this.radius + other.radius;

        // check if particles intersect
        if (distance < minDistance && distance > 0)
        {
            double overlap = (minDistance - distance)/2;

            // collision line
            double nx = dx / distance;
            double ny = dy / distance;

            // separate particles
            this.x -= nx * overlap;
            this.y -= ny * overlap;
            other.x += nx * overlap;
            other.y += ny * overlap;

            // calculate relative velocity impulse exchange
            double kx = this.vx - other.vx;
            double ky = this.vy - other.vy;
            double p = nx * kx + ny * ky;

            // apply energy transfer
            this.vx -= p * nx * 0.8;
            this.vy -= p * ny * 0.8;
            other.vx += p * nx * 0.8;
            other.vy += p * ny * 0.8;
        }
    }

    public void applyFluidPressure(Particle other, double smoothingRadius, double pressureStrength)
    {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double distSq = dx*dx + dy*dy;

        if (distSq < smoothingRadius * smoothingRadius && distSq > 0)
        {
            double dist = Math.sqrt(distSq);
            double force = (1.0-dist / smoothingRadius) * pressureStrength;

            // collision line
            double nx = dx / dist;
            double ny = dy / dist;

            // push particles apart
            this.vx -= nx * force;
            this.vy -= ny * force;
            other.vx += nx * force;
            other.vy += ny * force;
        }


    }
}