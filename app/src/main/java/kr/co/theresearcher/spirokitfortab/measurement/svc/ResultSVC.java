package kr.co.theresearcher.spirokitfortab.measurement.svc;

public class ResultSVC {

    private String hashed;
    private double vc;
    private double ic;
    private double erv;
    private double irv;
    private double vt;
    private long timestamp;
    private boolean isSelected;
    private boolean isPost;
    private String order;

    public ResultSVC(String hash) {
        this.hashed = hash;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(int post) {
        if (post == 0) {
            isPost = false;
        } else {
            isPost = true;
        }
    }

    public String getHashed() {
        return hashed;
    }

    public void setHashed(String hashed) {
        this.hashed = hashed;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getVc() {
        return vc;
    }

    public void setVc(double vc) {
        this.vc = vc;
    }

    public double getIc() {
        return ic;
    }

    public void setIc(double ic) {
        this.ic = ic;
    }

    public double getErv() {
        return erv;
    }

    public void setErv(double erv) {
        this.erv = erv;
    }

    public double getIrv() {
        return irv;
    }

    public void setIrv(double irv) {
        this.irv = irv;
    }

    public double getVt() {
        return vt;
    }

    public void setVt(double vt) {
        this.vt = vt;
    }
}
