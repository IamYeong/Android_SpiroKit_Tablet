package kr.co.theresearcher.spirokitfortab.calc;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.graph.Coordinate;

/**
 * 스파이로킷에서 들어온 데이터를 처리하고 가공하는 클래스입니다.
 * 단일 데이터와 데이터 배열 모두 처리할 수 있으며,
 * 그래프를 출력하기 위한 배열도 출력할 수 있습니다.
 * 메서드 독립성을 위해 각 메서드는 따로따로 사용할 수 있도록 했습니다.
 *
 * 그래프를 그릴 수 있는 데이터는
 * 데이터 하나가 의미하는 시간(Time(s)),
 * 데이터 하나가 의미하는 유속(Liter Per one Second(l/s)),
 * 데이터 하나가 의미하는 유량(Volume(L)).
 * 위 세 개의 데이터를 배열로 가질 수 있어야 하기 때문에
 * Coordinate 라는 클래스로 관리합니다.
 *
 * @see Coordinate
 *
 */
public class SpiroKitDataHandler {

    /**
     * 보정할 때 필요한 변수입니다.
     * 정적 변수가 아니기 때문에 해당 클래스를 인스턴스화해야 사용할 수 있으며
     * 관련 메서드 또한 인스턴스를 얻어야 호출할 수 있습니다.
     * '보정 완료된 이전 데이터' 를 의미합니다.
     */
    private int calibratedData = 0;

    /**
     * 데이터를 정수로 변환하는 메서드입니다
     * @param data BLE 에서 들어온 데이터 1개를 의미합니다
     * @return 변환한 정수입니다
     */
    public static int convert(String data) {

        //"123456789 " -> 123456789
        //정해진 포맷으로 입력되므로 마지막 문자만 자르고 숫자만 파싱
        if (data.length() == 10) return Integer.parseInt(data.substring(0, 9));
        else return Integer.parseInt(data);

    }

    /**
     * 데이터 1개를 보정합니다
     * @param calibrated 보정완료된 이전 데이터
     * @param pre 이전 데이터 원본
     * @param current 보정할 데이터
     * @return 보정된 데이터
     */
    private static int calibrate(int calibrated, int pre, int current) {

        int result = calibrated;

        if (pre % 100000000 == 0) return current;
        if (current % 100_000_000 == 0) return pre;
        if (((pre / 100_000_000) + (current / 100_000_000)) == 1) return current;

        if ((pre > 105_000_000) || (current > 105_000_000)) return (calibrated - (pre - current));
        if ((pre > 5_000_000) || (current > 5_000_000)) return (calibrated - (pre - current));

        result -= (pre - current) * 1.012d;

        return result;

    }

    /**
     * 데이터 1개가 의미하는 시간(단위 : 초)을 구합니다.
     * @param value 정수로 변환된 데이터
     * @return 데이터가 의미하는 시간
     */
    private static double getTime(int value) {

        double time = 0d;

        if (value >= 100000000) value -= 100000000;
        time = value * 0.0000000125d;
        if (time == 0d) return time;
        if ((1d / time) * 0.026d <= 0.12d) time = 0d;
        return time;

    }

    /**
     * 데이터 1개가 의미하는 유속(단위 : l/s)을 구합니다.
     * @param value 정수로 변환된 데이터
     * @return 데이터가 의미하는 유속
     */
    private static double getLPS(int value) {

        double lps = 0d;
        double time = getTime(value);
        if (time == 0d) return lps;
        lps = (1d / time) * 0.026d;
        if (value >= 100000000) lps *= -1d;
        return lps;

    }

    /**
     * 데이터 1개가 의미하는 유량(단위 : L)를 구합니다.
     * @param lps 유속(l/s)
     * @param time 시간(s)
     * @return 유량
     */
    private static double getVolume(double lps, double time) {

        if (Math.abs(lps) <= 0.12d) return 0d;
        return Math.abs(lps) * time;

    }

