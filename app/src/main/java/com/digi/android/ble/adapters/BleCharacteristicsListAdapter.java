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
import java.util.Locale;

import com.digi.android.ble.utils.BLEUtils;
import com.digi.android.ble.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BleCharacteristicsListAdapter extends BaseAdapter {
	
	// Variables.
	private ArrayList<BluetoothGattCharacteristic> bleCharacteristics;
	
	private LayoutInflater layoutInflater;

	/**
	 * Class constructor. Instantiates a new 
	 * {@code BleCharacteristicsListAdapter} object with the given parameters.
	 * 
	 * @param parent Activity that holds this adapter.
	 */
	public BleCharacteristicsListAdapter(Activity parent) {
		super();
		bleCharacteristics  = new ArrayList<BluetoothGattCharacteristic>();
		layoutInflater = parent.getLayoutInflater();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return getCharacteristic(position);
	}

	@Override
	public int getCount() {
		return bleCharacteristics.size();
	}

	@Override
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		// If view does not exist create it.
		if (convertView == null)
			convertView = layoutInflater.inflate(R.layout.ble_characteristic_list_item, null);
		// Retrieve the selected characteristic.
		BluetoothGattCharacteristic ch = getCharacteristic(position);
		// Get view fields.
		TextView charName = (TextView)convertView.findViewById(R.id.peripheral_list_characteristic_name);
		TextView charUuid = (TextView)convertView.findViewById(R.id.peripheral_list_characteristic_uuid);
		ImageView readImage = (ImageView)convertView.findViewById(R.id.read_enabled_image);
		ImageView writeImage = (ImageView)convertView.findViewById(R.id.write_enabled_image);
		ImageView notifyImage = (ImageView)convertView.findViewById(R.id.notify_enabled_image);
		// Fill fields.
		// Fill UUID.
		String uuid = ch.getUuid().toString().toLowerCase(Locale.getDefault());
		charUuid.setText(uuid);
		// Fill Name.
		String name = BLEUtils.getCharacteristicName(ch);
		charName.setText(name);
		// Read Image.
		if (BLEUtils.isCharacteristicReadable(ch))
			readImage.setVisibility(View.VISIBLE);
		else
			readImage.setVisibility(View.GONE);
		// Write Image.
		if (BLEUtils.isCharacteristicWritable(ch))
			writeImage.setVisibility(View.VISIBLE);
		else
			writeImage.setVisibility(View.GONE);
		// Notify Image.
		if (BLEUtils.canCharacteristicNotify(ch))
			notifyImage.setVisibility(View.VISIBLE);
		else
			notifyImage.setVisibility(View.GONE);
		// Return modified view.
		return convertView;
	}

	/**
	 * Adds the given BLE Characteristic to the list of characteristics.
	 * 
	 * @param characteristic BLE Characteristic to add to the list.
	 */
	public void addCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (!bleCharacteristics.contains(characteristic))
			bleCharacteristics.add(characteristic);
	}

	/**
	 * Returns the characteristic at the given position from the list.
	 * 
	 * @param index Position of the characteristic in the list to retrieve.
	 * @return The characteristic at the given position.
	 */
	public BluetoothGattCharacteristic getCharacteristic(int index) {
		return bleCharacteristics.get(index);
	}

	/**
	 * Clears the BLE Characteristics list.
	 */
	public void clearList() {
		bleCharacteristics.clear();
	}
}
