package org.example;

public class Matrix3V3 {
    double[] values;
    public Matrix3V3(double[] values) {
        this.values = values;
    }
    public Matrix3V3 multiply(Matrix3V3 otherMatrix) {
        double[] result = new double[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i++) {
                    result[row * 3 + col] +=
                            this.values[row * 3 + i] * otherMatrix.values[i * 3 + col];
                }
            }
        }
        return new Matrix3V3(result);
    }
    public Point transform(Point in) {
        return new Point(
                in.x * values[0] + in.y * values[3] + in.z * values[6],
                in.x * values[1] + in.y * values[4] + in.z * values[7],
                in.x * values[2] + in.y * values[5] + in.z * values[8]
        );
    }
}
