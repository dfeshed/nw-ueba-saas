import $ from 'jquery';
import Route from 'ember-route';
import service from 'ember-service/inject';

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
    clearFatalErrorQueue() {
      this.get('fatalErrors').clearQueue();
    },
    scrollTo(selector, offset = -185) {
      $('.spec-container.scroll-box').animate({
        scrollTop: $(selector).offset().top + offset
      }, 600);
    }
  }
});
