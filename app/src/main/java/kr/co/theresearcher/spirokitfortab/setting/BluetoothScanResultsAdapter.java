package kr.co.theresearcher.spirokitfortab.setting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.bluetooth.BluetoothAttributes;

public class BluetoothScanResultsAdapter extends RecyclerView.Adapter<BluetoothScanResultViewHolder> {

    private Context context;
    private List<ScanResult> scanResults;
    private List<Boolean> selects;
    private OnDeviceLookupListener mListener;

    public BluetoothScanResultsAdapter(Context context) {
        this.context = context;
        scanResults = new ArrayList<>();
        selects = new ArrayList<>();
    }

    @SuppressLint("MissingPermission")
    public void addResult(ScanResult result) {

        //이름검사
        if (!result.getDevice().getName().toUpperCase(Locale.ROOT).contains("SPIROKIT")) {
            return;
        }

        for (ScanResult r : scanResults) {
            //중복 검사
            if (r.getDevice().getAddress().equals(result.getDevice().getAddress())) {
                return;
            }

        }

        scanResults.add(result);
        selects.add(false);
        notifyDataSetChanged();

    }

    public void clear() {

        scanResults.clear();
        selects.clear();
        notifyDataSetChanged();

    }

    public void setLookupListener(OnDeviceLookupListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public BluetoothScanResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_result, parent, false);
        return new BluetoothScanResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothScanResultViewHolder holder, int position) {

        ScanResult result = scanResults.get(holder.getAdapterPosition());

        if (selects.get(holder.getAdapterPosition())) {
            //holder.getConstraintLayout().setBackground(AppCompatResources.getDrawable(context, R.drawable.item_selected_scan_result));
        } else {
            //holder.getConstraintLayout().setBackgroundResource(0);
        }

        String name = result.getScanRecord().getDeviceName();
        String macAddress = result.getDevice().getAddress();

        //RSSI : Receive Signal Strength Indicator (dB)
        String decibel = Integer.toString(result.getRssi());

        if (name != null) {
            holder.getNameText().setText(name);
        } else {
            holder.getNameText().setText(context.getString(R.string.state_null));
        }

        if (macAddress != null) {
            holder.getAddressText().setText(macAddress);
        } else {
            holder.getAddressText().setText(context.getString(R.string.state_null));
        }

        holder.getDecibelText().setText(decibel);

        holder.getConstraintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < selects.size(); i++) selects.set(i, false);
                selects.set(holder.getAdapterPosition(), true);

                mListener.onDeviceLookup(result);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (scanResults != null ? scanResults.size() : 0);
    }
}

class BluetoothScanResultViewHolder extends RecyclerView.ViewHolder {

    private TextView nameText, addressText, decibelText;
    private ConstraintLayout constraintLayout;

    public BluetoothScanResultViewHolder(@NonNull View itemView) {
        super(itemView);

        nameText = itemView.findViewById(R.id.tv_scan_result_name);
        addressText = itemView.findViewById(R.id.tv_scan_result_mac_address);
        decibelText = itemView.findViewById(R.id.tv_scan_result_decibel);
        constraintLayout = itemView.findViewById(R.id.constraint_scan_result);

    }

    public TextView getNameText() {
        return nameText;
    }

    public void setNameText(TextView nameText) {
        this.nameText = nameText;
    }

    public TextView getAddressText() {
        return addressText;
    }

    public void setAddressText(TextView addressText) {
        this.addressText = addressText;
    }

    public TextView getDecibelText() {
        return decibelText;
    }

    public void setDecibelText(TextView decibelText) {
        this.decibelText = decibelText;
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public void setConstraintLayout(ConstraintLayout constraintLayout) {
        this.constraintLayout = constraintLayout;
    }
}
