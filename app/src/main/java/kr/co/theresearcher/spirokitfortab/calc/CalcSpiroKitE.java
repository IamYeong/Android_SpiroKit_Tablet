package kr.co.theresearcher.spirokitfortab.calc;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.Fluid;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;

public class CalcSpiroKitE {

    /**
     * ESP32 : 80 MHz 로 설정함.
     */
    public static float HERTZ_80MHZ = 0.0000000125f;

    private double fvc; // = SVC, VC
    private double fev1;
    private double pef;
    private double pif;
    private double fivc;
    private double pefTime;
    private double totalTime;
    private double mvv;

    private List<Integer> measuredPulseWidth;
    private List<Integer> pulseWidth;

    public CalcSpiroKitE(List<Integer> pulseWidthList) {
        measuredPulseWidth = new ArrayList<>();
        pulseWidth = new ArrayList<>();
        pulseWidth.addAll(pulseWidthList);

    }

    public void measure() {

        List<Integer> temp = new ArrayList<>(pulseWidth);

        for (int i = 1; i < pulseWidth.size(); i++) {

            int beforePW = temp.get(i - 1);
            int afterPW = temp.get(i);

            //Log.e("CAL : ", pulseWidth.get(i - 1) + "");

            if (((beforePW >= 100_000_000) && (afterPW >= 100_000_000))) {
                //둘다 흡기
                if ((afterPW > 105_000_000) || (beforePW > 105_000_000)) continue;

                int diff = (int)(((float)beforePW - (float)afterPW) * 1.012f);
                int pw = pulseWidth.get(i - 1) - diff;
                pulseWidth.set(i, pw);

            } else if ((beforePW < 100_000_000) && (afterPW < 100_000_000)) {
                //둘다 호기
                if ((afterPW > 5_000_000) || (beforePW > 5_000_000)) continue;

                int diff = (int)(((float)beforePW - (float)afterPW) * 1.012f);

                int pw = pulseWidth.get(i - 1) - diff;
                pulseWidth.set(i, pw);

            }

        }

        float accRotate = 0f;
        float refRotate = 0f;
        int startIndex = 0;
        int endIndex = 0;
        int refStartIndex = 0;
        int refEndIndex = 0;
        boolean isStarted = false;

        for (int i = 0; i < pulseWidth.size(); i++) {

            int pw = pulseWidth.get(i);

            if ((pw >= 100_000_000) || (i == pulseWidth.size() - 1)) {

                //비교 후 정산 작업
                if (refRotate > accRotate) {
                    accRotate = refRotate;

                    startIndex = refStartIndex;
                    endIndex = i;

                }

                //초기화
                refRotate = 0f;
                isStarted = false;

            } else if (pw > 0) {

                double time = Fluid.getTimeFromPulseWidthForE(pw);
                double rps = Fluid.calcRevolutionPerSecond(time);
                refRotate += rps;

                if (!isStarted) {

                    isStarted = true;
                    refStartIndex = i;

                }

            }

        }

        if (pulseWidth.size() == 0) {
            return;
        }

        for (int i = startIndex - 1; i >= 0; i--) {

            int pw = pulseWidth.get(i);

            if (pw > 100_000_000) {
                startIndex = i;
            } else {
                break;
            }

        }

        measuredPulseWidth.add(100_000_000);
        for (int i = startIndex; i <= endIndex; i++) {
            measuredPulseWidth.add(pulseWidth.get(i));
        }

    }

    public List<ResultCoordinate> getFlowTimeGraph() {

        List<ResultCoordinate> flowTimes = new ArrayList<>();

        for (int i = 0; i < pulseWidth.size(); i++) {

            double time = 0f;
            double rps = 0f;
            double lps = 0f;

            int pw = pulseWidth.get(i);

            if (pw > 100_000_000) {

                pw -= 100_000_000;
                time = Fluid.getTimeFromPulseWidthForE(pw);
                rps = Fluid.calcRevolutionPerSecond(time);
                lps = -1f * Fluid.conversionLiterPerSecond(rps);

            } else if (pw > 0) {

                time = Fluid.getTimeFromPulseWidthForE(pw);
                rps = Fluid.calcRevolutionPerSecond(time);
                lps = Fluid.conversionLiterPerSecond(rps);

            }

            ResultCoordinate coordinate = new ResultCoordinate(time, lps);
            flowTimes.add(coordinate);

        }

        return flowTimes;
    }

    public List<ResultCoordinate> getVolumeTimeGraph() {

        List<ResultCoordinate> volumeTimes = new ArrayList<>();
        List<ResultCoordinate> flowTimes = getFlowTimeGraph();

        for (int i = 0; i < flowTimes.size() - 1; i++) {

            ResultCoordinate flowTime = flowTimes.get(i + 1);

            if (flowTime.getY() > 0f) {
                double volume = Fluid.calcVolume(flowTimes.get(i).getY(), flowTime.getY(), flowTime.getX());

                ResultCoordinate volumeTime = new ResultCoordinate(flowTime.getY(), volume);
                volumeTimes.add(volumeTime);
            }

        }

        return volumeTimes;
    }

    public List<ResultCoordinate> getForcedVolumeTimeGraph() {

        List<ResultCoordinate> volumeTimes = new ArrayList<>();

        for (int i = 0; i < measuredPulseWidth.size() - 1; i++) {

            if (measuredPulseWidth.get(i) < 100_000_000) {

                int pw = measuredPulseWidth.get(i);


                double time = pw * HERTZ_80MHZ;
                double flow = Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(pw * HERTZ_80MHZ));
                double volume = 0d;
                if (Math.abs(flow) > 0.12f) {
                    volume = flow * time;
                } else {
                    continue;
                }

                volumeTimes.add(new ResultCoordinate(time, volume));
            }

        }

