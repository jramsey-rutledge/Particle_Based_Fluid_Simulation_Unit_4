import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class FluidSimulation { 
    public static void main(String[] args) {
        // Run GUI initialization on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fluid Simulation");
            SimulationPanel panel = new SimulationPanel();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(panel); // Attach custom canvas to window frame
            frame.pack();     // Size window to fit panel's preferred size
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);

            panel.startSimulation(); // Fire up the 60 FPS timer
        });
    }
}