    /**
     * 데이터 배열을 보정 후 값 배열로 반환합니다.(그래프 데이터)
     * @param data 정수 데이터 배열
     * @return 시간/유속/유량의 구조체 역할인 객체 배열
     * @see Coordinate
     */
    public static List<Coordinate> getValues(List<Integer> data) {

        List<Integer> calibratedData = calibrateAll(data);
        List<Coordinate> values = new ArrayList<>();

        for (int i = 0; i < calibratedData.size(); i++) {

            int value = calibratedData.get(i);
            double time = getTime(data.get(i));
            double lps = getLPS(value);
            double volume = getVolume(lps, time);

            values.add(new Coordinate(time, lps, volume));

            Log.e("CALC : ", "value : " + value + ", time : " + time + ", LPS : " + lps + ", Volume : " + volume);

        }

        return values;

    }

    /**
     * 모든 정수데이터를 보정완료된 배열로 반환합니다.
     * @param data 보정 전 배열
     * @return 보정 후 배열
     */
    private static List<Integer> calibrateAll(List<Integer> data) {

        List<Integer> calibrated = new ArrayList<>(data); // 인자로 받은 data 배열 복사

        for (int i = 1; i < data.size(); i++) {

            int pre = data.get(i - 1);
            int current = data.get(i);

            calibrated.set(i, calibrate(calibrated.get(i - 1), pre, current));

        }

        return calibrated;

    }

    /**
     * 전체 데이터 중 가장 큰 유량을 내쉰 부분부터
     * 내쉬기 전 흡기했던 부분까지 잘라서 부분배열을 반환합니다
     * @param data 전체 호흡에 대한 데이터 배열
     * @return 흡기부터 가장 큰 유량을 뱉어낸 호기까지의 부분배열
     */
    public static List<Integer> filteringData(List<Integer> data) {

        //데이터 없다면 실행하지 않음
        if (data.size() == 0) {
            return new ArrayList<>();
        }

        float accVolume = 0f; // 최고 유량인 누적볼륨값
        float refVolume = 0f; // 최고 유량과 비교할 누적 볼륨값
        int startIndex = 0; // 타겟 부분배열의 시작 인덱스
        int endIndex = 0; // 타겟 부분배열의 종료 인덱스
        int refStartIndex = 0; // 시작 인덱스 임시 변수
        int refEndIndex = 0; // 종료 인덱스 임시 변수
        boolean isStarted = false; // 데이터 방향이 바뀌었을 때를 대비한 변수

        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            //흡기 데이터거나 마지막 데이터면 호기가 끝났다는 뜻이므로 기존 누적값과 비교
            if ((value >= 100_000_000) || (i == data.size() - 1)) {

                //비교 후 정산 작업, 최대누적값을 갱신하고 시작/종료 인덱스도 갱신
                if (refVolume > accVolume) {
                    accVolume = refVolume;

                    startIndex = refStartIndex;
                    endIndex = i;

                }

                //초기화
                refVolume = 0f;
                isStarted = false;

                //호기 데이터라면 값을 계산해서 레퍼런스 인덱스에 마킹하고, 레퍼런스 변수에 누적 시작
            } else if (value > 0) {

                double time = getTime(value);
                double lps = getLPS(value);
                double volume = getVolume(lps, time);
                refVolume += volume;

                if (!isStarted) {

                    isStarted = true;
                    refStartIndex = i;

                }

            }

        }

        //가장 큰 호기를 시작하는 인덱스가 마킹되었는데,
        //그 호기 전의 흡기도 배열에 포함돼야 함(IC부분임)
        //종료인덱스는 그대로 두고 시작인덱스를 흡기 혹은 첫 데이터까지 후퇴시키기
        for (int i = startIndex - 1; i >= 0; i--) {

            int value = data.get(i);

            //흡기라면 시작인덱스를 한칸 미루고, 아니라면 미루기 종료
            if (value > 100_000_000) {
                startIndex = i;
            } else {
                break;
            }

        }

        //필터링된 배열을 위한 새 배열을 만들고 Y 축 첫 값이 떠있는 배열을 방지하기 위해 0에 해당하는 값을 1개 추가
        List<Integer> filteredData = new ArrayList<>();
        filteredData.add(0);

        //가장 큰 호기였던 흡기부터 호기까지의 데이터를 복사
        for (int i = startIndex; i <= endIndex; i++) {
            filteredData.add(data.get(i));

        }

