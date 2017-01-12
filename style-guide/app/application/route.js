import Ember from 'ember';
import RsaApplicationRoute from 'component-lib/routes/application';

const {
  inject: {
    service
  }
} = Ember;

export default RsaApplicationRoute.extend({
  fatalErrors: service(),

  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),

  activate() {
    this.set('timezone.options', [{
      'displayLabel': 'UTC (GMT+00:00)',
      'offset': 'GMT+00:00',
      'zoneId': 'UTC'
    }]);

    this.setProperties({
      'dateFormat.selected': 'MM/dd/yyyy',
      'timeFormat.selected': 'HR24',
      'timezone.selected': 'UTC'
    });
  },

  actions: {
    clearFatalErrorQueue() {
      this.get('fatalErrors').clearQueue();
    }
  }
});
