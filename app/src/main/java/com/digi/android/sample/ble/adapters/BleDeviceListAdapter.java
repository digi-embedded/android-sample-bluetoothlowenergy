/*
 * Copyright (c) 2014-2021, Digi International Inc. <support@digi.com>
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

package com.digi.android.sample.ble.adapters;

import java.util.ArrayList;

import com.digi.android.sample.ble.BleSampleApplication;
import com.digi.android.sample.ble.R;

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
	private final ArrayList<BluetoothDevice> bleDevices;

	private final ArrayList<Integer> bleDevicesRssi;

	private final LayoutInflater layoutInflater;

	/**
	 * Class constructor. Instantiates a new {@code BleDeviceListAdapter}
	 * object with the given parameters.
	 * 
	 * @param parentActivity Activity that holds this adapter.
	 */
	public BleDeviceListAdapter(Activity parentActivity) {
		super();
		// Initialize variables.
		bleDevices  = new ArrayList<>();
		bleDevicesRssi = new ArrayList<>();
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
		TextView deviceAddress = convertView.findViewById(R.id.device_address);
		TextView deviceName = convertView.findViewById(R.id.device_name);
		TextView deviceRssi = convertView.findViewById(R.id.device_rssi);
		ImageView rssiImage = convertView.findViewById(R.id.rssi_image);

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