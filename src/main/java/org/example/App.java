package org.example;

import org.example.model.Point;
import org.example.model.Triangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App 
{
    public static void main( String[] args ) throws IOException {
        String[] points = FileManager.fileReader("Points.txt").split("v");
        List<Point> pointsList = new ArrayList<>();
        for (int i=1;i<points.length;i++){
            pointsList.add(new Point(
                    Double.parseDouble(points[i].split(" ")[1])*3000,
                    Double.parseDouble(points[i].split(" ")[2])*3000,
                    Double.parseDouble(points[i].split(" ")[3])*3000));
        }
        String[] triangles = FileManager.fileReader("Triangle.txt").split("f");
        List<Triangle> trianglesList = new ArrayList<>();
        for(int i=1;i<triangles.length;i++){
            trianglesList.add(new Triangle(
                    pointsList.get(Integer.parseInt(triangles[i].split(" ")[1].split("/")[0])-1),
                    pointsList.get(Integer.parseInt(triangles[i].split(" ")[2].split("/")[0])-1),
                    pointsList.get(Integer.parseInt(triangles[i].split(" ")[3].split("/")[0])-1),
                    new Color((int) (Math.random()*255), (int) (Math.random()*255), (int) (Math.random()*255))
            ));
        }
        MainFrame mainFrame = new MainFrame();
        mainFrame.start(trianglesList);


    }
}
