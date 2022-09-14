package kr.co.theresearcher.spirokitfortab.calc;

import java.util.List;

import kr.co.theresearcher.spirokitfortab.graph.Coordinate;

//만약 BLE 에서 온 값을 해석한 결과가 정수가 아닌 타입이 된다면
//인터페이스에 제네릭을 추가하여 확장성을 높일 것.

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
public interface SpiroKitHandler {

    /**
     * 데이터를 정수로 변환하는 메서드입니다
     * @param data BLE 에서 들어온 데이터 1개를 의미합니다
     * @return 변환한 정수입니다
     */
    int convert(String data);

    /**
     * DB에서 꺼낸 데이터 묶음을 정수 배열로 변환한다
     * @param allData DB에서 꺼낸 데이터 묶음
     * @return 호흡 데이터 배열
     */
    List<Integer> convertAll(String allData);

    /**
     * 변환 전 데이터 배열을 모두 정수배열로 변환한다(Activity 에서 가지고 있던 배열을 즉시 변환하기 위한 오버로딩)
     * @param data 구분자를 기준으로 나눠진 문자열 데이터 배열
     * @return 호흡 데이터 배열
     */
    List<Integer> convertAll(List<String> data);

    /**
     * 변환 전 데이터 배열을 모두 정수배열로 변환한다(String split 후 즉시 사용하기 위한 오버로딩)
     * @param data 구분자를 기준으로 나눠진 문자열 데이터 배열
     * @return 호흡 데이터 배열
     */
    List<Integer> convertAll(String[] data);

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
    Coordinate getValue(String pre, String data);

    /**
     * convertAll(String) 된 데이터 배열을 보정 후 값 배열로 반환합니다.(그래프 데이터)
     * @param data 정수 데이터 배열
     * @return 시간/유속/유량의 구조체 역할인 객체 배열
     * @see Coordinate
     */
    List<Coordinate> getValues(List<Integer> data);


    /**
     * FVC 검사에서 Volume - Time curve 그래프를 그리기 위한 배열을 잘라냅니다
     * @param data 전체 호흡 데이터
     * @return 가장 큰 유량을 뱉어낸 날숨 부분 배열
     */
    List<Coordinate> getForcedValues(List<Integer> data);

    /**
     * Vital Capacity, 폐의 용적 값입니다.
     * @param data 전체 호흡 데이터 배열
     * @return VC(Vital Capacity)
     */
    double getVC(List<Integer> data);

    /**
     * FVC 를 할 동안의 총 시간을 구합니다.
     * @param data 전체 호흡 데이터 배열
     * @return FET(Forced Expiratory Time)
     */
    double getFET(List<Integer> data);

    /**
     * IC, Inspiratory Capacity, 들이마신 용적을 구합니다.
     * @param data 전체 호흡 데이터 배열
     * @return IC(Inspiratory Capacity)
     */
    double getIC(List<Integer> data);

    /**
     * 가장 큰 유량의 호기 중, 초반 1초 간의 호기 용적
     * @param data 전체 호흡 데이터 배열
     * @return EV1(Expiratory Volume in one second)
     */
    double getEV1(List<Integer> data);

    /**
     * 가장 큰 호기 유량 중 가장 빨랐던 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return PEF(Peak Expiratory Flow)
     */
    double getPEF(List<Integer> data);

    /**
     * 가장 큰 호기 유량 중 가장 빠른 유속 까지 도달 시간(단위 : s)
     * @param data 호흡 전체 데이터 배열
     * @return PET(Peak Expiratory Time)
     */
    double getPET(List<Integer> data);

    /**
     * 최대 호기 유량 전 흡기 떄의 최대 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return PIF(Peak Inspiratory Flow)
     */
    double getPIF(List<Integer> data);

    /**
     * 통상 호흡량을 구함.
     * @param data 전체 호흡 데이터 배열
     * @return TV(Tidal Volume)
     */
    double getTV(List<Integer> data);

    /**
     * 통상 호흡 중 날숨을 하고도 더 내쉴 수 있는 양
     * @param data 전체 호흡 데이터 배열
     * @return ERV(Expiratory Residual Volume)
     */
    double getERV(List<Integer> data);

    /**
     * 통상 호흡 중 들이마시고서 더 들이마실 수 있는 양
     * @param data 전체 호흡 데이터 배열
     * @return IRV(Inspiratory Residual Volume)
     */
    double getIRV(List<Integer> data);

    /**
     * 최대 호기 중 25~75% 부분의 평균 유속(시간 기준)
     * @param data 전체 호흡 데이터 배열
     * @return FEF25-75(Forced Expiratory Flow at 25 and 75% of the pulmonary volume)
     */
    double getFEF_25to75(List<Integer> data);

    /**
     * 최대 호기 중 25% 지점의 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return MEF25(Maximal Expiratory Flow at 25 % of the forced vital capacity)
     */
    double getMEF25(List<Integer> data);

    /**
     * 최대 호기 중 25% 지점의 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return MEF50(Maximal Expiratory Flow at 50 % of the forced vital capacity)
     */
    double getMEF50(List<Integer> data);

    /**
     * 최대 호기 중 25% 지점의 유속(단위 : l/s)
     * @param data 전체 호흡 데이터 배열
     * @return MEF75(Maximal Expiratory Flow at 25 % of the forced vital capacity)
     */
    double getMEF75(List<Integer> data);

    /**
     * 환자 정보를 이용한 FVC 예측값(단위 : L)
     * FVC : Forced Vital Capacity
     * @param age 만 나이(세)
     * @param height 신장(cm)
     * @param weight 체중(kg)
     * @param gender 성별(m, f)
     * @return FVC 예측값(L)
     */
    double getPredictFVC(int age, int height, int weight, String gender);

    /**
     * 환자 정보를 이용한 FEV1 예측값(단위 : L)
     * FEV1 : Forced Expiratory Volume in one second
     * @param age 만 나이(세)
     * @param height 신장(cm)
     * @param weight 체중(kg)
     * @param gender 성별(m, f)
     * @return FEV1 예측값(L)
     */
    double getPredictFEV1(int age, int height, int weight, String gender);

}
