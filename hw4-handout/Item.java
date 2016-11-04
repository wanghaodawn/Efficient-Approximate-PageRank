
public final class Item {
    private String key;
    private double pr;

    public Item() {
    }

    public Item(String key, double pr) {
        this.key = key;
        this.pr = pr;
    }

    public String getKey() {
        return this.key;
    }

    public double getPR() {
        double res = this.pr;
        return res;
    }

    public void setKey(String s) {
        this.key = s;
    }

    public void setPR(double pr) {
        this.pr = pr;
    }
}