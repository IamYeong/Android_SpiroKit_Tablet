package kr.co.theresearcher.spirokitfortab;

public class Fluid {

    public static float HERTZ_80MHZ = 0.0000000125f;

    /**
     * @param digit 소수점 아래 몇 번째 자리까지 남길 것인지 정합니다.
     * @param value infinity 나 NaN이 우려되는 float 값을 소숫점 아래 digit만큼 자릅니다.
     * @return digit만큼 잘린 value를 반환합니다.
     */
    public static float autoRound(int digit, float value) {

        float num = 1f;

        for (int i = 0; i < digit; i++) {

            num *= 10f;

        }

        return (float)(Math.round(value * num) / num);

    }

    public static float autoRound(int digit, double value) {

        double num = 1d;

        for (int i = 0; i < digit; i++) {
            num *= 10d;
        }

        return (float)(Math.round(value * num) / num);

    }

    public static double getTimeFromPulseWidthForE(int pulse) {

        if (pulse > 100_000_000) {

            return (pulse - 100_000_000) * HERTZ_80MHZ;

        } else if (pulse > 0) {

            return pulse * HERTZ_80MHZ;
        } else {
            return 0f;
        }

    }

    public static double calcRevolutionPerSecond(double time) {

        //pw * clock 이 time 이 되는데, pw 가 0이라면 해당 연산에서 NaN 혹은 Infinity 가 나올 수 있음.
        if (time == 0d) return 0d;
        double rps = (1f / time);
        if (Double.isInfinite(rps) || Double.isNaN(rps)) return 0d;
        return rps;

    }

    public static double conversionLiterPerSecond(double rps) {

        return (rps * 0.026d);
    }

    public static double calcVolume(double time, double lps) {

        return time * lps;
    }

    public static double calcVolume(double preFlow, double currentFlow, double time) {

        return (currentFlow * time) - (((currentFlow - preFlow) * time) / 2d);

    }

    public static double calcVolume(int prePW, int currentPW) {

        double beforeLPS = conversionLiterPerSecond(calcRevolutionPerSecond(getTimeFromPulseWidthForE(prePW)));
        double afterLPS = conversionLiterPerSecond(calcRevolutionPerSecond(getTimeFromPulseWidthForE(currentPW)));

        double time = getTimeFromPulseWidthForE(currentPW);

        return (afterLPS * time) - (((afterLPS - beforeLPS) * time) / 2d);

    }

    public static long ratioCalcX1000(long numerator, long denominator, long operand) {

        return (((numerator * 1000) / denominator) * operand) / 1000;

    }


}
