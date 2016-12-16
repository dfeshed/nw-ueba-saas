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

  timezone: service(),

  timeFormat: service(),

  dateFormat: service(),

  outputFormat: computed('timeFormat.selected.format', 'dateFormat.selected.format', function() {
    if (this.get('displayDate') && this.get('displayTime')) {
      return `${this.get('dateFormat.selected.format')} ${this.get('timeFormat.selected.format')}`;
    } else {
      if (this.get('displayDate')) {
        return this.get('dateFormat.selected.format');
      } else if (this.get('displayTime')) {
        return this.get('timeFormat.selected.format');
      }
    }
  })

});
