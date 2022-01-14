package app.gui;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class ResizablePanel extends JPanel {

    private boolean drag = false;
    private Point dragLocation  = new Point();
    private final JFrame frame;

    public ResizablePanel(JFrame frame) {
        this.frame = frame;
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setPreferredSize(frame.getSize());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag = true;
                dragLocation = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drag = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drag) {
                    if (dragLocation.getX()> getWidth()-10 && dragLocation.getY()>getHeight()-10) {
                        System.err.println("in");
                        setSize((int)(getWidth()+(e.getPoint().getX()-dragLocation.getX())),
                                (int)(getHeight()+(e.getPoint().getY()-dragLocation.getY())));
                        dragLocation = e.getPoint();
                    }
                }
            }
        });
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);
    }
}