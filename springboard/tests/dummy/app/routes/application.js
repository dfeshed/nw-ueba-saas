import Route from '@ember/routing/route';
import { getOwner } from '@ember/application';
import { inject as service } from '@ember/service';
import config from 'dummy/config/environment';

export default class ApplicationRoute extends Route {
  @service('date-format') dateFormat;
  @service('time-format') timeFormat;
  @service('timezone') timezone;

  model() {

    // When running in sa, these are set as part of protected route,
    // just setting defaults here so preferences exist
    this.timezone.options = [{
      'displayLabel': 'UTC (GMT+00:00)',
      'offset': 'GMT+00:00',
      'zoneId': 'UTC'
    }];
    this.dateFormat.selected = 'MM/dd/yyyy';
    this.timeFormat.selected = 'HR24';
    this.timezone.selected = 'UTC';

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
}
