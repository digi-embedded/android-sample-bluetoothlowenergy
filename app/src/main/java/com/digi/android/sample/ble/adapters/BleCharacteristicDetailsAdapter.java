/**
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.digi.android.ble.BLEConnection;
import com.digi.android.ble.exceptions.BLEException;
import com.digi.android.ble.listeners.BLECharacteristicUpdateListener;
import com.digi.android.ble.utils.BLEUtils;
import com.digi.android.ble.utils.ByteUtils;
import com.digi.android.ble.utils.HexUtils;
import com.digi.android.sample.ble.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BleCharacteristicDetailsAdapter extends BaseAdapter implements BLECharacteristicUpdateListener {
	
	// Variables.
	private BluetoothGattCharacteristic bleCharacteristic = null;

	private LayoutInflater layoutInflater;

	private BLEConnection connection = null;
	
	private Date lastUpdateTime;

	private boolean notificationEnabled = false;
	
	private ProgressDialog progressDialog;
	
	private Activity parentActivity;
	
	/**
	 * Class constructor. Instantiates a new 
	 * {@code BleCharacteristicDetailsAdapter} object with the given parameters.
	 * 
	 * @param parentActivity Activity that holds this adapter.
	 */
	public BleCharacteristicDetailsAdapter(Activity parentActivity) {
		super();
		this.parentActivity = parentActivity;
		layoutInflater = parentActivity.getLayoutInflater();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return bleCharacteristic;
	}

	@Override
	public int getCount() {
		if (bleCharacteristic != null)
			return 1;
		return 0;
	}

	@Override
	public void characteristicValueUpdated(BluetoothDevice arg0, BluetoothGattCharacteristic arg1) {
		parentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lastUpdateTime = new Date();
				notifyDataSetChanged();
			}
		});
	}

	/**
	 * Sets the BLE Characteristic that will be used in this adapter.
	 * 
	 * @param characteristic BLE Characteristic that will be used in this 
	 *                       adapter.
	 */
	public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
		this.bleCharacteristic = characteristic;
		lastUpdateTime = null;
		notificationEnabled = false;
	}

	/**
	 * Retrieves the BLE characteristic that is used in this adapter.
	 * 
	 * @return BLE characteristic that is used in this adapter.
	 */
	public BluetoothGattCharacteristic getCharacteristic() {
		return bleCharacteristic;
	}

	/**
	 * Sets the BLE connection that will be used to interact with the
	 * configured BLE characteristic.
	 * 
	 * @param connection BLE Connection to use.
	 */
	public void setBLEConnection(BLEConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * Removes the BLE characteristic that is used in this adapter.
	 */
	public void clearCharacteristic() {
		if (notificationEnabled) {
			Thread writeThread = new Thread() {
				@Override
				public void run() {
					try {
						showNotificationsDialog();
						connection.disableCharacteristicValueUpdates(bleCharacteristic);
						notificationEnabled = false;
					} catch (BLEException e) {
						showErrorMessage("Error changing characteristic notifications status > " + e.getMessage());
					} finally {
						hideProgressDialog();
						bleCharacteristic = null;
					}
				}
			};
			writeThread.start();
		} else
			bleCharacteristic = null;
	}

	@Override
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup p) {
		// If the view does not exist create it.
		if (convertView == null)
			convertView = layoutInflater.inflate(R.layout.ble_characteristic_details_item, null);
		// Find view fields.
		// Texts.
		TextView serviceNameText = (TextView)convertView.findViewById(R.id.char_details_service);
		TextView serviceUuidText = (TextView)convertView.findViewById(R.id.char_details_service_uuid);
		TextView characteristicNameText = (TextView)convertView.findViewById(R.id.char_details_name);
		TextView characteristicUuidText = (TextView)convertView.findViewById(R.id.char_details_uuid);
		TextView characteristicDataTypeText = (TextView)convertView.findViewById(R.id.char_details_type);
		TextView characteristicPropertiesText = (TextView) convertView.findViewById(R.id.char_details_properties);
		TextView characteristicUpdatedDateText = (TextView) convertView.findViewById(R.id.char_details_timestamp);
		TextView characteristicValueStringText = (TextView) convertView.findViewById(R.id.char_details_value_string);
		TextView characteristicValueDecimalText = (TextView) convertView.findViewById(R.id.char_details_value_decimal);
		
		// Input texts.
		EditText characteristicHexValueInputText = (EditText) convertView.findViewById(R.id.char_details_hex_value);
		// Buttons.
		ToggleButton notificationsToggleButton = (ToggleButton) convertView.findViewById(R.id.char_details_notification_switcher);
		notificationsToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked == notificationEnabled)
					return; // No need to update anything.
				handleToggleNotificationsButtonPressed(isChecked);
			}
		} );
		Button readButton = (Button) convertView.findViewById(R.id.char_details_read_btn);
		readButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleReadButtonPressed();
			}
		});
		Button writeButton = (Button) convertView.findViewById(R.id.char_details_write_btn);
		writeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				EditText valueField = (EditText)((RelativeLayout)v.getParentForAccessibility()).findViewById(R.id.char_details_hex_value);
				String value =  valueField.getText().toString().toLowerCase(Locale.getDefault());
				byte[] dataToWrite = HexUtils.hexStringToByteArray(value);
				handleWriteButtonPressed(dataToWrite);
			}
		});
		// Fill characteristic values.
		// Set service UUID.
		String ServiceUuid = bleCharacteristic.getService().getUuid().toString().toLowerCase(Locale.getDefault());
		serviceUuidText.setText(ServiceUuid);
		// Set service name.
		serviceNameText.setText(BLEUtils.getServiceName(bleCharacteristic.getService()));
		// Set characteristic UUID.
		String uuid = bleCharacteristic.getUuid().toString().toLowerCase(Locale.getDefault());
		characteristicUuidText.setText(uuid);
		// Set characteristic name.
		characteristicNameText.setText(BLEUtils.getCharacteristicName(bleCharacteristic));
		// Set characteristic data format.
		int format = BLEUtils.getCharacteristicValueFormat(bleCharacteristic);
		characteristicDataTypeText.setText(BLEUtils.getValueFormatName(format));
		// Set characteristic properties.
		int props = bleCharacteristic.getProperties();
		String propertiesString = String.format("0x%04X [", props);
		if (BLEUtils.isCharacteristicReadable(bleCharacteristic))
			propertiesString += "read ";
		if (BLEUtils.isCharacteristicWritable(bleCharacteristic))
			propertiesString += "write ";
		if (BLEUtils.canCharacteristicNotify(bleCharacteristic))
			propertiesString += "notify ";
		if (BLEUtils.canCharacteristicIndicate(bleCharacteristic))
			propertiesString += "indicate ";
		if (BLEUtils.isCharacteristicWritableWithoutResponse(bleCharacteristic))
			propertiesString += "write_no_response ";
		propertiesString += "]";
		characteristicPropertiesText.setText(propertiesString);
		// Set notifications toggle button status.
		notificationsToggleButton.setEnabled(BLEUtils.canCharacteristicNotify(bleCharacteristic));
		notificationsToggleButton.setChecked(notificationEnabled);
		// Set read button status.
		readButton.setEnabled(BLEUtils.isCharacteristicReadable(bleCharacteristic));
		// Set write button status.
		writeButton.setEnabled(BLEUtils.isCharacteristicWritable(bleCharacteristic) | BLEUtils.isCharacteristicWritableWithoutResponse(bleCharacteristic));
		// Set characteristic hex value.
		characteristicHexValueInputText.setEnabled(writeButton.isEnabled());
		byte[] rawValue = bleCharacteristic.getValue();
		if (rawValue != null && rawValue.length > 0)
			characteristicHexValueInputText.setText(
					String.format("0x%s", HexUtils.byteArrayToHexString(rawValue)));
		// Set characteristic string value.
		if (rawValue != null)
			characteristicValueStringText.setText(new String(rawValue));
		// Set characteristic int value.
		if (rawValue != null)
			characteristicValueDecimalText.setText(String.format("%d", ByteUtils.byteArrayToInt(rawValue)));
		// Set last updated time.
		if (lastUpdateTime != null)
			characteristicUpdatedDateText.setText(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(lastUpdateTime));
		// Return the modified view.
		return convertView;
	}
	
	/**
	 * Handles what happens when the read button is pressed.
	 */
	private void handleReadButtonPressed() {
		Thread readThread = new Thread() {
			@Override
			public void run() {
				try {
					showReadingDialog();
					bleCharacteristic = connection.readCharacteristic(bleCharacteristic);
					parentActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							lastUpdateTime = new Date();
							notifyDataSetChanged();
						}
					});
				} catch (BLEException e) {
					showErrorMessage(e.getMessage());
				} finally {
					hideProgressDialog();
				}
			}
		};
		readThread.start();
	}
	
	/**
	 * Handles what happens when the write button is pressed.
	 * 
	 * @param value The characteristic value to write.
	 */
	private void handleWriteButtonPressed(final byte[] value) {
		Thread writeThread = new Thread() {
			@Override
			public void run() {
				try {
					showWritingDialog();
					connection.writeCharacteristic(bleCharacteristic, value);
				} catch (BLEException e) {
					showErrorMessage(e.getMessage());
				} finally {
					hideProgressDialog();
				}
			}
		};
		writeThread.start();
	}
	
	/**
	 * Handles what happens when the toggle notifications button is pressed.
	 * 
	 * @param checked The new check status.
	 */
	private void handleToggleNotificationsButtonPressed(final boolean checked) {
		Thread writeThread = new Thread() {
			@Override
			public void run() {
				try {
					showNotificationsDialog();
					if (checked)
						connection.enableCharacteristicValueUpdates(bleCharacteristic, BleCharacteristicDetailsAdapter.this);
					else
						connection.disableCharacteristicValueUpdates(bleCharacteristic);
					notificationEnabled = checked;
				} catch (BLEException e) {
					showErrorMessage("Error changing characteristic notifications status > " + e.getMessage());
				} finally {
					hideProgressDialog();
				}
			}
		};
		writeThread.start();
	}
	
	/**
	 * Shows the reading progress dialog.
	 */
	private void showReadingDialog() {
		parentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showProgressDialog(parentActivity.getResources().getString(R.string.reading_dialog_title), parentActivity.getResources().getString(R.string.reading_dialog_text));
			}
		});
	}
	
	/**
	 * Shows the writing progress dialog.
	 */
	private void showWritingDialog() {
		parentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showProgressDialog(parentActivity.getResources().getString(R.string.writing_dialog_title), parentActivity.getResources().getString(R.string.writing_dialog_text));
			}
		});
	}
	
	/**
	 * Shows the notifications progress dialog.
	 */
	private void showNotificationsDialog() {
		parentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showProgressDialog(parentActivity.getResources().getString(R.string.notifications_dialog_title), parentActivity.getResources().getString(R.string.notifications_dialog_text));
			}
		});
	}
	
	/**
	 * Hides the progress dialog.
	 */
	private void hideProgressDialog() {
		parentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
		parentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(parentActivity, message, Toast.LENGTH_LONG).show();
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
		progressDialog = new ProgressDialog(parentActivity);
		progressDialog.setCancelable(false);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.show();
	}
}
