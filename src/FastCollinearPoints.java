import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class FastCollinearPoints {

    private Point[] points;
    private List<LineSegment> segments = new ArrayList<>();
    private List<PointSlopes> visited = new ArrayList<>();

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] pointArray) {

        if (pointArray == null) {
            throw new NullPointerException();
        }

        // see if there is anything to do
        if (pointArray.length < 2) {
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
                        // re-sort the points of interest by natural order to ensure the correct endpoints are produced
                        Arrays.sort(points, start, end);
                        int from = anchor.compareTo(points[start]) < 0 ? i : start;
                        Point origin = points[from];
                        Point endPoint = anchor.compareTo(points[end - 1]) > 0 ? anchor : points[end - 1];

                        // have we already processed this segment in the previous iteration
                        if (!isVisited(anchor, slope)) {
                            visitSegment(anchor, slope);
                            visitSegment(points, start, end - 1, slope);
                            LineSegment segment = new LineSegment(origin, endPoint);
                            segments.add(segment);
                        }
                    }
                    start = end;
                }
            }
        }
    }

    private static <T extends Comparable<T>> int search(List<T> list, T item) {
        if (list == null) {
            throw new NullPointerException();
        }
        int lo = 0;
        int hi = list.size() - 1;
        while (hi >= lo) {
            int mid = (lo + hi) / 2;
            T lookup = list.get(mid);
            int compare = item.compareTo(lookup);
            if (compare < 0) { 
                hi = mid - 1;
            }
            else if (compare > 0) {
                lo = mid + 1;
            }
            else {
                return mid;
            }
        }
        return -1;
    }

    private static <T extends Comparable<T>> void insert(T item, List<T> list) {
        if ((list == null) || (item == null)) {
            throw new NullPointerException();
        }
        int lo = 0;
        int hi = list.size() - 1;
        int mid = (lo + hi) / 2;
        int compare = 0;

        while (hi >= lo) {
            mid = (lo + hi) / 2;
            T lookup = list.get(mid);
            compare = item.compareTo(lookup);
            if (compare < 0) { 
                hi = mid - 1;
            }
            else if (compare > 0) {
                lo = mid + 1;
            }
            else {
                break;
            }
        }
        if (compare > 0) {
            list.add(mid + 1, item);
        }
        else {
            list.add(mid, item);
        }
    }

    private static <T extends Comparable<T>> boolean isSorted(List<T> items) {
        if (items == null) {
            throw new NullPointerException();
        }
        int len = items.size();
        if ((len == 0) || (len == 1)) {
            return true;
        }
        T prev = items.get(0);
        for (int i = 1; i < len; i++) {
            T next = items.get(i);
            if (prev.compareTo(next) > 0) {
                return false;
            }
            prev = next;
        }
        return true;
    }

    // add the point and slope to the visited collection
    private void visitSegment(Point point, double slope) {
        PointSlopes pair = new PointSlopes(point);
        List<Double> slopes;
        int lookup = search(visited, pair);
        // if there is no line with this slope, add an entry
        if (lookup < 0) {
            insert(pair, visited);
            assert isSorted(visited);
            slopes = pair.slopes;
        }
        else {
            PointSlopes existing = visited.get(lookup);
            slopes = existing.slopes;
        }
        lookup = search(slopes, slope);
        if (lookup < 0) {
            insert(slope, slopes);
            assert isSorted(slopes);
        }
    }

    // add all point pairs between start and end to the visited collection
    private void visitSegment(Point[] points, int from, int to, double slope) {
        for (int i = from; i <= to; i++) {
            visitSegment(points[i], slope);
        }
    }

    private boolean isVisited(Point anchor, double slope) {
        PointSlopes candidate = new PointSlopes(anchor);
        int lookup = search(visited, candidate);
        if (lookup < 0) {
            return false;
        }
        PointSlopes existing = visited.get(lookup);
        lookup = search(existing.slopes, slope);
        return lookup >= 0;
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
//        if (args.length == 0) {
//            runTimingTests();
//            return;
//        }
//        // read the n points from a file
//        In in = new In(args[0]);
//        int n = in.readInt();
//        Point[] points = new Point[n];
//        for (int i = 0; i < n; i++) {
//            int x = in.readInt();
//            int y = in.readInt();
//            points[i] = new Point(x, y);
//        }

    	int w = 17;
    	int h = 17;
    	int n = w * h;
    	int spacing = 4096;
        Point[] points = new Point[n];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                points[i + j * w] = new Point(i * spacing, j * spacing);
            }
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 65536);
        StdDraw.setYscale(0, 65536);
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

    private static void runTimingTests() {
        runTest1();
        for (int r = 1; r <= 13; r++) {
            runGridTest(r);
        }
    }

    private static void runTest1() {
        StdOut.println("Test 1a-1g: Find collinear points among N random distinct points");
        StdOut.println();
        StdOut.println("    N    time");
        StdOut.println("-------------");
        for (int l = 6; l < 13; l++) {
            int n = (int) Math.pow(2L, l);
            Point[] points = new Point[n];
            for (int i = 0; i < n; i++) {
                int x = StdRandom.uniform(32767);
                int y = StdRandom.uniform(32767);
                points[i] = new Point(x, y);
            }
            long now = System.currentTimeMillis();
            new FastCollinearPoints(points);
            double time = (double) (System.currentTimeMillis() - now) / 1000;
            StdOut.println(String.format("%6d  %5.2f", n, time));
        }
    }

    private static void runGridTest(int testNum) {
        int h = (int) Math.pow(2L, testNum - 1);
        String name = String.format("Test %da-%dg: Find collinear points among the N points on an N/%d-by-%d grid",
                testNum, testNum, h, h);
        StdOut.println();
        StdOut.println(name);
        StdOut.println();
        StdOut.println("    N    time");
        StdOut.println("-------------");
        for (int l = 6; l < 13; l++) {
            int n = (int) Math.pow(2L, l);
            if (n < h) {
                continue;
            }
            Point[] points = new Point[n];
            int w = n / h;
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    points[i + j * w] = new Point(i, j);
                }
            }
            long now = System.currentTimeMillis();
            new FastCollinearPoints(points);
            double time = (double) (System.currentTimeMillis() - now) / 1000;
            StdOut.println(String.format("%6d  %5.2f", n, time));
        }
    }

    private class PointSlopes implements Comparable<PointSlopes> {
        private Point origin;
        private List<Double> slopes;

        public PointSlopes(Point origin) {
            this.origin = origin;
            this.slopes = new ArrayList<>();
        }

        @Override
        public int compareTo(PointSlopes that) {
            return this.origin.compareTo(that.origin);
        }

        @Override
        public String toString() {
            return origin + " -> " + Arrays.toString(slopes.toArray());
        }
    }
}
