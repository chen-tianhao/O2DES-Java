package O2DES_Java;

public class Pointer {
    private final double X;
    private final double Y;
    private final double Angle;
    private final boolean Flipped;

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getAngle() {
        return Angle;
    }

    public boolean isFlipped() {
        return Flipped;
    }

    public Pointer(double x, double y, double angle, boolean flipped) {
        X = x;
        Y = y;
        Angle = angle;
        Flipped = flipped;
    }

    public Pointer() {
        X = 0;
        Y = 0;
        Angle = 0;
        Flipped = false;
    }

    public static Pointer multiply(Pointer inner, Pointer outter) {
        double radius = outter.Angle / 180 * Math.PI;
        return new Pointer(
                inner.getX() * Math.cos(radius) - inner.getY() * Math.sin(radius) + outter.getX(),
                inner.getY() * Math.cos(radius) + inner.getX() * Math.sin(radius) + outter.getY(),
                (outter.getAngle() + inner.getAngle()) % 360,
                outter.isFlipped() ^ inner.isFlipped()
        );
    }

    public static Pointer divide(Pointer product, Pointer outter) {
        Pointer inverse = new Pointer(-outter.getX(), -outter.getY(), -outter.getAngle(), outter.isFlipped());
        return multiply(product, inverse);
    }
}
