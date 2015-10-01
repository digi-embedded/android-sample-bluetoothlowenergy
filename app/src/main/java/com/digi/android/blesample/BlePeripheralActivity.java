/**
* Copyright (c) 2014 Digi International Inc.,
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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.digi.android.ble.BLEConnection;
import com.digi.android.ble.BLEManager;
import com.digi.android.ble.exceptions.BLEException;
import com.digi.android.ble.listeners.BLEConnectionListener;
import com.digi.android.ble.listeners.BLEDisoverServicesListener;
import com.digi.android.ble.listeners.BLERSSIUpdateListener;
import com.digi.android.ble.utils.BLEUtils;
import com.digi.android.blesample.adapters.BleCharacteristicDetailsAdapter;
import com.digi.android.blesample.adapters.BleCharacteristicsListAdapter;
import com.digi.android.blesample.adapters.BleServicesListAdapter;
import com.digi.android.blesample.models.ListType;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BlePeripheralActivity extends Activity implements BLERSSIUpdateListener, BLEConnectionListener, BLEDisoverServicesListener {

	// Constants.
	public static final String EXTRAS_DEVICE = "BLE_DEVICE";
	public static final String EXTRAS_DEVICE_RSSI = "BLE_DEVICE_RSSI";
	
	private static final int CONNECT_TIMEOUT = 30000;

	// Variables.
	private ListType listType = ListType.GATT_SERVICES;
	
	private int bleDeviceRSSI;

	private BluetoothDevice bleDevice;
	
	private BLEManager bleManager;

	private BLEConnection connection;
	
	private TextView deviceNameText;
	private TextView deviceAddressText;
	private TextView deviceRssiText;
	private TextView headerTitleText;
	
	private ImageView headerBackImage;
	
	private ImageView rssiImage;
	
	private Button connectButton;
	private Button disconnectButton;
	private Button backButton;
	
	private ListView mainList;
	
	private View listViewHeader;
	
	private BleServicesListAdapter servicesListAdapter;
	
	private BleCharacteristicsListAdapter characteristicsListAdapter;
	
	private BleCharacteristicDetailsAdapter characteristicDetailsAdapter;
	
	private ProgressDialog progressDialog;
	
	private Timer connectTimer;
	
	private TimerTask connectTimerTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peripheral);

		// Initializes UI elements with their listeners.
		initializeUIElements();

		// Read intent values.
		final Intent intent = getIntent();
		bleDevice = intent.getExtras().getParcelable(EXTRAS_DEVICE);
		bleDeviceRSSI =  intent.getIntExtra(EXTRAS_DEVICE_RSSI, 0);
		updateRSSIValue(bleDeviceRSSI);
		
		// Update activity texts.
		deviceNameText.setText(bleDevice.getName());
		deviceAddressText.setText(bleDevice.getAddress());
		headerTitleText.setText("");
		
		// Initialize Bluetooth Low Energy manager.
		bleManager = BleSampleApplication.getInstance().getBLEManager();
	}

	/**
	 * Initializes all the UI elements with their listeners.
	 */
	@SuppressLint("InflateParams")
	private void initializeUIElements() {
		// Texts.
		deviceNameText = (TextView) findViewById(R.id.peripheral_name);
		deviceAddressText = (TextView) findViewById(R.id.peripheral_address);
		deviceRssiText = (TextView) findViewById(R.id.peripheral_rssi);
		// List.
		mainList = (ListView) findViewById(R.id.listView);
		mainList.setOnItemClickListener(listClickListener);
		listViewHeader = (View) getLayoutInflater().inflate(R.layout.ble_main_list_header, null, false);
		mainList.addHeaderView(listViewHeader);
		// List elements.
		headerTitleText = (TextView) listViewHeader.findViewById(R.id.peripheral_list_title);
		// Images.
		headerBackImage = (ImageView) listViewHeader.findViewById(R.id.peripheral_list_back);
		rssiImage = (ImageView) findViewById(R.id.rssi_image);
		// Buttons.
		connectButton = (Button) findViewById(R.id.connect_button);
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				connect();
			}
		});
		disconnectButton = (Button) findViewById(R.id.disconnect_button);
		disconnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnect();
			}
		});
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnect();
				close();
				onBackPressed();
			}
		});
	}
	
	// Click listener used by the list.
	private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("ITEM PRESSED: " + position);
			if (position == 0) { // Header was clicked, this means to execute BACK action.
				switch (listType) {
				case GATT_SERVICES:
					// This case will never occur.
					return;
				case GATT_CHARACTERISTICS:
					// Going back from a characteristic, fill list with services.
					fillServices();
					characteristicsListAdapter.clearList();
					break;
				case GATT_CHARACTERISTIC_DETAILS:
					// Going back from a characteristic details, fill list with characteristics.
					fillCharacteristicsFromService(characteristicDetailsAdapter.getCharacteristic().getService());
					characteristicDetailsAdapter.clearCharacteristic();
					break;
				}
			} else { // An element of the tree was pressed (service or characteristic)
				position = position -1;
				switch (listType) {
				case GATT_SERVICES:
					// Service selected, fill list with its characteristics.
					fillCharacteristicsFromService(servicesListAdapter.getService(position));
					break;
				case GATT_CHARACTERISTICS:
					// Characteristic selected, fill list with its details.
					fillCharacteristicDetails(characteristicsListAdapter.getCharacteristic(position));
					break;
				case GATT_CHARACTERISTIC_DETAILS:
				default:
					// Will never occur.
					break;
				}
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		// Initialize all the list adapters.
		if (servicesListAdapter == null)
			servicesListAdapter = new BleServicesListAdapter(this);
		if (characteristicsListAdapter == null)
			characteristicsListAdapter = new BleCharacteristicsListAdapter(this);
		if (characteristicDetailsAdapter == null)
			characteristicDetailsAdapter = new BleCharacteristicDetailsAdapter(this);
		// Change list type to services.
		switchListType(ListType.GATT_SERVICES);
		// Initialize the device connection.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				connect();
			}
		});
	};

	@Override
	protected void onPause() {
		super.onPause();
		// Clear all the list adapters.
		servicesListAdapter.clearList();
		characteristicsListAdapter.clearList();
		characteristicDetailsAdapter.clearCharacteristic();
		// Disconnect from the device.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				disconnect();
				close();
			}
		});
	};

	/**
	 * Connects to the remote BLE Device.
	 */
	private void connect() {
		// Check if already connected.
		if (connection != null && connection.isConnected())
			return;
		// Show progress dialog.
		showConnectDialog();
		// Check if connection exists.
		if (connection == null)
			connection = bleManager.getConnection(bleDevice);
		// Start connect timer task.
		startConnectTimer();
		// Connect.
		try {
			connection.connect(BlePeripheralActivity.this);
		} catch (BLEException e) {
			showErrorMessage(e.getMessage());
			hideProgressgDialog();
		}
	}
	
	/**
	 * Disconnects from the remote BLE Device.
	 */
	private void disconnect() {
		if (connection == null)
			return;
		connection.disconnect();
		// Clear all adapters.
		servicesListAdapter.clearList();
		characteristicsListAdapter.clearList();
		characteristicDetailsAdapter.clearCharacteristic();
		// Update UI.
		updateUIDeviceDisconnected();
	}
	
	/**
	 * Completely closes the connection with the device.
	 */
	private void close() {
		if (connection == null)
			return;
		connection.close();
	}
	
	/**
	 * Performs service discovery in the remote device.
	 */
	private void discoverServices() {
		// Discover and show the services.
		// Show progress dialog.
		showDiscoverServicesDialog();
		try {
			connection.discoverServices(this);
		} catch (BLEException e) {
			showErrorMessage(e.getMessage());
			hideProgressgDialog();
		}
	}
	
	/**
	 * Fills the BLE list of supported services. This action considers that
	 * services have already been discovered in the remote device.
	 */
	private void fillServices() {
		// We are touching UI so run on UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Change main list type to services.
				switchListType(ListType.GATT_SERVICES);
				// Update header title.
				headerTitleText.setText(bleDevice.getName() + getResources().getString(R.string.services_suffix));
				// Read services from connected device.
				List<BluetoothGattService> services = connection.getSupportedServices();
				for (BluetoothGattService service:services)
					servicesListAdapter.addService(service);
				// Notify adapter about data changed.
				servicesListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * Fills the BLE list with the given service characteristics.
	 * 
	 * @param service GATT service to read its characteristics.
	 */
	private void fillCharacteristicsFromService(final BluetoothGattService service) {
		// We are touching UI so run on UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Change main list type to characteristics.
				switchListType(ListType.GATT_CHARACTERISTICS);
				// Update header title.
				headerTitleText.setText(BLEUtils.getServiceName(service) + getResources().getString(R.string.characteristics_suffix));
				// Read characteristics from service.
				List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
				// Add the characteristics to the adapter.
				for (BluetoothGattCharacteristic characteristic : characteristics)
					characteristicsListAdapter.addCharacteristic(characteristic);
				// Notify adapter about data changed.
				characteristicsListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * Fills the BLE list with the details of the given service characteristic.
	 * 
	 * @param characteristic GATT characteristic to read its details.
	 */
	private void fillCharacteristicDetails(final BluetoothGattCharacteristic characteristic) {
		// We are touching UI so run on UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Change main list type to characteristic details.
				switchListType(ListType.GATT_CHARACTERISTIC_DETAILS);
				// Update header title.
				headerTitleText.setText(getResources().getString(R.string.title_characteristic_details));
				// Update characteristic entry in the adapter.
				characteristicDetailsAdapter.setCharacteristic(characteristic);
				// Notify adapter about data changed.
				characteristicDetailsAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * Updates the RSSI value with the given one.
	 * 
	 * @param rssi The new RSSI value.
	 */
	private void updateRSSIValue(final int rssi) {
		// We are touching UI so run on UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bleDeviceRSSI = rssi;
				deviceRssiText.setText(bleDeviceRSSI + getResources().getString(R.string.db_suffix));
				rssiImage.setImageDrawable(BleSampleApplication.getInstance().getRSSIImage(rssi));
			}
		});
	}
	
	/**
	 * Switches the list type depending on the given value.
	 * 
	 * @param listType The new list type value.
	 */
	private void switchListType(ListType listType) {
		this.listType = listType;
		switch (listType) {
		case GATT_SERVICES:
			servicesListAdapter.clearList();
			mainList.setAdapter(servicesListAdapter);
			headerBackImage.setVisibility(View.GONE);
			break;
		case GATT_CHARACTERISTICS:
			characteristicsListAdapter.clearList();
			mainList.setAdapter(characteristicsListAdapter);
			headerBackImage.setVisibility(View.VISIBLE);
			break;
		case GATT_CHARACTERISTIC_DETAILS:
			mainList.setAdapter(characteristicDetailsAdapter);
			headerBackImage.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	/**
	 * Shows the connecting progress dialog.
	 */
	private void showConnectDialog() {
		// We are working with UI so run on UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showProgressDialog(getResources().getString(R.string.connect_dialog_title), getResources().getString(R.string.connect_dialog_text));
			}
		});
	}
	
	/**
	 * Shows the services discover progress dialog.
	 */
	private void showDiscoverServicesDialog() {
		// We are working with UI so run on UI thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showProgressDialog(getResources().getString(R.string.discover_serviecs_dialog_title), getResources().getString(R.string.discover_serviecs_dialog_text));
			}
		});
	}
	
	/**
	 * Shows a progress dialog with the given parameters.
	 * 
	 * @param title Progress dialog title.
	 * @param message Progress dialog message.
	 */
	private void showProgressDialog(String title, String message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.show();
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
			}
		});
	}
	
	/**
	 * Shows the given error message as a toast.
	 * 
	 * @param message Error message to show.
	 */
	private void showErrorMessage(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BlePeripheralActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	/**
	 * Updates the UI after device disconnects.
	 */
	private void updateUIDeviceDisconnected() {
		// We will update the UI so run on UI Thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Update UI.
				disconnectButton.setEnabled(false);
				connectButton.setEnabled(true);
				deviceRssiText.setVisibility(View.INVISIBLE);
				rssiImage.setVisibility(View.INVISIBLE);
				// Switch to the default list type.
				switchListType(ListType.GATT_SERVICES);
			}
		});
	}

	/**
	 * Stops the connect timer task.
	 */
	private void stopConnectTimer() {
		if (connectTimer != null) {
			connectTimer.cancel();
			connectTimer.purge();
		}
		if (connectTimerTask != null)
			connectTimerTask.cancel();
	}
	
	/**
	 * Starts the connect timer task to avoid UI to be hang if
	 * connection never happens.
	 */
	private void startConnectTimer() {
		stopConnectTimer();
		connectTimer = new Timer();
		connectTimerTask = new TimerTask() {
			/*
			 * (non-Javadoc)
			 * @see java.util.TimerTask#run()
			 */
			public void run() {
				// Hide progress dialog.
				hideProgressgDialog();
				// Disconnect.
				disconnect();
			}
		};
		connectTimer.schedule(connectTimerTask, CONNECT_TIMEOUT);
	}
	
	@Override
	public void rssiValueRead(BluetoothDevice device, int rssi) {
		updateRSSIValue(rssi);
	}

	@Override
	public void deviceConnected(BluetoothDevice arg0) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Stop connect timer task.
				stopConnectTimer();
				// Hide progress dialog.
				hideProgressgDialog();
				// Update UI.
				connectButton.setEnabled(false);
				disconnectButton.setEnabled(true);
				deviceRssiText.setVisibility(View.VISIBLE);
				rssiImage.setVisibility(View.VISIBLE);
				// Set the connection to the Characteristic Details Adapter.
				characteristicDetailsAdapter.setBLEConnection(connection);
				// Discover services.
				discoverServices();
			}
		});
	}

	@Override
	public void deviceDisconnected(BluetoothDevice arg0) {
		disconnect();
	}

	@Override
	public void errorConnecting(String errorMessage) {
		// Stop connect timer task.
		stopConnectTimer();
		// Hide progress dialog.
		hideProgressgDialog();
		// Show error.
		showErrorMessage(errorMessage);
	}

	@Override
	public void errorDiscoveringServices(BluetoothDevice arg0, String arg1) {
		// Hide progress dialog.
		hideProgressgDialog();
		// Show error.
		showErrorMessage(arg1);
	}

	@Override
	public void servicesDiscovered(BluetoothDevice arg0, List<BluetoothGattService> arg1) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Hide progress dialog.
				hideProgressgDialog();
				// Fill services.
				fillServices();
				// Enable RSSI read.
				connection.enablePeriodicRSSIRead(BlePeripheralActivity.this);
			}
		});
	}
}