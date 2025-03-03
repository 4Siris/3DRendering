package org.example;

import org.example.model.Point;
import org.example.model.Triangle;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class MainFrame{
    public void start(List<Triangle> triangles){
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        JSlider heightSlider = new JSlider(SwingConstants.VERTICAL,0,720,180);
        pane.add(heightSlider, BorderLayout.EAST);

        JSlider widthSlider = new JSlider(0,720,180);
        pane.add(widthSlider, BorderLayout.SOUTH);

        JSlider depthSlider = new JSlider(0,720,180);
        pane.add(depthSlider, BorderLayout.NORTH);

        JPanel renderPanel = new JPanel(){
            public void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                double heading = Math.toRadians(heightSlider.getValue());
                Matrix3V3 heightMatrix = new Matrix3V3(new double[] {
                        Math.cos(heading), 0, -Math.sin(heading),
                        0, 1, 0,
                        Math.sin(heading), 0, Math.cos(heading)
                });
                double pitch = Math.toRadians(widthSlider.getValue());
                Matrix3V3 widthMatrix = new Matrix3V3(new double[] {
                        1, 0, 0,
                        0, Math.cos(pitch), Math.sin(pitch),
                        0, -Math.sin(pitch), Math.cos(pitch)
                });
                double depth = Math.toRadians(depthSlider.getValue());
                Matrix3V3 depthMatrix = new Matrix3V3(new double[] {
                        Math.cos(depth), -Math.sin(depth), 0,
                        Math.sin(depth), Math.cos(depth), 0,
                        0, 0, 1
                });
                Matrix3V3 transform = heightMatrix.multiply(widthMatrix).multiply(depthMatrix);

                g2.translate(getWidth() / 2, getHeight() / 2);
                g2.setColor(Color.WHITE);
                BufferedImage img = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
                double[] zBuffer = new double[img.getWidth() * img.getHeight()];
                Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

                for (Triangle t : triangles) {
                    Point p1 = transform.transform(t.p1);
                    Point p2 = transform.transform(t.p2);
                    Point p3 = transform.transform(t.p3);
                    p1.x += (double) getWidth() / 2;
                    p1.y += (double) getHeight() / 2;
                    p2.x += (double) getWidth() / 2;
                    p2.y += (double) getHeight() / 2;
                    p3.x += (double) getWidth() / 2;
                    p3.y += (double) getHeight() / 2;

                    Point ab = new Point(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
                    Point ac = new Point(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);
                    Point norm = new Point(
                            ab.y * ac.z - ab.z * ac.y,
                            ab.z * ac.x - ab.x * ac.z,
                            ab.x * ac.y - ab.y * ac.x
                    );
                    double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;

                    double angleCos = Math.abs(norm.z);


                    int minX = (int) Math.max(0, Math.ceil(Math.min(p1.x, Math.min(p2.x, p3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(p1.x, Math.max(p2.x, p3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(p1.y, Math.min(p2.y, p3.y))));
                    int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(p1.y, Math.max(p2.y, p3.y))));

                    double triangleArea = (p1.y - p3.y) * (p2.x - p3.x) + (p2.y - p3.y) * (p3.x - p1.x);

                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                            double b1 = ((y - p3.y) * (p2.x - p3.x) + (p2.y - p3.y) * (p3.x - x)) / triangleArea;
                            double b2 = ((y - p1.y) * (p3.x - p1.x) + (p3.y - p1.y) * (p1.x - x)) / triangleArea;
                            double b3 = ((y - p2.y) * (p1.x - p2.x) + (p1.y - p2.y) * (p2.x - x)) / triangleArea;
                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                double depthShade = b1 * p1.z + b2 * p2.z + b3 * p3.z;
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depthShade) {
                                    img.setRGB(x, y, getShade(t.color,angleCos).getRGB());
                                    zBuffer[zIndex] = depthShade;
                                }
                            }
                        }
                    }
                }
                g2.drawImage(img, -600,-400,null);
            }
        };
        heightSlider.addChangeListener(e -> renderPanel.repaint());
        widthSlider.addChangeListener(e -> renderPanel.repaint());
        depthSlider.addChangeListener(e -> renderPanel.repaint());
        pane.add(renderPanel, BorderLayout.CENTER);

        frame.setSize(1000,1000);
        frame.setVisible(true);
    }

    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);

        return new Color(red, green, blue);
    }

}
