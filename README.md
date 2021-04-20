Bluetooth Low Energy Sample Application
=======================================

This application demonstrates the usage of the Bluetooth Low Energy API in
Android. The application scans for Bluetooth Low Energy devices in range, allows
to connect to them, lists their services and characteristics and allows to read
and write their values.

Demo requirements
-----------------

To run this example you need:

* One compatible device to host the application.
* A USB connection between the device and the host PC in order to transfer and
  launch the application.
* A remote Bluetooth Low Energy device to connect to.

Demo setup
----------

Make sure the hardware is set up correctly:

1. The device is powered on.
2. The device is connected directly to the PC by the micro USB cable.
3. The remote Bluetooth Low Energy device is powered on and in range.

Demo run
--------

The example is already configured, so all you need to do is to build and
launch the project.
  
The first page of the application will perform a scan to discover available
Bluetooth Low Energy devices in range. Once scan finishes, you will be able
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

Compatible with
---------------

* ConnectCore 6 SBC
* ConnectCore 6 SBC v3
* ConnectCore 8X SBC Pro

License
-------

Copyright (c) 2014-2021, Digi International Inc. <support@digi.com>

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.