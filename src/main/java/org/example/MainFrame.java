package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class MainFrame {
    public static void start(){
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        JSlider heightSlider = new JSlider(SwingConstants.VERTICAL,-90,90,0);
        pane.add(heightSlider, BorderLayout.EAST);

        JSlider widthSlider = new JSlider(0,360,180);
        pane.add(widthSlider, BorderLayout.SOUTH);

        List<Triangle> tris = new ArrayList<>();
        tris.add(new Triangle(new Point(100, 100, 100),
                new Point(-100, -100, 100),
                new Point(-100, 100, -100),
                Color.WHITE));
        tris.add(new Triangle(new Point(100, 100, 100),
                new Point(-100, -100, 100),
                new Point(100, -100, -100),
                Color.RED));
        tris.add(new Triangle(new Point(-100, 100, -100),
                new Point(100, -100, -100),
                new Point(100, 100, 100),
                Color.GREEN));
        tris.add(new Triangle(new Point(-100, 100, -100),
                new Point(100, -100, -100),
                new Point(-100, -100, 100),
                Color.BLUE));

        JPanel renderPanel = new JPanel(){
            public void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0,0,getWidth(),getHeight());
                double heading = Math.toRadians(heightSlider.getValue());
                Matrix3V3 heightMatrix = new Matrix3V3(new double[] {
                        Math.cos(heading), 0, Math.sin(heading),
                        0, 1, 0,
                        -Math.sin(heading), 0, Math.cos(heading)
                });
                double pitch = Math.toRadians(widthSlider.getValue());
                Matrix3V3 widthMatrix = new Matrix3V3(new double[] {
                        1, 0, 0,
                        0, Math.cos(pitch), Math.sin(pitch),
                        0, -Math.sin(pitch), Math.cos(pitch)
                });
                Matrix3V3 transform = heightMatrix.multiply(widthMatrix);

                g2.translate(getWidth() / 2, getHeight() / 2);
                g2.setColor(Color.WHITE);
                for (Triangle t : tris) {
                    Point p1 = transform.transform(t.p1);
                    Point p2 = transform.transform(t.p2);
                    Point p3 = transform.transform(t.p3);
                    Path2D path = new Path2D.Double();
                    path.moveTo(p1.x, p1.y);
                    path.lineTo(p2.x, p2.y);
                    path.lineTo(p3.x, p3.y);
                    path.closePath();
                    g2.draw(path);
                }
            }
        };
        heightSlider.addChangeListener(e -> renderPanel.repaint());
        widthSlider.addChangeListener(e -> renderPanel.repaint());
        pane.add(renderPanel, BorderLayout.CENTER);


        frame.setSize(400,400);
        frame.setVisible(true);
    }
}
