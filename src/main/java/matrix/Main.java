package matrix;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("My Matrix Rain");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            MatrixRain matrixRain = new MatrixRain();
            frame.add(matrixRain);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            matrixRain.requestFocusInWindow();
        });
    }
}