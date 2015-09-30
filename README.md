Bluetooth Low energy Sample Application
=======================================

This application demonstrates the usage of the Bluetooth Low
Energy API in Android. The application scans for Bluetooth Low Energy
devices in range, allows to connect to them, lists their services and 
characteristics and allows to read and write their values.

Demo requeriments
-----------------

To run this example you will need:
    - One compatible device to host the application.
    - Network connection between the device and the host PC in order to
      transfer and launch the application.
    - Establish remote target connection to your Digi hardware before running
      this application.
    - A remote Bluetooth Low Energy device to connect to

Demo setup
----------

Make sure the hardware is set up correctly:
    - The device is powered on.
    - The remote Bluetooth Low energy device is powered on and in range.
    - The device is connected directly to the PC or to the Local 
      Area Network (LAN) by the Ethernet cable.

Demo run
--------

The example is already configured, so all you need to do is to build and 
launch the project.
  
The first page of the application will perform a scan to discover available
Bluetooth Low energy devices in range. Once scan finishes, you will be able
to select any discovered device from the list.
  
When a device is selected from the list, the application page will change and
will perform a connection with the selected device in order to display some 
device properties and fill the right list with the available services.

If a service is selected, the list will be filled with the service
characteristics that can also be selected to check their values and details.

When a characteristic is selected, the list will be replaced with the 
characteristic details and several buttons to interact with it (read value, 
write value and enable characteristic value notifications). Some of these 
buttons may be disabled depending on the characteristic type.

You can go back to the characteristics list or services list by pressing the 
back arrow at the top of the list.

Finally, from the device page you can disconnect or go back to the devices list.

Tested on
---------

ConnectCore 6 SBC
ConnectCore 6 SBC v2