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
import java.util.Locale;

import com.digi.android.ble.utils.BLEUtils;
import com.digi.android.sample.ble.R;

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
	private final ArrayList<BluetoothGattCharacteristic> bleCharacteristics;
	
	private final LayoutInflater layoutInflater;

	/**
	 * Class constructor. Instantiates a new 
	 * {@code BleCharacteristicsListAdapter} object with the given parameters.
	 * 
	 * @param parent Activity that holds this adapter.
	 */
	public BleCharacteristicsListAdapter(Activity parent) {
		super();
		bleCharacteristics  = new ArrayList<>();
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
		TextView charName = convertView.findViewById(R.id.peripheral_list_characteristic_name);
		TextView charUuid = convertView.findViewById(R.id.peripheral_list_characteristic_uuid);
		ImageView readImage = convertView.findViewById(R.id.read_enabled_image);
		ImageView writeImage = convertView.findViewById(R.id.write_enabled_image);
		ImageView notifyImage = convertView.findViewById(R.id.notify_enabled_image);
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
