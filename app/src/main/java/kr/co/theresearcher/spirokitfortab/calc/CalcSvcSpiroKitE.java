package kr.co.theresearcher.spirokitfortab.calc;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.Fluid;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;

public class CalcSvcSpiroKitE {

    /**
     * ESP32 : 80 MHz 로 설정함.
     */
    public static float HERTZ_80MHZ = 0.0000000125f;

    private double svc;
    private double ivc;
    private double erv;
    private double irv;
    private double vt;

    private List<Integer> measuredPulseWidth;
    private List<Integer> pulseWidth;

    public CalcSvcSpiroKitE(List<Integer> pulseWidthList) {
        measuredPulseWidth = new ArrayList<>();
        pulseWidth = new ArrayList<>();
        pulseWidth.addAll(pulseWidthList);

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

        for (int i = 0; i < pulseWidth.size(); i++) {

            int value = pulseWidth.get(i);

            float time = 0f;
            float rps = 0f;
            float lps = 0f;
            float volume = 0f;

            if ((value > 100_000_000) && (value < 200_000_000)) {
                //흡기
                value -= 100_000_000;

                time = (float) Fluid.getTimeFromPulseWidthForE(value);
                rps = (float)Fluid.calcRevolutionPerSecond(time);
                lps = (float)Fluid.conversionLiterPerSecond(rps);
                if (lps > 0.12f) {
                    volume = lps * time;
                } else {
                    continue;
                }


            } else if ((value > 0) && (value < 100_000_000)) {
                //호기

                time = (float) Fluid.getTimeFromPulseWidthForE(value);
                rps = (float)Fluid.calcRevolutionPerSecond(time);
                lps = (float)Fluid.conversionLiterPerSecond(rps);
                volume = (float)Fluid.calcVolume(time, lps);
                if (lps > 0.12f) {
                    volume = lps * time;
                } else {
                    continue;
                }
                volume *= -1f;

            } else {

            }

            volumeTimes.add(new ResultCoordinate(time, volume));

        }

        return volumeTimes;
    }

    public List<ResultCoordinate> getForcedVolumeTimeGraph() {

        List<ResultCoordinate> volumeTimes = new ArrayList<>();

        for (int i = 0; i < measuredPulseWidth.size() - 1; i++) {

            if (measuredPulseWidth.get(i) < 100_000_000) {

                int pw = measuredPulseWidth.get(i);
                int prePW = measuredPulseWidth.get(i - 1);

                double time = pw * HERTZ_80MHZ;
                double volume = Fluid.calcVolume(
                        Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(prePW * HERTZ_80MHZ)),
                        Fluid.conversionLiterPerSecond(Fluid.calcRevolutionPerSecond(pw * HERTZ_80MHZ)),
                        pw * HERTZ_80MHZ
                );

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
            double volume = Fluid.calcVolume(flowTimes.get(i - 1).getY(), flowTime.getY(), flowTime.getX());
            ResultCoordinate volumeFlow = new ResultCoordinate(Math.abs(volume), flowTime.getY());
            volumeFlows.add(volumeFlow);
        }

        return volumeFlows;
    }

    public double getVC() {

        for (int i = 1; i < measuredPulseWidth.size(); i++) {

            int pw = measuredPulseWidth.get(i);

            if (pw < 100_000_000) {

                double time = Fluid.getTimeFromPulseWidthForE(pw);
                double flow = Fluid.conversionLiterPerSecond(
                        Fluid.calcRevolutionPerSecond(time)
                );

                if (flow > 0.12d) {
                    double volume = flow * time;
                    svc += volume;
                }
            }

        }

        return svc;

    }

}
