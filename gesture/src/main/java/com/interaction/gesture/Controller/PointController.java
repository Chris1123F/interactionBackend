package com.interaction.gesture.Controller;

import com.alibaba.fastjson.JSON;
import com.interaction.gesture.Entry.Point;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController("/api/point")
public class PointController {
    private static Map<String, List<Point>> pointMap = new HashMap<>();
    public static final double EPS = Math.pow(2,-52);

    @RequestMapping("/addPoint")
    public String addPoint(@RequestParam String projectName,@RequestParam ArrayList<Point> p) {
        pointMap.put(projectName, p);
        return JSON.toJSONString("result");
    }

    @RequestMapping("/match")
    public String match(@RequestParam List<Point> p) {
        List<Point> newP = resample(p , 20);
        for (Map.Entry<String,List<Point>> entry : pointMap.entrySet()) {
            List<Point> newlist = resample(entry.getValue(),20);
            double result = pathDistance(newlist, newP);
            if (result < 1) {
                return "true";
            }
        }
        return JSON.toJSONString("null");
    }
    //重采样，n是点个数
    public ArrayList<Point> resample(List<Point> p,int n) {
        if (p.size() <= 1) {
            return null;
        }

        double I = pathLength(p) / (n - 1);
        System.out.println(I);
        double D = 0;
        ArrayList<Point> q = new ArrayList<>();
        Point newPoint = new Point(p.get(0).x, p.get(0).y);
        q.add(newPoint);
        for (int i = 1; i < p.size(); i++) {
            double d = Math.sqrt(Math.pow(p.get(i).x - p.get(i - 1).x, 2) + Math.pow(p.get(i).y - p.get(i - 1).y, 2));
            if (D + d >= I - EPS) {
                double newX = (I - D) / d * (p.get(i).x - p.get(i - 1).x) + p.get(i - 1).x;
                double newY = (I - D) / d * (p.get(i).y - p.get(i - 1).y) + p.get(i - 1).y;
                Point newP = new Point(newX, newY);
                q.add(newP);
                replacePoint(p, i - 1, newX, newY);
                D = 0;
                i = i - 1;
            } else {
                D += d;
            }
        }
        return q;
    }

    //将图形压缩到size大小的正方形中
    @RequestMapping("/scaleTo")
    public ArrayList<Point> scaleTo(@RequestParam ArrayList<Point> p, @RequestParam double size) {
        ArrayList<Point> q = new ArrayList<>();

        String borders = getborder(p);
        String[] border = borders.split(",");
        double width = Double.parseDouble(border[1]) - Double.parseDouble(border[0]);
        double heigth = Double.parseDouble(border[3]) - Double.parseDouble(border[2]);

        for (int i = 0; i < p.size(); i++) {
            Point newP = new Point(p.get(i).x * size / width, p.get(i).y * size / heigth);
            q.add(newP);
        }

        return q;
    }

    //旋转
    @RequestMapping("/distanecAtBestAngle")
    public double distanecAtBestAngle(@RequestParam ArrayList<Point> p, @RequestParam ArrayList<Point> T, @RequestParam double Oa, @RequestParam double Ob, @RequestParam double delta) {
        double fi = (Math.sqrt(5) - 1) / 2;
        double x1 = fi * Oa + (1 - fi) * Ob;
        double f1 = distanceAtAngle(p, T, x1);
        double x2 = (1 - fi) * Oa + fi * Ob;
        double f2 = distanceAtAngle(p, T, x2);
        while (Math.abs(Ob - Oa) > delta) {
            if (f1 < f2) {
                Ob = x2;
                x2 = x1;
                f2 = f1;
                x1 = fi * Oa + (1 - fi) * Ob;
                f1 = distanceAtAngle(p, T, x1);
            } else {
                Oa = x1;
                x1 = x2;
                f1 = f2;
                x2 = (1 - fi) * Oa + fi * Ob;
                f2 = distanceAtAngle(p, T, x2);
            }
        }
        return (f1 < f2) ? f1 : f2;
    }


    //最终和阈值比较的结果
    public double pathDistance(List<Point> a, List<Point> b) {
        double d = 0;
        for (int i = 0; i < a.size(); i++)
            d += Math.sqrt(Math.pow(b.get(i).x - a.get(i).x, 2) + Math.pow(b.get(i).y - a.get(i).y, 2));
        return d / a.size();
    }

    //此方法没有完成，不太理解
    private double distanceAtAngle(ArrayList<Point> p, ArrayList<Point> T, double x) {

        return 0;
    }

    private double pathLength(List<Point> p) {
        double length = 0.0;

        for (int i = 1; i < p.size(); i++) {
            length = length + Math.sqrt(Math.pow(p.get(i).x - p.get(i - 1).x, 2) + Math.pow(p.get(i).y - p.get(i - 1).y, 2));
        }

        return length;
    }

    private String getborder(ArrayList<Point> p) {
        String result = "";
        double min_x = Integer.MAX_VALUE;
        double max_x = Integer.MIN_VALUE;
        double min_y = Integer.MAX_VALUE;
        double max_y = Integer.MIN_VALUE;

        for (int i = 0; i < p.size(); i++) {
            if (p.get(i).x > max_x) {
                max_x = p.get(i).x;
            }
            if (p.get(i).x < min_x) {
                min_x = p.get(i).x;
            }
            if (p.get(i).y > max_y) {
                max_y = p.get(i).y;
            }
            if (p.get(i).y < min_y) {
                min_y = p.get(i).y;
            }
        }

        result = result + min_x + "," + max_x + "," + min_y + "," + max_y;

        return result;
    }

    private void replacePoint(List<Point> p, int n, double x, double y) {
        Point point = p.get(n);
        point.x = x;
        point.y = y;
    }
}