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
package com.example.android.blesample.adapters;

import java.util.ArrayList;
import java.util.Locale;

import com.digi.android.ble.utils.BLEUtils;
import com.example.android.blesample.R;

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
	private ArrayList<BluetoothGattService> bleServices;
	
	private LayoutInflater layoutInflater;
	
	/**
	 * Class constructor. Instantiates a new {@code BleServicesListAdapter}
	 * object with the given parameters.
	 * 
	 * @param parentActivity Activity that holds this adapter.
	 */
	public BleServicesListAdapter(Activity parentActivity) {
		super();
		bleServices  = new ArrayList<BluetoothGattService>();
		layoutInflater = parentActivity.getLayoutInflater();
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
	 * @param position
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
		TextView serviceName = (TextView)convertView.findViewById(R.id.peripheral_list_services_name);
		TextView serviceUuid = (TextView)convertView.findViewById(R.id.peripheral_list_services_uuid);
		TextView serviceType = (TextView)convertView.findViewById(R.id.peripheral_list_service_type);
		// Fill the fields with service values.
		// Fill UUID.
		String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
		serviceUuid.setText(uuid);
		// Fill name.
		String name = BLEUtils.getServiceName(service);
		serviceName.setText(name);
		// Fill type.
		if (BLEUtils.isServicePrimary(service))
			serviceType.setText("Primary");
		else
			serviceType.setText("Secondary");
		// Return the modified view.
		return convertView;
	}
}