        return filteredData;

    }

    /**
     * 폐기능 검사에서 Volume - Time curve 그래프를 그리기 위한 배열을 잘라냅니다
     * @param data 전체 호흡 데이터
     * @return 가장 큰 유량을 뱉어낸 날숨 부분 배열
     */
    public static List<Coordinate> getForcedValues(List<Integer> data) {

        List<Coordinate> timeVolumeGraph = new ArrayList<>();
        List<Integer> filteredData = filteringData(data);
        List<Integer> values = calibrateAll(filteredData);

        for (int i = 0; i < filteredData.size(); i++) {

            if (filteredData.get(i) >= 100000000) continue;

            double time = getTime(filteredData.get(i));
            double lps = getLPS(values.get(i));
            double volume = getVolume(lps, time);

            timeVolumeGraph.add(new Coordinate(time, lps, volume));

        }

        return timeVolumeGraph;

    }

    /**
     * Vital Capacity, 폐의 용적 값입니다.
     * @param data 전체 호흡 데이터 배열
     * @return VC(Vital Capacity)
     */
    public static double getVC(List<Integer> data) {

        double vc = 0d;
        List<Integer> filteredData = filteringData(data);
        List<Integer> calibratedData = calibrateAll(filteredData);

        for (int i = 0; i < calibratedData.size(); i++) {

            int value = calibratedData.get(i);

            //흡기 데이터라면 패스
            if (value >= 100000000) continue;

            double time = getTime(value);
            double lps = getLPS(value);
            double volume = getVolume(lps, time);
            vc += volume;

        }

        return vc;

    }

    /**
     * FVC 를 할 동안의 총 시간을 구합니다.
     * @param data 전체 호흡 데이터 배열
     * @return FET(Forced Expiratory Time)
     */
    public static double getFET(List<Integer> data) {

        double fet = 0d;
        List<Integer> filteredData = filteringData(data);

        for (int i = 0; i < filteredData.size(); i++) {

            if (filteredData.get(i) >= 100000000) continue;

            double time = getTime(filteredData.get(i));
            fet += time;

        }

        return fet;
    }

    /**
     * IC, Inspiratory Capacity, 들이마신 용적을 구합니다.
     * @param data 전체 호흡 데이터 배열
     * @return IC(Inspiratory Capacity)
     */
    public static double getIC(List<Integer> data) {

        double ic = 0d;
        List<Integer> filteredData = filteringData(data);
        List<Integer> calibratedData = calibrateAll(filteredData);

        for (int i = 0; i < calibratedData.size(); i++) {

            int value = calibratedData.get(i);

            //호기 데이터라면 패스
            if (value < 100000000) continue;

            double time = getTime(value);
            double lps = getLPS(value);
            double volume = getVolume(lps, time);
            ic += volume;

        }

        return ic;

    }

    /**
     * 가장 큰 유량의 호기 중, 초반 1초 간의 호기 용적
     * @param data 전체 호흡 데이터 배열
     * @return EV1(Expiratory Volume in one second)
     */
    public static double getEV1(List<Integer> data) {

        double ev = 0d;
        double time = 0d;

        List<Integer> filteredData = filteringData(data);
        List<Integer> calibratedData = calibrateAll(filteredData);

        for (int i = 0; i < calibratedData.size(); i++) {

            int value = calibratedData.get(i);

            //흡기 데이터라면 패스
            if (value >= 100000000) continue;

            double t = getTime(filteredData.get(i));
            double lps = getLPS(value);
            double volume = getVolume(lps, t);

            //유량과 시간을 먼저 누적시킨 뒤 조건검사
            time += t;
            ev += volume;

            if (time > 1d) return ev;

        }

        //1초간 안 불었다면 지금까지 누적시킨 값을 반환
        return ev;

    }

    /**
     * 가장 큰 호기 유량 중 가장 빨랐던 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return PEF(Peak Expiratory Flow)
     */
    public static double getPEF(List<Integer> data) {

        double pef = 0d;

        List<Integer> filteredData = filteringData(data);
        List<Integer> calibratedData = calibrateAll(filteredData);

        for (int i = 0; i < calibratedData.size(); i++) {

            int value = calibratedData.get(i);

            //흡기 데이터라면 패스
            if (value >= 100000000) continue;

            double time = getTime(value);
            double lps = getLPS(value);

            //가장 큰 LPS 값으로 갱신해나가는 작업
            if (pef < lps) pef = lps;

        }

        return pef;

    }

    /**
     * 가장 큰 호기 유량 중 가장 빠른 유속 까지 도달 시간(단위 : s)
     * @param data 호흡 전체 데이터 배열
     * @return PET(Peak Expiratory Time)
     */
    public static double getPET(List<Integer> data) {

        double pef = 0d;
        double refTime = 0d;
        double pefTime = 0d;

        List<Integer> filteredData = filteringData(data);
        List<Integer> calibratedData = calibrateAll(filteredData);

        for (int i = 0; i < calibratedData.size(); i++) {

            int value = calibratedData.get(i);

            //흡기 데이터라면 패스
            if (value >= 100000000) continue;

            double time = getTime(value);
            double lps = getLPS(value);
            refTime += time;

            //가장 큰 LPS 값으로 갱신해나가는 작업
            if (pef < lps) {
                pef = lps;
                pefTime = refTime;
            }

        }

        return pefTime;

    }

    /**
     * 최대 호기 유량 전 흡기 떄의 최대 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return PIF(Peak Inspiratory Flow)
     */
    public static double getPIF(List<Integer> data) {

        double pif = 0d;

        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            //호기 데이터라면 패스
            if (value < 100000000) continue;

            double lps = getLPS(value);

            //음의 방향으로 가장 큰 LPS 값으로 갱신해나가는 작업
            if (pif > lps) pif = lps;

        }

        return pif;

    }

    /**
     * 통상 호흡량을 구함.
     * @param data 전체 호흡 데이터 배열
     * @return TV(Tidal Volume)
     */
    public static double getTV(List<Integer> data) {

        double tv = 0d;

        //데이터 없다면 실행하지 않음
        if (data.size() == 0) {
            return tv;
        }

        float accVolume = 0f; // 최고 유량인 누적볼륨값
        float refVolume = 0f; // 최고 유량과 비교할 누적 볼륨값
        int startIndex = 0; // 타겟 부분배열의 시작 인덱스
        int endIndex = 0; // 타겟 부분배열의 종료 인덱스
        int refStartIndex = 0; // 시작 인덱스 임시 변수
        int refEndIndex = 0; // 종료 인덱스 임시 변수
        boolean isStarted = false; // 데이터 방향이 바뀌었을 때를 대비한 변수

        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            //흡기 데이터거나 마지막 데이터면 호기가 끝났다는 뜻이므로 기존 누적값과 비교
            if ((value >= 100_000_000) || (i == data.size() - 1)) {

                //비교 후 정산 작업, 최대누적값을 갱신하고 시작/종료 인덱스도 갱신
                if (refVolume > accVolume) {
                    accVolume = refVolume;

                    startIndex = refStartIndex;
                    endIndex = i;

                }

                //초기화
                refVolume = 0f;
                isStarted = false;

                //호기 데이터라면 값을 계산해서 레퍼런스 인덱스에 마킹하고, 레퍼런스 변수에 누적 시작
            } else if (value > 0) {

                double time = getTime(value);
                double lps = getLPS(value);
                double volume = getVolume(lps, time);
                refVolume += volume;

                if (!isStarted) {

                    isStarted = true;
                    refStartIndex = i;

                }

            }

        }

        //가장 큰 호기를 시작하는 인덱스가 마킹되었는데,
        //그 호기 전의 흡기도 배열에 포함돼야 함(IC부분임)
        //종료인덱스는 그대로 두고 시작인덱스를 흡기 혹은 첫 데이터까지 후퇴시키기
        for (int i = startIndex - 1; i >= 0; i--) {

            int value = data.get(i);

            //흡기라면 시작인덱스를 한칸 미루고, 아니라면 미루기 종료
            if (value >= 100_000_000) {
                startIndex = i;
            } else {
                break;
            }

        }

        List<Integer> calibratedData = calibrateAll(data);

        //Start index ~ End index 까지 골라냈으니 바로 이전 VT 측정
        for (int i = startIndex - 1; i >= 0; i--) {

            int value = data.get(i);

            if (value < 100000000) {

                double lps = getLPS(calibratedData.get(i));
                double volume = getVolume(lps, getTime(data.get(i)));

                tv += volume;
            } else {

                break;
            }

        }

        return tv;

    }

    /**
     * 통상 호흡 중 날숨을 하고도 더 내쉴 수 있는 양
     * @param data 전체 호흡 데이터 배열
     * @return ERV(Expiratory Residual Volume)
     */
    public static double getERV(List<Integer> data) {

        double erv = 0d;

        List<Integer> filteredData = filteringData(data);

        double vc = getVC(filteredData);
        double ic = getIC(filteredData);
        erv = vc - ic;

        return erv;

    }

    /**
     * 통상 호흡 중 들이마시고서 더 들이마실 수 있는 양
     * @param data 전체 호흡 데이터 배열
     * @return IRV(Inspiratory Residual Volume)
     */
    public static double getIRV(List<Integer> data) {

        double irv = 0d;

        List<Integer> filteredData = filteringData(data);

        double vc = getVC(filteredData);
        double erv = getERV(filteredData);
        double tv = getTV(data);

        irv = vc - erv - tv;

        return irv;

    }

    /**
     * 최대 호기 중 25~75% 부분의 평균 유속(시간 기준)
     * @param data 전체 호흡 데이터 배열
     * @return FEF25-75(Forced Expiratory Flow at 25 and 75% of the pulmonary volume)
     */
    public static double getFEF_25to75(List<Integer> data) {

        double fef = 0d;
        int count = 0;
        double totalTime = 0d;
        double time = 0d;

        //호기 때의 총 시간을 누적
        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            if (value >= 100000000) continue;
            totalTime += getTime(value);

        }

        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            //흡기 데이터라면 패스
            if (value >= 100000000) continue;

            double t = getTime(value);
            time += t;

            //호기 시간 25~75% 구간이라면 평균을 계속 냄
            if (((time / totalTime) >= 0.25d) && ((time / totalTime) <= 0.75d)) {

                double lps = getLPS(value);
                count++;

                fef = ((fef * (double)(count - 1)) + lps) / (double)count;

            }

        }

        return fef;

    }

    /**
     * 최대 호기 중 25% 지점의 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return MEF25(Maximal Expiratory Flow at 25 % of the forced vital capacity)
     */
    public static double getMEF25(List<Integer> data) {

        double mef = 0d;
        double totalTime = 0d;
        double time = 0d;

        //호기 때의 총 시간을 누적
        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            if (value >= 100000000) continue;
            totalTime += getTime(value);

        }

        //호기 떄의 LPS 중 25% 지점의 LPS 를 선택 후 반환
        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            //흡기 데이터라면 패스
            if (value >= 100000000) continue;

            double t = getTime(value);
            time += t;

            if ((time / totalTime) >= 0.25d) {

                double lps = getLPS(value);
                mef = lps;
                break;

            }

        }

        return mef;

    }

    /**
     * 최대 호기 중 25% 지점의 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return MEF50(Maximal Expiratory Flow at 50 % of the forced vital capacity)
     */
    public static double getMEF50(List<Integer> data) {

        double mef = 0d;
        double totalTime = 0d;
        double time = 0d;

        //호기 때의 총 시간을 누적
        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            if (value >= 100000000) continue;
            totalTime += getTime(value);

        }

        //호기 떄의 LPS 중 50% 지점의 LPS 를 선택 후 반환
        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            //흡기 데이터라면 패스
            if (value >= 100000000) continue;

            double t = getTime(value);
            time += t;

            if ((time / totalTime) >= 0.5d) {

                double lps = getLPS(value);
                mef = lps;
                break;

            }

        }

        return mef;

    }

    /**
     * 최대 호기 중 25% 지점의 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return MEF75(Maximal Expiratory Flow at 25 % of the forced vital capacity)
     */
    public static double getMEF75(List<Integer> data) {

        double mef = 0d;
        double totalTime = 0d;
        double time = 0d;

        //호기 때의 총 시간을 누적
        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            if (value >= 100000000) continue;
            totalTime += getTime(value);

        }

        //호기 떄의 LPS 중 75% 지점의 LPS 를 선택 후 반환
        for (int i = 0; i < data.size(); i++) {

            int value = data.get(i);

            //흡기 데이터라면 패스
            if (value >= 100000000) continue;

            double t = getTime(value);
            time += t;

            if ((time / totalTime) >= 0.75d) {

                double lps = getLPS(value);
                mef = lps;
                break;

            }

        }

        return mef;

    }

    /**
     * 환자 정보를 이용한 FVC 예측값(단위 : L)
     * FVC : Forced Vital Capacity
     * @param age 만 나이(세)
     * @param height 신장(cm)
     * @param weight 체중(kg)
     * @param gender 성별(m, f)
     * @return FVC 예측값(L)
     */
    public static double getPredictFVC(int age, int height, int weight, String gender) {

        if (gender.equals("m")) {
            return -4.8434d - 0.00008633d * (double)(age * age) + 0.05292d * height + 0.01095d * weight;
        } else {
            return -3.0006d - 0.0001273d * (double)(age * age) + 0.03951d * height + 0.006892d * weight;
        }

    }

    /**
     * 환자 정보를 이용한 FEV1 예측값(단위 : L)
     * FEV1 : Forced Expiratory Volume in one second
     * @param age 만 나이(세)
     * @param height 신장(cm)
     * @param weight 체중(kg)
     * @param gender 성별(m, f)
     * @return FEV1 예측값(L)
     */
    public static double getPredictFEV1(int age, int height, int weight, String gender) {

        if (gender.equals("m")) {
            return -3.4132d - 0.0002484d * (double)(age * age) + 0.04578d * height;
        } else {
            return -2.4114d - 0.0001920d * (double)(age * age) + 0.03558d * height;
        }

    }

    /**
     * DB에서 꺼낸 데이터 묶음을 정수 배열로 변환한다
     * @param allData DB에서 꺼낸 데이터 묶음
     * @return 호흡 데이터 배열
     */
    public static List<Integer> convertAll(String allData) {

        String[] data = allData.split(" ");
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {

            int value = Integer.parseInt(data[i]);
            result.add(value);

        }

        return result;

    }

    /**
     * 변환 전 데이터 배열을 모두 정수배열로 변환한다(Activity 에서 가지고 있던 배열을 즉시 변환하기 위한 오버로딩)
     * @param data 구분자를 기준으로 나눠진 문자열 데이터 배열
     * @return 호흡 데이터 배열
     */
    public static List<Integer> convertAll(List<String> data) {

        List<Integer> convertedData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {

            convertedData.add(convert(data.get(i)));

        }

        return convertedData;

    }

    /**
     * 변환 전 데이터 배열을 모두 정수배열로 변환한다(String split 후 즉시 사용하기 위한 오버로딩)
     * @param data 구분자를 기준으로 나눠진 문자열 데이터 배열
     * @return 호흡 데이터 배열
     */
    public static List<Integer> convertAll(String[] data) {

        List<Integer> convertedData = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {

            convertedData.add(convert(data[i]));

        }

        return convertedData;

    }

    /**
     * 1개의 데이터를 즉시 보정하여 그래프를 그리기 위한 데이터로 변환하는 기능
     * @param pre 이전 값, 첫 데이터 등의 사유로 인해 없다면 null 을 입력해야 한다
     * @param data 보정할 값
     * calibratedData 가 함수 내에서 쓰이므로 해당 함수는 정적으로 선언되지 않았다.
     * 해당 클래스를 전역 인스턴스로 선언 후 이어서 사용해야 의미있다.
     *
     * Right way :
     * SpiroKitDataHandler instance = new SpiroKitDataHandler();
     * for (i -> data.size()) {
     *     data = instance.getValue(pre, data);
     * }
     *
     * Wrong way :
     * for (i -> data.size()) {
     *     data = new SpiroKitDataHandler() { getValue(pre, data); }
     * }
     * or
     * for (i -> data.size()) {
     *     SpiroKitDataHandler instance = new SpiroKitDataHandler();
     *     data = instance.getValue(pre, data);
     *
     * }
     *
     * @return 보정된 데이터
     */
    public Coordinate getValue(String pre, String data) {

        if (data == null) return null;

        int convertedData = convert(data);

        if (pre == null) {
            double time = getTime(convertedData);
            double lps = getLPS(convertedData);
            double volume = getVolume(lps, time);
            this.calibratedData = convertedData;
            return new Coordinate(time, lps, volume);
        }

        int convertedPreData = convert(pre);
        int calibrateData = calibrate(this.calibratedData, convertedPreData, convertedData);
        this.calibratedData = calibrateData;

        double t = getTime(convertedData);
        double time = getTime(calibrateData);
        double lps = getLPS(calibrateData);
        double volume = getVolume(lps, time);

        return new Coordinate(t, lps, volume);

    }



}


