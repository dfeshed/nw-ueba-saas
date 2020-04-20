import Ember from 'ember';
import config from 'dummy/config/environment';

const {
  Route,
  getOwner,
  inject: {
    service
  }
} = Ember;

export default Route.extend({

  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),
  features: service(),
  session: service(),

  model() {

    // When running in sa, these are set as part of protected route,
    // just setting defaults here so preferences exist
    this.setProperties({
      'timezone.options': [{
        'displayLabel': 'UTC (GMT+00:00)',
        'offset': 'GMT+00:00',
        'zoneId': 'UTC'
      }],
      'dateFormat.selected': 'MM/dd/yyyy',
      'timeFormat.selected': 'HR24',
      'timezone.selected': 'UTC'
    });

    // also set as part of protected route in sa
    this.get('features').setFeatureFlags({
      'rsa.usm.viewSourcesFeature': false,
      'rsa.usm.filePolicyFeature': true,
      'rsa.usm.allowFilePolicies': true
    });

    this.set('session.isNwUIPrimary', true);

    // When running microservices, need to login and get cookie
    // so requests do not fail.
    //
    // However we do not want to force a login if we are running
    // local mocks (local node server)
    if (!config.mock) {
      const applicationInstance = getOwner(this);
      const auth = applicationInstance.lookup('authenticator:oauth-authenticator');

      // model hook returning promise, then ensures
      // log in occurs before engine loads
      return auth.authenticate('local', 'changeMe');
    }
  }
});
