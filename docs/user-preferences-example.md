# User Preferences Developer Guide

## Requirements

1. Allow teams to store and retrieve user preferences.
2. Currently, Admin server is used to fetch or return all of the global user preferences.
3. Folks from other teams (investigate or respond) run one server locally that could be investigate or respond server,
in order to save respond or investigate specific user preferences, the following code sample below can be utilized to hold
these preferences in their server, or Admin server could be used alongside their primary data source.
4. The easiest way to store user preferences is to utilize the Admin server as it is already configured with all the necessary socket connection settings.
5. Before adding a new user preference, ensure that the preference is supported in the Admin server.
6. To set any preference, the name of the preference has to be embedded into the JSON object that is sent along with the payload.
7. A single property, multiple properties or a whole object is supported as long as the property name matches with the corresponding name
in Admin server.
8. On fetching the preferences from Admin server, currently they are not cached in local storage but instead default options are set incase
we don't get a response back from the service or the backend service is not available.
9. Currently the preferences for Language, Time Zone, Date Format, Time Format and Default Landing Page exist in the Admin Server.


## Current Default options when backend is not running

```javascript
'i18n.locale': 'en-us',
'dateFormat.selected': 'MM/dd/yyyy',
'timeFormat.selected': 'HR24',
'timezone.selected': 'UTC',
'landingPage.selected': '/respond'
```
## Getting user preferences

```javascript
const timezonesPromise = new Promise((resolve) => {
  const forceResolve = later(() => {
    this.set('timezone.options', [{
      'displayLabel': 'UTC (GMT+00:00)',
      'offset': 'GMT+00:00',
      'zoneId': 'UTC'
    }]);
    resolve();
  }, 3500);
this.request.promiseRequest({
  method: 'getPreference',
  modelName: 'preferences',
  query: {}
}).then((response) => {
  const {
    userLocale,
    dateFormat,
    timeFormat,
    timeZone,
    defaultComponentUrl
  } = response.data;

  cancel(forceResolve);
  resolve();
}).catch((error) => {
  Logger.error('Error loading preferences', error);
});
```

## Setting a new preference
The user preferences is split between two service calls in component lib (component-lib/addon/components/rsa-application-user-preferences.js)
and protected route in sa (sa/app/protected/route.js)

* Adding a new service in rsa-application-user-preferences component

```javascript
export default Component.extend({
  ..
  ..
  xyzService: service('xyzService'),
  ..
  ..
});
```
* Map that new ember service to the user preferences

```javascript
saveUserPreferences() {
    ..
    ..
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          ..
        }
      }
    }).then(() => {
      localStorage.setItem('..', ..);
    }).catch(() => {
      Logger.error('Error updating preferences');
    });
  }
  ..
  ..
  if (this.get('pendingXYZService')) {
    this.set('xyzService.options', this.get('pendingXYZServiceOptions'));
  }
  ..
  ..
}
```
Also see [Common UI User Preferences](https://github.rsa.lab.emc.com/asoc/common-ui/blob/master/modules/admin/docs/user-preferences.md)

