package kr.co.theresearcher.spirokitfortab.setting;

import android.bluetooth.le.ScanResult;

public interface OnDeviceLookupListener {
    void onDeviceLookup(ScanResult result);
}
