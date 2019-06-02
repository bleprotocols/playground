Playground - web-based control for BLE and bluetooth devices. I built this to enable remote control of various bluetooth devices.

The author of this software does not want to be known.

Included is the source code for an android app that connects to supported devices, and a few php scripts ( in the web folder ) that enable another person to control said devices via the web.

Currently supports an erostek et-312B box connected to a bluetooth serial port ( https://www.ebay.com/itm/400453431708 ). The linked device needs to be set to 19200 baud - this can be done using AT commands:
https://www.instructables.com/id/AT-command-mode-of-HC-05-Bluetooth-module/

Bluetooth locks ( Anbound ) are also supported. These can be ordered from china, for instance here:
https://www.aliexpress.com/item/Motorcycle-Bluetooth-Smart-Lock-Electric-Bicycle-Secure-Mobile-Phone-Bluetooth-Padlock-Gray-Color-O-Keys-Required/32914353710.html
For these locks it's recommended to solder a wire to the bluetooth antenna - and extend it outside the lock so that the Bluetooth signal can reach the lock from further out.

Also supports the VisionBody EMS box. Encryption is currently not supported, but supposedly maybe later box models have an encryption key stored on their webserver. I have not seen encryption enabled in the wild, so
it's not enabled by default. I'll add the code to do the encryption later.

LoveSense devices are supported in a limited fashion - change the UUIDs to match those of your own vibrator.


