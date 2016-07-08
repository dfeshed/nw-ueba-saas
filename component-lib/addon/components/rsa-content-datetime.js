import Ember from 'ember';
import layout from '../templates/components/rsa-content-datetime';

export default Ember.Component.extend({

  layout,

  tagName: 'span',

  classNames: ['rsa-content-datetime'],

  timestamp: null,

  asTimeAgo: false,

  displayDate: true,

  displayTime: true,

  timezone: Ember.inject.service('timezone'),

  timeFormat: Ember.inject.service('time-format'),

  dateFormat: Ember.inject.service('date-format'),

  outputFormat: Ember.computed('timeFormat.selected.format', 'dateFormat.selected.key', function() {
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
