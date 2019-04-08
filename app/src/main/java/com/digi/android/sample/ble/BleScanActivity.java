/*
 * Copyright (c) 2014-2016, Digi International Inc. <support@digi.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.digi.android.sample.ble;

import com.digi.android.ble.BLEManager;
import com.digi.android.ble.exceptions.BLEException;
import com.digi.android.ble.listeners.BLEDeviceScanListener;
import com.digi.android.sample.ble.adapters.BleDeviceListAdapter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BleScanActivity extends Activity implements BLEDeviceScanListener {

	// Constants.
	private static final long SCANNING_TIMEOUT = 5 * 1000; /* 5 seconds */
	
	// Variables.
	private BLEManager bleManager;
	
	private BleDeviceListAdapter devicesListAdapter = null;
	
	private ListView bleDevicesList;
	
	private ProgressDialog progressDialog;
	
	private Button scanButton;

	private final ArrayList<String> wantedPermissions = new ArrayList<>();
	private ArrayList<String> permissionsToRequest;
	// Integer for permissions results request
	private static final int ALL_PERMISSIONS_RESULT = 1011;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		
		// Initialize UI Components.
		initializeUIComponents();
		
		// Retrieve BLE Manager.
		bleManager = BleSampleApplication.getInstance().getBLEManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			managePermissions();
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
		bleDevicesList = findViewById(R.id.ble_devices_list);
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
		scanButton = findViewById(R.id.scan_button);
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startScanning();
			}
		});
		Button clearButton = findViewById(R.id.clear_button);
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

	@TargetApi(Build.VERSION_CODES.M)
	private void managePermissions() {
		wantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
		wantedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

		permissionsToRequest = getPermissionsToRequest(wantedPermissions);

		if (permissionsToRequest.size() > 0) {
			requestPermissions(permissionsToRequest.toArray(
					new String[0]), ALL_PERMISSIONS_RESULT);
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	private ArrayList<String> getPermissionsToRequest(ArrayList<String> wantedPermissions) {
		ArrayList<String> result = new ArrayList<>();

		for (String p : wantedPermissions) {
			if (!hasPermission(p))
				result.add(p);
		}

		return result;
	}

	@TargetApi(Build.VERSION_CODES.M)
	private boolean hasPermission(String permission) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;

		return true;
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void askForPermissions(final List<String> permissionsRejected) {
		if (!shouldShowRequestPermissionRationale(permissionsRejected.get(0)))
			return;

		new AlertDialog.Builder(BleScanActivity.this).
				setMessage("Starting from Android 6.0 Marshmallow the system requires location "
						+ "permissions in order to scan for Bluetooth LE devices.\n\n"
						+ "Bluetooth beacons, like iBeacons or Eddystone beacons, may be used "
						+ "to determine the device's and user's location. This application is "
						+ "not using this information in any way.").
				setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
							requestPermissions(permissionsRejected.
									toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
						}
					}
				}).setNegativeButton("Cancel", null).create().show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch(requestCode) {
			case ALL_PERMISSIONS_RESULT:
				ArrayList<String> permissionsRejected = new ArrayList<>();
				for (String p : permissionsToRequest) {
					if (!hasPermission(p))
						permissionsRejected.add(p);
				}

				if (permissionsRejected.size() == 0)
					break;

				askForPermissions(permissionsRejected);

				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				break;
		}
	}
}