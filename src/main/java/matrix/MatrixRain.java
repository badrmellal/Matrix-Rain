package matrix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class MatrixRain extends JPanel implements ActionListener, KeyListener, MouseMotionListener {
    private ArrayList<DigitalStream> streams;
    private Timer timer;
    private Random random;
    private int mouseX, mouseY;
    private boolean rainbowMode = false;
    private boolean pulseMode = false;
    private boolean gravityMode = false;
    private float hue = 0;
    private int speed = 2;

    // My example is Japanese-like characters but you guys can change it
    private static final String CHARS = "ｦｱｳｴｵｶｷｹｺｻｼｽｾｿﾀﾂﾃﾅﾆﾇﾈﾊﾋﾎﾏﾐﾑﾒﾓﾔﾕﾗﾘﾜ123456780";

    public MatrixRain() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        streams = new ArrayList<>();
        random = new Random();

        // Initializing streams
        for (int i = 0; i < 50; i++) {
            streams.add(new DigitalStream(random.nextInt(800), -random.nextInt(600)));
        }

        addKeyListener(this);
        addMouseMotionListener(this);

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Creating fade effect
        g2d.setColor(new Color(0, 0, 0, 25));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Drawing all streams
        for (DigitalStream stream : streams) {
            stream.draw(g2d);
        }

        // Drawing controls info
        drawControls(g2d);
    }

    private void drawControls(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.WHITE);
        int y = 20;
        g2d.drawString("R - Rainbow Mode: " + (rainbowMode ? "ON" : "OFF"), 10, y += 20);
        g2d.drawString("P - Pulse Mode: " + (pulseMode ? "ON" : "OFF"), 10, y += 20);
        g2d.drawString("G - Gravity Mode: " + (gravityMode ? "ON" : "OFF"), 10, y += 20);
        g2d.drawString("UP/DOWN - Speed: " + speed, 10, y += 20);
        g2d.drawString("Move mouse to influence streams", 10, y += 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        hue += 0.005f;
        if (hue > 1) hue = 0;

        // Updating all streams
        for (DigitalStream stream : streams) {
            stream.update();
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R:
                rainbowMode = !rainbowMode;
                break;
            case KeyEvent.VK_P:
                pulseMode = !pulseMode;
                break;
            case KeyEvent.VK_G:
                gravityMode = !gravityMode;
                break;
            case KeyEvent.VK_UP:
                speed = Math.min(10, speed + 1);
                break;
            case KeyEvent.VK_DOWN:
                speed = Math.max(1, speed - 1);
                break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private class DigitalStream {
        private float x, y;
        private ArrayList<Symbol> symbols;
        private float speed;
        private float targetX;
        private Color streamColor;

        public DigitalStream(int x, int y) {
            this.x = x;
            this.y = y;
            symbols = new ArrayList<>();
            speed = 1 + random.nextFloat() * 2;
            targetX = x;
            streamColor = new Color(0, 255, 0);

            // Initializing symbols
            int length = 10 + random.nextInt(20);
            for (int i = 0; i < length; i++) {
                symbols.add(new Symbol(i));
            }
        }

        public void update() {
            // Updating position
            y += speed * MatrixRain.this.speed;

            // Mouse interaction
            if (gravityMode) {
                float dx = mouseX - x;
                float dy = mouseY - y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance < 200) {
                    targetX += dx * 0.02f;
                }
            }

            // For smooth movement
            x += (targetX - x) * 0.1f;

            // Here to reset if off-screen
            if (y > getHeight() + 50) {
                y = -random.nextInt(600);
                x = random.nextInt(getWidth());
                targetX = x;
            }

            // Updating symbols
            for (Symbol symbol : symbols) {
                symbol.update();
            }
        }

        public void draw(Graphics2D g2d) {
            for (int i = 0; i < symbols.size(); i++) {
                Symbol symbol = symbols.get(i);
                float alpha = 1.0f - (float)i / symbols.size();

                if (rainbowMode) {
                    streamColor = Color.getHSBColor((hue + y * 0.001f) % 1.0f, 0.8f, 1.0f);
                }

                if (pulseMode) {
                    alpha *= 0.5f + 0.5f * Math.sin(hue * 10 + y * 0.05f);
                }

                g2d.setColor(new Color(
                        streamColor.getRed() / 255f,
                        streamColor.getGreen() / 255f,
                        streamColor.getBlue() / 255f,
                        alpha
                ));

                symbol.draw(g2d, (int)x, (int)(y - i * 20));
            }
        }
    }

    private class Symbol {
        private char character;
        private int changeCounter;

        public Symbol(int offset) {
            character = CHARS.charAt(random.nextInt(CHARS.length()));
            changeCounter = offset * 5;
        }

        public void update() {
            if (--changeCounter < 0) {
                character = CHARS.charAt(random.nextInt(CHARS.length()));
                changeCounter = random.nextInt(20) + 5;
            }
        }

        public void draw(Graphics2D g2d, int x, int y) {
            g2d.setFont(new Font("MS Gothic", Font.PLAIN, 20));
            g2d.drawString(String.valueOf(character), x, y);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}
}