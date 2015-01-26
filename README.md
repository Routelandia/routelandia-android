Routelandia Android application use Google Play services to load Google Map
[![Build Status](https://travis-ci.org/PSU-Capstone-2014-B/routelandia-android.svg?branch=master)](https://travis-ci.org/PSU-Capstone-2014-B/routelandia-android)

## Requirements

 - Java 1.7 or later.
 - A Google Maps API key.

### API keys

Each Google Maps Web Service requires an API key or Client ID. API keys are
freely available with a Google Account at
https://developers.google.com/console. To generate a server key for
your project:

 1. Visit https://developers.google.com/console and log in with
    a Google Account.
 2. Select an existing project, or create a new project.
 3. Click **Enable an API**.
 4. Browse for the API, and set its status to "On". The Java Client for Google Maps Services
    accesses the following APIs:
    * Directions API
    * Google Maps API
 5. Once you've enabled the APIs, click **Credentials** from the left navigation of the Developer
    Console.
 6. In the "Public API access", click **Create new Key**.
 7. Choose **Server Key**.
 8. If you'd like to restrict requests to a specific IP address, do so now.
 9. Click **Create**.

Your API key should be 40 characters long, and begin with `AIza`.

