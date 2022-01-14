package app.gui;

import app.ExecutionController;
import app.FuzzySimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Gui {
    static class MyPanel extends ResizablePanel {
        private final FuzzySimulator fuzzySimulator;
        public MyPanel(JFrame frame, FuzzySimulator fuzzySimulator) {
            super(frame);
            this.fuzzySimulator = fuzzySimulator;
        }


        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            Graphics2D g2d = (Graphics2D) g;
            double scaleRatio = (double) Math.min(getHeight(), getWidth()) / fuzzySimulator.getWidth();
            double radius = fuzzySimulator.getTrapRadius() * scaleRatio;
            Ellipse2D trap = new Ellipse2D.Double(
                    (fuzzySimulator.getTrapX()  - radius / 2)* scaleRatio,
                    (fuzzySimulator.getTrapY() - radius / 2) * scaleRatio,
                    radius,
                    radius);

            Ellipse2D car = new Ellipse2D.Double(
                    fuzzySimulator.getCurrentX() * scaleRatio - radius / 2,
                    fuzzySimulator.getCurrentY() * scaleRatio - radius / 2,
                    radius,
                    radius
            );

            g.setColor(Color.RED);
            g2d.fill(trap);

            g.setColor(Color.BLACK);
            g2d.fill(car);

        }

        @Override
        public void paintComponent(Graphics g) {}
    }

    private final int defaultWindowWidth = 800;
    private final int defaultWindowHeight = 800;
    private JFrame frame;
    private MyPanel panel;
    private Thread guiUpdateThread;
    private final ExecutionController executionController;
    private FuzzySimulator fuzzySimulator;

    public Gui(ExecutionController executionController) {
        initFrame();
        this.executionController = executionController;
        this.fuzzySimulator = new FuzzySimulator(executionController);
        initToolbar();
        initDisplayPanel();
        initControlBar();
        frame.setVisible(true);


        initGuiThread();
    }

    private void initFrame() {
        frame = new JFrame("Fuzzy example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(defaultWindowWidth, defaultWindowHeight);
    }
    private void initToolbar() {
//        JMenuBar mb = new JMenuBar();
//        JMenu m1 = new JMenu("FILE");
//        JMenu m2 = new JMenu("Help");
//        mb.add(m1);
//        mb.add(m2);
//        JMenuItem m11 = new JMenuItem("Open");
//        JMenuItem m22 = new JMenuItem("Save as");
//        m1.add(m11);
//        m1.add(m22);
//        frame.getContentPane().add(BorderLayout.NORTH, mb);
    }
    private void initDisplayPanel() {
        panel = new MyPanel(frame, fuzzySimulator);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
    }
    private void initControlBar() {
//        JPanel panel = new JPanel();
//        JLabel label = new JLabel("Enter Text");
//        JTextField tf = new JTextField(10);
//        JButton send = new JButton("Send");
//        send.addActionListener(e -> System.out.println("send action"));
//        JButton reset = new JButton("Reset");
//        panel.add(label);
//        panel.add(tf);
//        panel.add(send);
//        panel.add(reset);
//        frame.getContentPane().add(BorderLayout.SOUTH, panel);
    }
    private void initGuiThread() {
        guiUpdateThread = new Thread(() -> {
            System.out.println("start gui:");

            while (!executionController.checkIfEnd()) {
//                System.out.println("gui: loop");
                panel.repaint();
                executionController.sleepAnimation();
                executionController.waitIfEnabled();
            }
        });
    }
    public void start() {
        guiUpdateThread.start();
        new Thread(() -> fuzzySimulator.run()).start();
    }
}