Routelandia android application use Google Play services to load Google Map

In oder to request from Google Map API, you need to set up the API key

	Displaying the debug certificate fingerprint

    Locate your debug keystore file. The file name is debug.keystore, and is created the first time you build your project. By default, it is stored in the same directory as your Android Virtual Device (AVD) files:

    OS X and Linux: ~/.android/
    Windows Vista and Windows 7: C:\Users\your_user_name\.android\
    If you are using Eclipse with ADT, and you're not sure where your debug keystore is located, you can select Windows > Prefs > Android > Build to check the full path, which you can then paste into a file explorer to locate the directory containing the keystore.

    List the SHA-1 fingerprint.

    For Linux or OS X, open a terminal window and enter the following:

    keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
    For Windows Vista and Windows 7, run:

    keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android


    The line that begins SHA1 contains the certificate's SHA-1 fingerprint. The fingerprint is the sequence of 20 two-digit hexadecimal numbers separated by colons

    The package name of Routelandia is edu.pdx.its.portal.routelandia

    After you know the SHA-1 and package name, go to https://console.developers.google.com/project to create API key
