import java.awt.Color;
import java.awt.Graphics2D;

class Particle {
    private double x, y;
    private double vx, vy;
    private int radius;

    public Particle(double x, double y, double vx, double vy, int radius) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
    }

    public void update(int width, int height, double gravity) {
        // Apply gravity acceleration
        this.vy += gravity;

        // Apply velocity vector to position coordinates
        this.x += this.vx;
        this.y += this.vy;

        // Wall boundary collision checks with energy loss (0.75 damping)
        double damping = 0.75;
        if (this.x - radius < 0) {
            this.x = radius;
            this.vx *= -damping;
        } else if (this.x + radius > width) {
            this.x = width - radius;
            this.vx *= -damping;
        }

        if (this.y - radius < 0) {
            this.y = radius;
            this.vy *= -damping;
        } else if (this.y + radius > height) {
            this.y = height - radius;
            this.vy *= -damping;
        }
    }

    // Handles short-range fluid repulsion & collision response between two particles
    public void resolveCollision(Particle other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double minDistance = this.radius + other.radius;

        // Check if particles overlap
        if (distance < minDistance && distance > 0) {
            double overlap = 0.5 * (minDistance - distance);

            // Calculate surface normal vector
            double nx = dx / distance;
            double ny = dy / distance;

            // Push particles apart along collision normal
            this.x -= nx * overlap;
            this.y -= ny * overlap;
            other.x += nx * overlap;
            other.y += ny * overlap;

            // Impulse velocity exchange
            double kx = this.vx - other.vx;
            double ky = this.vy - other.vy;
            double p = (nx * kx + ny * ky);

            this.vx -= p * nx * 0.8;
            this.vy -= p * ny * 0.8;
            other.vx += p * nx * 0.8;
            other.vy += p * ny * 0.8;
        }
    }

    // Interactive Force Vector: Push particle away from mouse cursor radius
    public void applyMouseRepulsion(int mouseX, int mouseY, double radiusForce, double strength) {
        double dx = this.x - mouseX;
        double dy = this.y - mouseY;
        double distSq = dx * dx + dy * dy;

        if (distSq < radiusForce * radiusForce && distSq > 0) {
            double dist = Math.sqrt(distSq);
            double force = (1.0 - dist / radiusForce) * strength;
            this.vx += (dx / dist) * force;
            this.vy += (dy / dist) * force;
        }
    }

    public void draw(Graphics2D g2d) {
        // Dynamically shift color brightness based on kinetic speed
        double speed = Math.sqrt(vx * vx + vy * vy);
        int red = Math.min(255, (int) (40 + speed * 30));
        int green = Math.min(255, (int) (140 + speed * 20));
        int blue = 255;
        g2d.setColor(new Color(red, green, blue));

        int drawX = (int) (this.x - this.radius);
        int drawY = (int) (this.y - this.radius);
        int diameter = this.radius * 2;
        g2d.fillOval(drawX, drawY, diameter, diameter);
    }
}

