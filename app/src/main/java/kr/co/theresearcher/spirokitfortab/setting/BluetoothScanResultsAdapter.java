package kr.co.theresearcher.spirokitfortab.setting;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;

public class BluetoothScanResultsAdapter extends RecyclerView.Adapter<BluetoothScanResultViewHolder> {

    private Context context;
    private List<ScanResult> scanResults;
    private OnDeviceLookupListener mListener;

    public BluetoothScanResultsAdapter(Context context) {
        this.context = context;
        scanResults = new ArrayList<>();
    }

    public void addResult(ScanResult result) {

        for (ScanResult r : scanResults) {
            if (r.getDevice().getAddress().equals(result.getDevice().getAddress())) {
                return;
            }
        }

        scanResults.add(result);
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

        String name = result.getDevice().getName();
        String macAddress = result.getDevice().getAddress();
        String decibel = Integer.toString(result.getTxPower());

        if (name != null) {
            holder.getNameText().setText(name);
        } else {
            holder.getNameText().setText(context.getString(R.string.null_string));
        }

        if (macAddress != null) {
            holder.getAddressText().setText(macAddress);
        } else {
            holder.getAddressText().setText(context.getString(R.string.null_string));
        }

        holder.getDecibelText().setText(decibel);

        holder.getConstraintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDeviceLookup(result);
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
