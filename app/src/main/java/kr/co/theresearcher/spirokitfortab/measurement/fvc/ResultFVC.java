package kr.co.theresearcher.spirokitfortab.measurement.fvc;

public class ResultFVC {

    private double fvc;
    private double fev1;
    private double pef;
    private double fev1percent;
    private double fvcPredict;
    private double fev1Predict;
    private double pefPredict;
    private double fev1PercentPredict;
    private boolean isSelected;
    private boolean isPost;
    private long timestamp;

    public ResultFVC() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public double getFvc() {
        return fvc;
    }

    public void setFvc(double fvc) {
        this.fvc = fvc;
    }

    public double getFev1() {
        return fev1;
    }

    public void setFev1(double fev1) {
        this.fev1 = fev1;
    }

    public double getPef() {
        return pef;
    }

    public void setPef(double pef) {
        this.pef = pef;
    }

    public double getFev1percent() {
        return fev1percent;
    }

    public void setFev1percent(double fev1percent) {
        this.fev1percent = fev1percent;
    }

    public double getFvcPredict() {
        return fvcPredict;
    }

    public void setFvcPredict(double fvcPredict) {
        this.fvcPredict = fvcPredict;
    }

    public double getFev1Predict() {
        return fev1Predict;
    }

    public void setFev1Predict(double fev1Predict) {
        this.fev1Predict = fev1Predict;
    }

    public double getPefPredict() {
        return pefPredict;
    }

    public void setPefPredict(double pefPredict) {
        this.pefPredict = pefPredict;
    }

    public double getFev1PercentPredict() {
        return fev1PercentPredict;
    }

    public void setFev1PercentPredict(double fev1PercentPredict) {
        this.fev1PercentPredict = fev1PercentPredict;
    }
}
