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

package com.digi.android.sample.ble;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.digi.android.ble.BLEManager;
import com.digi.android.sample.ble.R;

/**
 * Bluetooth Low Energy sample application.
 *
 * <p>This application demonstrates the usage of the Bluetooth Low Energy API in
 * Android. The application scans for Bluetooth Low Energy devices in range, allows
 * to connect to them, lists their services and characteristics and allows to read
 * and write their values.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class BleSampleApplication extends Application {

	// Variables.
	private BLEManager bleManager;
	
	private static BleSampleApplication instance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		// Initialize managers.
		bleManager = new BLEManager(this);
	}
	
	/**
	 * Retrieves the application instance.
	 * 
	 * @return The application instance.
	 */
	public static BleSampleApplication getInstance() {
		return instance;
	}
	
	/**
	 * Returns the Bluetooth Low Energy manager.
	 * 
	 * @return The Bluetooth Low energy manager.
	 */
	public BLEManager getBLEManager() {
		return bleManager;
	}
	
	/**
	 * Retrieves the image drawable corresponding to the given RSSI value.
	 * 
	 * @param rssi The image drawable corresponding to the given RSSI value.
	 *
	 * @return An object that can be used to draw RSSI value.
	 */
	public Drawable getRSSIImage(int rssi) {
		if (rssi < -90)
			return getResources().getDrawable(R.drawable.rssi_1);
		else if (rssi < -72)
			return getResources().getDrawable(R.drawable.rssi_2);
		else if (rssi < -50)
			return getResources().getDrawable(R.drawable.rssi_3);
		else if (rssi < -25)
			return getResources().getDrawable(R.drawable.rssi_4);
		else
			return getResources().getDrawable(R.drawable.rssi_5);
	}
}
