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

package com.digi.android.sample.ble.adapters;

import java.util.ArrayList;
import java.util.Locale;

import com.digi.android.ble.utils.BLEUtils;
import com.digi.android.sample.ble.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BleServicesListAdapter extends BaseAdapter {
	
	// Variables.
	private final ArrayList<BluetoothGattService> bleServices;
	
	private final LayoutInflater layoutInflater;
	
	/**
	 * Class constructor. Instantiates a new {@code BleServicesListAdapter}
	 * object with the given parameters.
	 * 
	 * @param parentActivity Activity that holds this adapter.
	 */
	public BleServicesListAdapter(Activity parentActivity) {
		super();
		bleServices  = new ArrayList<>();
		layoutInflater = parentActivity.getLayoutInflater();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return getService(position);
	}

	@Override
	public int getCount() {
		return bleServices.size();
	}

	@Override
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		// If view does not exist create it.
		if (convertView == null)
			convertView = layoutInflater.inflate(R.layout.ble_service_list_item, null);
		// Retrieve the selected service.
		BluetoothGattService service = bleServices.get(position);
		// Retrieve the view fields.
		TextView serviceName = convertView.findViewById(R.id.peripheral_list_services_name);
		TextView serviceUuid = convertView.findViewById(R.id.peripheral_list_services_uuid);
		TextView serviceType = convertView.findViewById(R.id.peripheral_list_service_type);
		// Fill the fields with service values.
		// Fill UUID.
		String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
		serviceUuid.setText(uuid);
		// Fill name.
		String name = BLEUtils.getServiceName(service);
		serviceName.setText(name);
		// Fill type.
		if (BLEUtils.isServicePrimary(service))
			serviceType.setText(R.string.service_type_primary);
		else
			serviceType.setText(R.string.service_type_secondary);
		// Return the modified view.
		return convertView;
	}

	/**
	 * Adds the given service to the list of services.
	 * 
	 * @param service BLE Service to add.
	 */
	public void addService(BluetoothGattService service) {
		if (!bleServices.contains(service))
			bleServices.add(service);
	}
	
	/**
	 * Retrieves the BLE Service with at given position from the list.
	 * 
	 * @param position The position of the BLE service to get.
	 *
	 * @return BLE Service at the given position in the list.
	 */
	public BluetoothGattService getService(int position) {
		return bleServices.get(position);
	}

	/**
	 * Clears the services list.
	 */
	public void clearList() {
		bleServices.clear();
	}
}
