package kr.co.theresearcher.spirokitfortab.bluetooth;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;

public class SpiroKitBluetoothLeService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothGatt bluetoothGatt;
    private BluetoothLeCallback bluetoothLeCallback;
    private boolean isConnect = false;

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Log.d(getClass().getSimpleName(), "CONNECT STATE : " + newState);


            if (newState == BluetoothProfile.STATE_CONNECTED) {

                bluetoothLeCallback.onConnectStateChanged(newState);

                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_CONNECTING) {

                isConnect = false;

            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {

                isConnect = false;

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                bluetoothLeCallback.onConnectStateChanged(newState);

                isConnect = false;

                disconnect();
                close();



                //if (initialize()) connect(SharedPreferencesManager.getBluetoothDeviceMacAddress(getApplicationContext()));
                //gatt.connect();
            }

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            Log.d(getClass().getSimpleName(), "onServiceDiscovered");

            bluetoothLeCallback.onDiscoverServices();

            List<BluetoothGattService> services = gatt.getServices();

            for (BluetoothGattService service : services) {

                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {

                    if (characteristic.getUuid().toString().equals(BluetoothAttributes.BLUETOOTH_LE_READ_CHARACTERISTIC_UUID)) {

                        gatt.setCharacteristicNotification(characteristic, true);

                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BluetoothAttributes.BLUETOOTH_LE_READ_DESCRIPTOR_UUID));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                        gatt.writeDescriptor(descriptor);

                        return;

                    }

                }

            }

            //BluetoothGattCharacteristic characteristic = findCharacteristics(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            readCharacteristic(characteristic);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            bluetoothLeCallback.onWriteCharacteristic();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            Log.d(getClass().getSimpleName(), "onDescriptorWrite");
            isConnect = true;
            bluetoothLeCallback.onDescriptorWrite();


        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            readCharacteristic(characteristic);

        }


    };

    public interface BluetoothLeCallback {

        void onReadCharacteristic(byte[] data);
        void onWriteCharacteristic();
        void onDescriptorWrite();
        void onDiscoverServices();
        void onConnectStateChanged(int status);

    }

    public void setBluetoothLeCallback(BluetoothLeCallback callback) {
        this.bluetoothLeCallback = callback;

    }

    public boolean initialize() {

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        //if (bluetoothManager == null) return false;

        bluetoothAdapter = bluetoothManager.getAdapter();
        //if (bluetoothAdapter == null) return false;

        return true;

    }

    @SuppressLint("MissingPermission")
    public boolean connect(String deviceAddress) {

        Log.d(getClass().getSimpleName(), "service connect() call");
        if (!initialize()) return false;

        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        bluetoothGatt = bluetoothDevice.connectGatt(getApplicationContext(), false, gattCallback);

        return true;

    }

    @SuppressLint("MissingPermission")
    public void disconnect() {

        if (bluetoothGatt != null) {

            bluetoothGatt.disconnect();
            //bluetoothGatt.close();

        }

    }

    @SuppressLint("MissingPermission")
    public void close() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            //bluetoothGatt = null;
            isConnect = false;
        }
    }

    public boolean isConnect() {
        return isConnect;
    }

    public SpiroKitBluetoothLeService() {
    }

    public class LocalBinder extends Binder {

        public SpiroKitBluetoothLeService getService() {
            return SpiroKitBluetoothLeService.this;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.d(getClass().getSimpleName(), "Service, onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //intentThread.interrupt();
        //intentThread = null;
        Log.d(getClass().getSimpleName(), "Service, onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(getClass().getSimpleName(), "Service, onCreate");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(getClass().getSimpleName(), "Service, onDestroy");
        destroyService();

    }

    private void destroyService() {

        disconnect();
        close();
        stopSelf();

    }


    @SuppressLint("MissingPermission")
    public void writeCharacteristic(String data) {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                for (BluetoothGattService service : bluetoothGatt.getServices()) {

                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {

                        if (characteristic.getUuid().toString().equals(BluetoothAttributes.BLUETOOTH_LE_WRITE_CHARACTERISTIC_UUID)) {

                            characteristic.setValue(data);
                            bluetoothGatt.writeCharacteristic(characteristic);

                            return;

                        }

                    }

                }

                Looper.loop();

            }
        };

        thread.start();

    }


    //
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (characteristic.getValue() != null) {

            if (bluetoothLeCallback != null) {
                bluetoothLeCallback.onReadCharacteristic(characteristic.getValue());
            }

        }

    }

    private BluetoothGattCharacteristic findCharacteristics(BluetoothGatt gatt) {

        List<BluetoothGattService> services = gatt.getServices();

        for (BluetoothGattService service : services) {

            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {

                if (characteristic.getUuid().toString().equals(BluetoothAttributes.BLUETOOTH_LE_READ_CHARACTERISTIC_UUID)) {

                    return characteristic;

                }

            }

        }

        return null;
    }

}