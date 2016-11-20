import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnitTests {

    
    @Test
//  *  antisymmetric, where p, q, and r have coordinates in [0, 500)
    public void testInverseSlope500() {
        Point p = new Point(57, 134);
        Point q = new Point(101, 449);
        assertTrue(p.slopeTo(q) == q.slopeTo(p));
    }

    @Test
//  *  antisymmetric, where p, q, and r have coordinates in [0, 500)
    public void testAntiSymmetric500() {
        Point p = new Point(57, 134);
        Point q = new Point(101, 449);
        Point r = new Point(448, 472);
        assertTrue(p.slopeOrder().compare(q, r) == p.slopeOrder().compare(q, r));
    }

    @Test
//    *  sign of compare(), where p, q, and r have coordinates in [0, 500)
    public void testSignOfCompare() {
        Point p = new Point(233, 191);
        Point q = new Point(431, 224);
        Point r = new Point(293, 201);
        assertTrue(p.slopeTo(q) == p.slopeTo(r));
    }
}
