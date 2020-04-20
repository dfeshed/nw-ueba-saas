import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({
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
    scrollTo(selector, offset = 0) {
      document.querySelector('.style-guide-content .scroll-box').scroll({
        top: document.querySelector(selector).offsetTop + offset,
        behavior: 'smooth'
      });
    }
  }


});
