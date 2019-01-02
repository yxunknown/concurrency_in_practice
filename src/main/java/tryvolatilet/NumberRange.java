package tryvolatilet;

public class NumberRange {
    private volatile int lower;
    private volatile int upper;

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }

    public void setLower(int lower) {
        if (lower > this.upper) {
            throw new IllegalArgumentException("lower should less than upper");
        }
        this.lower = lower;
    }

    public void setUpper(int upper) {
        if (upper < this.lower) {
            throw new IllegalArgumentException("upper should greater than lower");
        }
        this.upper = upper;
    }

    // 该类的错误说明
    // 在多线程的情况下，假定初始条件为（0，5）
    // 在同一时间内A线程setLower(4), B线程setUpper(3)
    // 显然，这两个操作都会通过set函数的检查，但是得到的结果确是(4, 3)
}
