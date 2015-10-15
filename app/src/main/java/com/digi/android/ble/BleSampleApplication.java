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

package com.digi.android.ble;

import android.app.Application;
import android.graphics.drawable.Drawable;

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
