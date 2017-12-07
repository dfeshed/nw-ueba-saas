import Route from 'ember-route';
import getOwner from 'ember-owner/get';
import service from 'ember-service/inject';
import config from 'dummy/config/environment';

export default Route.extend({
  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),

  model() {
    // When running in sa, these are set as part of protected route,
    // just setting defaults here so preferences exist
    this.setProperties({
      'timezone.options': [{
        'displayLabel': 'UTC (GMT+00:00)',
        'offset': 'GMT+00:00',
        'zoneId': 'UTC'
      }],
      'i18n.locale': 'en-us',
      'dateFormat.selected': 'MM/dd/yyyy',
      'timeFormat.selected': 'HR24',
      'timezone.selected': 'UTC'
    });

    // When running microservices, need to login and get cookie
    // so requests do not fail.
    //
    // However we do not want to force a login if we are running
    // local mocks (local node server)
    if (!config.mock) {
      const applicationInstance = getOwner(this);
      const auth = applicationInstance.lookup('authenticator:oauth-authenticator');
      return auth.authenticate('local', 'changeMe');
    }
  }
});
