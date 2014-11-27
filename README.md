Routelandia android application use Google Play services to load Google Map

In oder to request from Google Map API, you need to SHA-1 fingerprint and project package name to set up the API key

You can find your SHA-1 finger at

OS X and Linux: ~/.android/
Windows Vista and Windows 7: C:\Users\your_user_name\.android\

List the SHA-1 fingerprint.
For Linux or OS X, open a terminal window and enter the following:

    keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

For Windows Vista and Windows 7, run:

    keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

The line that begins SHA1 contains the certificate's SHA-1 fingerprint. The fingerprint is the sequence of 20 two-digit hexadecimal numbers separated by colons

The package name of Routelandia is edu.pdx.its.portal.routelandia

After you know the SHA-1 and package name, go to https://console.developers.google.com/project to create API key

You need to create a key for public API access and clienID on OAuth 2.0

Your API key should start with Alza, put your API key in AndroidManifest.xml at google_maps_key

    android:name="com.google.android.maps.v2.API_KEY"
    android:value="@string/google_maps_key"


