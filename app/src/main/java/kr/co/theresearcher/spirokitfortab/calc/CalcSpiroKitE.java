package kr.co.theresearcher.spirokitfortab.calc;

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

        measure();
    }

    public void measure() {

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

        measuredPulseWidth.add(0);
        for (int i = startIndex; i <= endIndex; i++) {
            measuredPulseWidth.add(pulseWidth.get(i));
        }

    }

    public List<ResultCoordinate> getFlowTimeGraph() {

        List<ResultCoordinate> flowTimes = new ArrayList<>();

        for (int i = 0; i < measuredPulseWidth.size(); i++) {

            double time = 0f;
            double rps = 0f;
            double lps = 0f;

            int pulseWidth = measuredPulseWidth.get(i);

            if (pulseWidth > 100_000_000) {

                time = Fluid.getTimeFromPulseWidthForE(pulseWidth);
                rps = Fluid.calcRevolutionPerSecond(time);
                lps = -1f * Fluid.conversionLiterPerSecond(rps);

            } else if (pulseWidth > 0) {

                time = Fluid.getTimeFromPulseWidthForE(pulseWidth);
                rps = Fluid.calcRevolutionPerSecond(time);
                lps = Fluid.conversionLiterPerSecond(rps);

            }

            ResultCoordinate coordinate = new ResultCoordinate(lps, time);
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

    public List<ResultCoordinate> getVolumeFlowGraph() {

        List<ResultCoordinate> volumeFlows = new ArrayList<>();
        List<ResultCoordinate> flowTimes = getFlowTimeGraph();

        for (int i = 1; i < flowTimes.size(); i++) {

            ResultCoordinate flowTime = flowTimes.get(i);
            double volume = Fluid.calcVolume(flowTimes.get(i - 1).getY(), flowTime.getY(), flowTime.getX());
            ResultCoordinate volumeFlow = new ResultCoordinate(Math.abs(volume), flowTime.getY());
            volumeFlows.add(volumeFlow);
        }

        return volumeFlows;
    }

    public double getFVC() {

        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int prePW = measuredPulseWidth.get(i - 1);
            int pw = measuredPulseWidth.get(i);

            if (pw < 100_000_000) {
                double volume = Fluid.calcVolume(
                        Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(prePW * HERTZ_80MHZ)),
                        Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(pw * HERTZ_80MHZ)),
                        pw * HERTZ_80MHZ
                );

                fvc += volume;
            }

        }

        return fvc;

    }

    public double getFev1() {

        double time = 0d;

        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int prePW = measuredPulseWidth.get(i - 1);
            int pw = measuredPulseWidth.get(i);

            if (pw < 100_000_000) {

                double volume = Fluid.calcVolume(
                        Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(prePW * HERTZ_80MHZ)),
                        Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(pw * HERTZ_80MHZ)),
                        pw * HERTZ_80MHZ
                );

                time += pw * HERTZ_80MHZ;
                if (time > 1d) break;
                fev1 += volume;
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

    public float getFVCp(long birth, int height, int weight, boolean gender) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birth);
        int year = calendar.get(Calendar.YEAR);

        calendar.setTime(Calendar.getInstance().getTime());

        int currentYear = calendar.get(Calendar.YEAR);

        if (gender) {
            return -4.8434f - 0.00008633f * (float)Math.pow(currentYear - year, 2) + 0.05292f * height + 0.01095f * weight;
        } else {
            return -3.0006f - 0.0001273f * (float)Math.pow(currentYear - year, 2) + 0.03951f * height + 0.006892f * weight;
        }

    }

    public float getFEV1p(long birth, int height, int weight, boolean gender) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birth);
        int year = calendar.get(Calendar.YEAR);

        calendar.setTime(Calendar.getInstance().getTime());

        int currentYear = calendar.get(Calendar.YEAR);

        if (gender) {
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
