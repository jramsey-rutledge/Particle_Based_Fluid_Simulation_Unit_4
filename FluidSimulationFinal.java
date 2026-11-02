import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * CS1 Fluid Simulation - Complete Final Code
 * Features:
 * - Particle kinematics (position, velocity, gravity, damping)
 * - Wall boundary collisions
 * - Elastic particle-particle collisions & fluid spatial repulsion
 * - Mouse interaction (click & drag to push fluid / spawn particles)
 * - Speed-based color gradient rendering
 */
public class FluidSimulationFinal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CS1 Fluid Simulation - Final Demo");
            SimulationPanel panel = new SimulationPanel();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.startSimulation();
        });
    }
}

