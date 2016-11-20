import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {

    private List<LineSegment> segments = new ArrayList<>();
    private Point[] points;
    private List<OriginSlope> visited = new ArrayList<>();

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] pointArray) {

        if (pointArray == null) {
            throw new NullPointerException();
        }

        // see if there is anything to do
        if (pointArray.length < 3) {
            return;
        }

        this.points = pointArray.clone();

        Arrays.sort(points);
        // Check for duplicate points
        for (int i = 1; i < this.points.length; i++) {
            if (points[i-1].compareTo(points[i]) == 0) {
                throw new IllegalArgumentException("point " + (i-1) + " and point " + i + " are the same");
            }
        }

        for (int i = 0; i < points.length-1; i++) {
            Point anchor = points[i];
            Arrays.sort(points, i+1, points.length, anchor.slopeOrder());
/*
            List<String> strs = new ArrayList<>();
            for (int k = i+1; k < points.length; k++) {
                strs.add(String.format("%5.3f", anchor.slopeTo(points[k])));
            }
            System.out.println(String.format("[%d] %s: %s", i, anchor, String.join(", ", strs)));
*/
            int start = i + 1;
            for (int end = start + 1; end <= points.length; end++) {
                double slope = anchor.slopeTo(points[start]);
                if (end == points.length || (slope != anchor.slopeTo(points[end]))) {
                    if (end - start > 2) {
                        // resort the points of interest by natural order to ensure the correct endpoints are produced
                        Arrays.sort(points, start, end);
                        Point origin = anchor.compareTo(points[start]) < 0 ? anchor : points[start];
                        Point endPoint = anchor.compareTo(points[end - 1]) > 0 ? anchor : points[end - 1];

                        if (!visitedSegment(origin, slope)) {
                            visitSegment(origin, slope);
                            LineSegment segment = new LineSegment(origin, endPoint);
                            segments.add(segment);
                        }
                    }
                    start = end;
                }
            }
        }
    }

    // add all point pairs between start and end to the visited collection
    private void visitSegment(Point origin, double slope) {
        OriginSlope pair = new OriginSlope(origin, slope);
        visited.add(pair);
    }

    private boolean visitedSegment(Point p1, double slope) {
        OriginSlope segment = new OriginSlope(p1, slope);
        return visited.contains(segment);
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.size();
    }

    // the line segments
    public LineSegment[] segments() {
        return segments.toArray(new LineSegment[0]);
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        long now = System.currentTimeMillis();
        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
        StdOut.println(String.format("Running time: %d milliseconds", System.currentTimeMillis() - now));
    }

    private class OriginSlope {
        private Point origin;
        private double slope;

        public OriginSlope(Point origin, double slope) {
            this.origin = origin;
            this.slope = slope;
        }

        @Override
        // hashing is not allowed in this assignment
        public int hashCode() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (this == object) {
                return true;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }

            OriginSlope other = (OriginSlope) object;
            // if the line segments represented by these two objects are collinear
            // they must have the same slope, and either the origin points are the
            // same, or the slopes represented by a line joining those points matches
            return (other.slope == this.slope) &&
                    ((other.origin == this.origin) || (this.origin.slopeTo(other.origin) == this.slope));
        }
    }
}
