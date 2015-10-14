/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */

package com.digi.android.blesample;

import com.digi.android.ble.BLEManager;
import com.digi.android.ble.exceptions.BLEException;
import com.digi.android.ble.listeners.BLEDeviceScanListener;
import com.digi.android.blesample.adapters.BleDeviceListAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BleScanActivity extends Activity implements BLEDeviceScanListener {

	// Constants.
	private static final long SCANNING_TIMEOUT = 5 * 1000; /* 5 seconds */
	
	// Variables.
	private BLEManager bleManager;
	
	private BleDeviceListAdapter devicesListAdapter = null;
	
	private ListView bleDevicesList;
	
	private ProgressDialog progressDialog;
	
	private Button scanButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		
		// Initialize UI Components.
		initializeUIComponents();
		
		// Retrieve BLE Manager.
		bleManager = BleSampleApplication.getInstance().getBLEManager();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set the devices list adapter.
		devicesListAdapter = new BleDeviceListAdapter(this);
		bleDevicesList.setAdapter(devicesListAdapter);
		// Start scanning process.
		startScanning();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop scanning.
		stopScanning();
		// Clear devices list.
		devicesListAdapter.clearList();
	}

	@Override
	public void deviceScanStarted() {
		showScanProgressDialog();
	}

	@Override
	public void deviceScanStopped() {
		hideProgressgDialog();
	}

	@Override
	public void deviceFound(final BluetoothDevice bleDevice, final int rssi, final byte[] scanRecords) {
		addBLEDevice(bleDevice, rssi);
	}
	
	/**
	 * Initializes all the required UI Components with listeners.
	 */
	private void initializeUIComponents() {
		bleDevicesList = (ListView)findViewById(R.id.ble_devices_list);
		bleDevicesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				// Retrieve selected device.
				final BluetoothDevice device = devicesListAdapter.getDevice(i);
				if (device == null)
					return;
				// Stop scanning.
				stopScanning();
				// Start peripheral activity.
				startPeripheralActivity(device, devicesListAdapter.getRssi(i));
			}
		});

		// Buttons.
		scanButton = (Button)findViewById(R.id.scan_button);
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startScanning();
			}
		});
		Button clearButton = (Button) findViewById(R.id.clear_button);
		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				devicesListAdapter.clearList();
				devicesListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * Starts the scanning process.
	 */
	private void startScanning() {
		try {
			if (!bleManager.isInitialized())
				bleManager.initialize();
			bleManager.startBLEScanProcess(this, SCANNING_TIMEOUT);
		} catch (BLEException e) {
			Toast.makeText(this, "Error starting scan process > " + e.getMessage(), Toast.LENGTH_LONG).show();
			hideProgressgDialog();
		}
	}
	
	/**
	 * Stops the scanning process.
	 */
	private void stopScanning() {
		if (bleManager.isScanning())
			bleManager.stopBLEScanProcess();
	}

	/**
	 * Starts the Peripheral Activity using the given device and its RSSI value.
	 * 
	 * <p>This will start a second activity to display device details.</p>
	 * 
	 * @param device Bluetooth Low Energy Device to display.
	 * @param rssi Bluetooth Low Energy Device RSSI value.
	 */
	private void startPeripheralActivity(BluetoothDevice device, int rssi) {
		// Create the intent to be executed.
		final Intent intent = new Intent(this, BlePeripheralActivity.class);
		// Create data bundle to attach to the intent.
		Bundle data = new Bundle();
		// Store device.
		data.putParcelable(BlePeripheralActivity.EXTRAS_DEVICE, device);
		// Store read RSSI.
		data.putInt(BlePeripheralActivity.EXTRAS_DEVICE_RSSI, rssi);
		// Attach data to the intent.
		intent.putExtras(data);
		// Send the intent.
		startActivity(intent);
	}

	/**
	 * Shows a progress dialog with the given parameters.
	 */
	private void showScanProgressDialog() {
		// We are working with UI so run on UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				scanButton.setEnabled(false);
				progressDialog = new ProgressDialog(BleScanActivity.this);
				progressDialog.setCancelable(false);
				progressDialog.setTitle(getResources().getString(R.string.scan_dialog_title));
				progressDialog.setMessage(getResources().getString(R.string.scan_dialog_text));
				progressDialog.show();
			}
		});
	}
	
	/**
	 * Hides the progress dialog.
	 */
	private void hideProgressgDialog() {
		// We are working with UI so run on UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null)
					progressDialog.dismiss();
				scanButton.setEnabled(true);
			}
		});
	}
	
	/**
	 * Adds the given device to the list of discovered Bluetooth Low Energy
	 * devices.
	 * 
	 * @param bleDevice Bluetooth Low energy device to add.
	 * @param rssi The last RSSI value of the discovered device.
	 */
	private void addBLEDevice(final BluetoothDevice bleDevice, final int rssi) {
		// Add the new device using the UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				devicesListAdapter.addDevice(bleDevice, rssi);
				devicesListAdapter.notifyDataSetChanged();
			}
		});
	}
}