        return volumeTimes;

    }

    public List<ResultCoordinate> getVolumeFlowGraph() {

        List<ResultCoordinate> volumeFlows = new ArrayList<>();
        List<ResultCoordinate> flowTimes = getFlowTimeGraph();

        for (int i = 1; i < flowTimes.size(); i++) {

            ResultCoordinate flowTime = flowTimes.get(i);
            double time = flowTime.getX();
            double flow = flowTime.getY();
            double volume = 0d;
            if (Math.abs(flow) > 0.12f) volume = flow * time;
            ResultCoordinate volumeFlow = new ResultCoordinate(Math.abs(volume), flowTime.getY());
            volumeFlows.add(volumeFlow);
        }

        return volumeFlows;
    }

    public double getFVC() {

        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int pw = measuredPulseWidth.get(i);

            if (pw < 100_000_000) {

                double time = Fluid.getTimeFromPulseWidthForE(pw);
                double flow = Fluid.conversionLiterPerSecond(
                        Fluid.calcRevolutionPerSecond(time)
                );

                if (flow > 0.12d) {
                    double volume = flow * time;
                    fvc += volume;
                }
            }

        }

        return fvc;

    }

    public double getFev1() {

        double time = 0d;

        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int pw = measuredPulseWidth.get(i);

            if (pw < 100_000_000) {

                double t = pw * HERTZ_80MHZ;
                double flow = (Fluid.conversionLiterPerSecond(
                        Fluid.calcRevolutionPerSecond(t)
                ));

                if (flow > 0.12d) {
                    double volume = t * flow;
                    fev1 += volume;
                    time += t;
                }


                if (time > 1d) break;

            }

        }

        return fev1;
    }

    public double getPef() {

        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int pw = measuredPulseWidth.get(i);

            if (pw < 100_000_000) {

                double lps = Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(pw * HERTZ_80MHZ));
                if (lps > pef) pef = lps;

            }

        }

        return pef;
    }

    public double getPif() {

        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int pw = measuredPulseWidth.get(i);

            if (pw > 100_000_000) {

                pw -= 100_000_000;
                double lps = Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(pw * HERTZ_80MHZ));
                if (lps > pif) pif = lps;

            }

        }

        return pif;
    }

    public double getFivc() {

        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int prePW = measuredPulseWidth.get(i - 1);
            int pw = measuredPulseWidth.get(i);

            if (pw > 100_000_000) {
                pw -= 100_000_000;
                prePW -= 100_000_000;
                double volume = Fluid.calcVolume(
                        Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(prePW * HERTZ_80MHZ)),
                        Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(pw * HERTZ_80MHZ)),
                        pw * HERTZ_80MHZ
                );

                fivc += volume;
            }

        }

        return fivc;
    }

    public double getPefTime() {

        int pefIndex = 0;


        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int pw = measuredPulseWidth.get(i);

            if (pw < 100_000_000) {

                double lps = Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(pw * HERTZ_80MHZ));
                if (lps > pef) {
                    pef = lps;
                    pefIndex = i;
                }

            }

        }

        for (int i = 0; i <= pefIndex; i++) {
            int pw = measuredPulseWidth.get(i);

            if (pw < 100_000_000) {

                double time = pw * HERTZ_80MHZ;
                pefTime += time;

            }
        }

        return pefTime;
    }

    public double getMvv() {
        return mvv;
    }

    public float getFVCp(long birth, int height, int weight, String gender) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birth);
        int year = calendar.get(Calendar.YEAR);

        calendar.setTime(Calendar.getInstance().getTime());

        int currentYear = calendar.get(Calendar.YEAR);

        if (gender.equals("m")) {
            return -4.8434f - 0.00008633f * (float)Math.pow(currentYear - year, 2) + 0.05292f * height + 0.01095f * weight;
        } else {
            return -3.0006f - 0.0001273f * (float)Math.pow(currentYear - year, 2) + 0.03951f * height + 0.006892f * weight;
        }

    }

    public float getFEV1p(long birth, int height, int weight, String gender) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birth);
        int year = calendar.get(Calendar.YEAR);

        calendar.setTime(Calendar.getInstance().getTime());

        int currentYear = calendar.get(Calendar.YEAR);

        if (gender.equals("m")) {
            return -3.4132f - 0.0002484f * (float)Math.pow(currentYear - year, 2) + 0.04578f * height;
        } else {
            return -2.4114f - 0.0001920f * (float)Math.pow(currentYear - year, 2) + 0.03558f * height;
        }

    }

    public float getFEV1_FVCp(long birth, int height, int weight, boolean gender) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birth);
        int year = calendar.get(Calendar.YEAR);

        calendar.setTime(Calendar.getInstance().getTime());

        int currentYear = calendar.get(Calendar.YEAR);

        if (gender) {
            return 119.9004f - 0.3902f * (float)(currentYear - year) - 0.1268f * height;
        } else {
            return 97.8567f - 0.2800f * (float)(currentYear - year) - 0.01564f * height;
        }

    }

    public float getPEFp(long birth, int height, int weight, boolean gender) {
        //논문에도 정해진 예측식이 없었고 코스메드에도 없음. 앞으로 방법이 나올지도 모르는 일이라서 인터페이스에 넣어둔 함수.
        return 0;
    }



}
