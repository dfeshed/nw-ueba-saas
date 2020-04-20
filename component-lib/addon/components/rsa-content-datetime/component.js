import Component from '@ember/component';
import { inject as service } from '@ember/service';
import moment from 'moment';
import layout from './template';
import computed from 'ember-computed-decorators';

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

  i18n: service(),

  @computed('timeFormat.selected.format', 'displayTime', 'displaySeconds', 'displayMilliseconds')
  adjustedTimeFormat: (format, displayTime, displaySeconds, displayMilliseconds) => {
    if (format && displayTime) {
      if (displaySeconds) {
        if (displayMilliseconds) {
          return format;
        }

        return format.replace(/.SSS/, '');
      }

      return format.replace(/:ss.SSS/, '');
    } else {
      return '';
    }
  },

  @computed('dateFormat.selected.format', 'displayDate')
  adjustedDateFormat: (format, displayDate) => {
    return displayDate ? format : '';
  },


  @computed('adjustedTimeFormat', 'adjustedDateFormat')
  outputFormat: (timeFormat, dateFormat) => {
    return `${dateFormat} ${timeFormat}`;
  },

  @computed('timestamp', 'i18n.locale', 'timezone.selected.zoneId')
  timeAgo: (timestamp, locale, timeZone) => {
    return moment.apply(moment, [timestamp], { locale: locale[0], timeZone }).fromNow();
  }

});
