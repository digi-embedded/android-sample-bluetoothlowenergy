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

package com.digi.android.ble.adapters;

import java.util.ArrayList;

import com.digi.android.ble.BleSampleApplication;
import com.digi.android.ble.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BleDeviceListAdapter extends BaseAdapter {

	// Variables.
	private ArrayList<BluetoothDevice> bleDevices;
	
	private ArrayList<Integer> bleDevicesRssi;

	private LayoutInflater layoutInflater;

	/**
	 * Class constructor. Instantiates a new {@code BleDeviceListAdapter}
	 * object with the given parameters.
	 * 
	 * @param parentActivity Activity that holds this adapter.
	 */
	public BleDeviceListAdapter(Activity parentActivity) {
		super();
		// Initialize variables.
		bleDevices  = new ArrayList<BluetoothDevice>();
		bleDevicesRssi = new ArrayList<Integer>();
		layoutInflater = parentActivity.getLayoutInflater();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return bleDevices.size();
	}

	@Override
	public Object getItem(int position) {
		return getDevice(position);
	}

	@Override
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		// If view is not available create it.
		if (convertView == null)
			convertView = layoutInflater.inflate(R.layout.ble_device_list_item, null);

		// Retrieve the fields.
		TextView deviceAddress = (TextView)convertView.findViewById(R.id.device_address);
		TextView deviceName = (TextView)convertView.findViewById(R.id.device_name);
		TextView deviceRssi = (TextView)convertView.findViewById(R.id.device_rssi);
		ImageView rssiImage = (ImageView)convertView.findViewById(R.id.rssi_image);

		// Retrieve the BLE Device.
		BluetoothDevice device = bleDevices.get(position);
		// Fill the fields values.
		// Fill RSSI.
		int rssi = bleDevicesRssi.get(position);
		String rssiString = (rssi == 0) ? "N/A" : rssi + " db";
		deviceRssi.setText(rssiString);

		// Fill name.
		String name = device.getName();
		if (name == null || name.length() <= 0)
			name = "Unknown Device";
		deviceName.setText(name);

		// Fill address.
		String address = device.getAddress();
		deviceAddress.setText(address);

		// Fill RSSI image.
		rssiImage.setImageDrawable(BleSampleApplication.getInstance().getRSSIImage(rssi));

		return convertView;
	}

	/**
	 * Adds the given Bluetooth Low Energy device to the list of BLE devices.
	 *  
	 * @param device Device to add.
	 * @param rssi RSSI reported by the device.
	 */
	public void addDevice(BluetoothDevice device, int rssi) {
		if (!bleDevices.contains(device)) {
			bleDevices.add(device);
			bleDevicesRssi.add(rssi);
		} else {
			int position = bleDevices.indexOf(device);
			bleDevicesRssi.set(position, rssi);
		}
	}

	/**
	 * Retrieves the device at the given position.
	 * 
	 * @param position Position of the device to retrieve.
	 * @return BLE Device corresponding to the given position.
	 */
	public BluetoothDevice getDevice(int position) {
		return bleDevices.get(position);
	}

	/**
	 * Retrieves the RSSI value for the given device position.
	 * 
	 * @param position Position of the device to retrieve its RSSI value.
	 * @return The RSSI value of the device.
	 */
	public int getRssi(int position) {
		return bleDevicesRssi.get(position);
	}

	/**
	 * Clears the devices list.
	 */
	public void clearList() {
		bleDevices.clear();
		bleDevicesRssi.clear();
	}
}