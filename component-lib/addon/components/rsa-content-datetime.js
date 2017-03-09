import Ember from 'ember';
import layout from '../templates/components/rsa-content-datetime';
import computed from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
  }
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

  displaySeconds: true,

  displayMilliseconds: true,

  timezone: service(),

  timeFormat: service(),

  dateFormat: service(),

  @computed('timeFormat.selected.format', 'displayTime', 'displaySeconds', 'displayMilliseconds')
  adjustedTimeFormat: (format, displayTime, displaySeconds, displayMilliseconds) => {
    if (displayTime && displaySeconds && displayMilliseconds) {
      return format;
    } else {
      return displayTime ? (displaySeconds ? format.replace(/.SSS/, '') : format.replace(/:ss.SSS/, '')) : '';
    }
  },

  @computed('dateFormat.selected.format', 'displayDate')
  adjustedDateFormat: (format, displayDate) => {
    return displayDate ? format : '';
  },


  @computed('adjustedTimeFormat', 'adjustedDateFormat')
  outputFormat: (timeFormat, dateFormat) => {
    return `${dateFormat} ${timeFormat}`;
  }

});
