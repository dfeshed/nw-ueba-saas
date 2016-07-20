import Ember from 'ember';
import layout from '../templates/components/rsa-content-datetime';

const {
  Component,
  inject: {
    service
  },
  computed
} = Ember;

export default Component.extend({

  layout,

  tagName: 'span',

  classNames: ['rsa-content-datetime'],

  timestamp: null,

  asTimeAgo: false,

  withTimeAgo: false,

  displayDate: true,

  displayTime: true,

  timezone: service('timezone'),

  timeFormat: service('time-format'),

  dateFormat: service('date-format'),

  outputFormat: computed('timeFormat.selected.format', 'dateFormat.selected.key', function() {
    if (this.get('displayDate') && this.get('displayTime')) {
      return `${this.get('dateFormat.selected.key')} ${this.get('timeFormat.selected.format')}`;
    } else {
      if (this.get('displayDate')) {
        return this.get('dateFormat.selected.key');
      } else if (this.get('displayTime')) {
        return this.get('timeFormat.selected.format');
      }
    }
  })

});
