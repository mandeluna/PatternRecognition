import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {

    private List<LineSegment> segments = new ArrayList<LineSegment>();
    private Point[] points;

    // finds all line segments containing 4 or more points
    public BruteCollinearPoints(Point[] pointArray) {
        if (pointArray == null) throw new NullPointerException();

        this.points = pointArray.clone();

        // Sort the points once - the CombinationIterator will return lexicographically ordered indexes
        // so p0,...,p3 will be pre-sorted when we check for collinearity
        Arrays.sort(points);

        // see if there is anything to do
        if (points.length == 1) {
            return;
        }

        // Check for duplicate points
        for (int i = 1; i < this.points.length; i++) {
            if (points[i-1].compareTo(points[i]) == 0) {
                throw new IllegalArgumentException("point " + (i-1) + " and point " + i + " are the same");
            }
        }

        // see if there is anything to do
        if (points.length < 4) {
            return;
        }

        // simple check, don't create combinations
        if (points.length == 4) {
            Point p0 = points[0];
            Point p1 = points[1];
            Point p2 = points[2];
            Point p3 = points[3];
            if ((p0 == null) || (p1 == null) || (p2 == null) || (p3 == null))
                throw new NullPointerException();
            bruteCollinearCheck(p0, p1, p2, p3);
            return;
        }

        CombinationIterator combIter = new CombinationIterator(points.length, 4);
        for (int[] index : combIter) {
            Point p0 = points[index[0]];
            Point p1 = points[index[1]];
            Point p2 = points[index[2]];
            Point p3 = points[index[3]];
            if ((p0 == null) || (p1 == null) || (p2 == null) || (p3 == null)) {
                throw new NullPointerException();
            }
            bruteCollinearCheck(p0, p1, p2, p3);
        }
    }

    private void collinearCheck(Point p0, Point p1, Point p2) {
        double slope1 = p0.slopeTo(p1);
        double slope2 = p0.slopeTo(p2);
        if (slope1 == slope2) {
            segments.add(new LineSegment(p0, p2));
        }
    }

    private void bruteCollinearCheck(Point p0, Point p1, Point p2, Point p3) {
        double slope1 = p0.slopeTo(p1);
        double slope2 = p0.slopeTo(p2);
        double slope3 = p0.slopeTo(p3);
        if ((slope1 == slope2) && (slope2 == slope3)) {
            segments.add(new LineSegment(p0, p3));
        }
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

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    private class CombinationIterator implements Iterable<int[]> {
    
        private int t, j, x;
        private int[] c;
        /*
         * Algorithm 7.2.1.3T TAOCP by Donald Knuth: Lexicographic Combinations
         * This algorithm is like Algorithm L, but faster. It assumes, for convenience, t < n
         * Return a list of digits from 1 to n taken t at a time, in lexicographic order"
         */
        public CombinationIterator(int n, int t) {
            this.t = t;
            c = new int[t+2];
    
            // T1. Initialize
            for (int i = 0; i < t; i++) {
                c[i] = i;
            }
            c[t] = n;
            c[t+1] = 0;
            j = t;
        }
    
        @Override
        public Iterator<int[]> iterator() {
            return new Iterator<int[]>() {
    
                private int[] visit() {
                    int[] result = new int[t];
                    for (int i = t-1; i >= 0; --i) {
                        result[i] = c[i];
                    }
                    return result;
                }
    
                private void findj() {
                    while (true) {
                        c[j-2] = j - 2;
                        x = c[j-1] + 1;
                        if (x != c[j]) {
                            return;
                        }
                        ++j;
                    }
                }
    
                @Override
                public boolean hasNext() {
                    return j <= t;
                }
    
                @Override
                public int[] next() {
                    int[] result = visit();
                    if (j > 0) {
                        x = j;
                        // T4. Increase c[j]
                        c[j-1] = x;
                        --j;
                    }
                    // T3. Easy case?
                    else if (c[0] + 1 < c[1]) {
                        c[0]++;
                    }
                    else {
                        j = 2;
                        findj();
                        // T5. Terminate the algorithm if j > t
                        if (j <= t) {
                            // T6. Increase c[j]
                            c[j-1] = x;
                            --j;
                        }
                    }
                    return result;
                }
            };
        }
    }
}
