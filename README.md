# EasyLocation

Easy Location for Android is a Library which makes it easy to get Location in Android Application, You need to take care of
- Google Play services availability Check, Update Google play Service Dialog
- Creation of GoogleApiClient and its callbacks connected,disconnected etc.
- Stopping and releasing resources for location updates
- Handling Location permission scenarios
- Checking Location services are On or Off
- Getting last known location is not so easy either
- Fallback to last known location if not getting location after certain duration

**EasyLocation** does all this stuff in background, so that you can concentrate on your business logic than handling all above

## Getting started

In your `build.gradle`:

Add the following maven{} line to your **PROJECT** build.gradle file

```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }   // add this line
		}
	}
```

**com.google.android.gms:play-services-location** dependency also needs to be added like this

**x.x.x** can be replaced with google play service latest version in your app is using [versions](https://developers.google.com/android/guides/releases) 

```gradle
 dependencies {
    compile 'com.github.vishal259:EasyLocation:1.0'
    compile "com.google.android.gms:play-services-location:x.x.x"
 }
```

Extend your `Activity` from `EasyLocationAppCompatActivity` or `EasyLocationActivity`:

*Create location request according to your needs*

```java
LocationRequest locationRequest = new LocationRequest()
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setInterval(4000)
        .setFastestInterval(4000);
```                        
*Create EasyLocation request, and set locationRequest created*
```java
EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
        .setLocationRequest(locationRequest)
        .setFallBackToLastLocationTime(4000)
        .build();
}
```
**Request Single location update like this**
```java
requestSingleLocationFix(easyLocationRequest);
```
**Or Request Multiple location updates like this**
```java
requestLocationUpdates(easyLocationRequest);
```

**You're good to go!**, You will get below callbacks now in your activity

```java
    @Override
    public void onLocationPermissionGranted() {
    }

    @Override
    public void onLocationPermissionDenied() {
    }

    @Override
    public void onLocationReceived(Location location) {
    }

    @Override
    public void onLocationProviderEnabled() {
    }

    @Override
    public void onLocationProviderDisabled() {
    }
```

**Additional Options**

Specify what messages you want to show to user using *EasyLocationRequestBuilder*
```java
EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
.setLocationRequest(locationRequest)
.setLocationPermissionDialogTitle(getString(R.string.location_permission_dialog_title))
.setLocationPermissionDialogMessage(getString(R.string.location_permission_dialog_message))
.setLocationPermissionDialogNegativeButtonText(getString(R.string.not_now))
.setLocationPermissionDialogPositiveButtonText(getString(R.string.yes))
.setLocationSettingsDialogTitle(getString(R.string.location_services_off))
.setLocationSettingsDialogMessage(getString(R.string.open_location_settings))
.setLocationSettingsDialogNegativeButtonText(getString(R.string.not_now))
.setLocationSettingsDialogPositiveButtonText(getString(R.string.yes))
.build();
```

## Library License

    Copyright 2016 Vishal Sojitra

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
