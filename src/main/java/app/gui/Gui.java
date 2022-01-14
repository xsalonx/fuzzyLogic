package app.gui;

import app.ExecutionController;
import app.FuzzySimulator;
import org.jfree.ui.tabbedui.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

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
            g.setColor(Color.GRAY);
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

    private final int defaultWindowWidth = 600;
    private final int defaultWindowHeight = 600;
    private JFrame frame;
    private MyPanel panel;
    private Thread guiUpdateThread;
    private final ExecutionController executionController;
    private FuzzySimulator fuzzySimulator;

    public Gui(ExecutionController executionController, FuzzySimulator fuzzySimulator) {
        initFrame();
        this.executionController = executionController;
        this.fuzzySimulator = fuzzySimulator;
        initMenuBar();
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
    private void initMenuBar() {
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
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Velocity:");
        JTextField velocityTextField = new JTextField(10);
        velocityTextField.setEditable(false);
        velocityTextField.setText(String.valueOf(fuzzySimulator.getVelocity()));
        JButton increment = new JButton("increment");
        increment.addActionListener(e -> velocityTextField.setText(String.valueOf(fuzzySimulator.incrementVelocity())));
        JButton decrement = new JButton("decrement");
        decrement.addActionListener(e -> velocityTextField.setText(String.valueOf(fuzzySimulator.decrementVelocity())));


        panel.setLayout(new GridLayout(2, 3));
        panel.add(label);
        panel.add(velocityTextField);
        panel.add(increment);
        panel.add(decrement);


        JLabel stepLabel = new JLabel("Step:");
        JTextField stepTextField = new JTextField(10);
        stepTextField.setEditable(false);
        stepTextField.setText(String.valueOf(fuzzySimulator.getStepRate()));
        JButton stepIncrement = new JButton("increment");
        stepIncrement.addActionListener(e -> stepTextField.setText(String.valueOf(fuzzySimulator.incrementStep())));
        JButton stepDecrement = new JButton("decrement");
        decrement.addActionListener(e -> stepTextField.setText(String.valueOf(fuzzySimulator.decrementStep())));

        panel.add(stepLabel);
        panel.add(stepTextField);
        panel.add(stepIncrement);
        panel.add(stepDecrement);

        frame.getContentPane().add(BorderLayout.SOUTH, panel);
    }
    private void initGuiThread() {
        guiUpdateThread = new Thread(() -> {
            System.out.println("start gui:");
            while (!executionController.checkIfEnd()) {
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