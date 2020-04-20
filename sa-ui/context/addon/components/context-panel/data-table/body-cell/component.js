import layout from './template';
import { get, computed } from '@ember/object';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import moment from 'moment';

export default Component.extend({
  layout,
  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),

  dateTime: computed('item', 'column', function() {
    const selectedDateFormat = this.get('dateFormat.selected.format');
    const selectedTimeFormat = this.get('timeFormat.selected.format');
    const timeZoneId = this.get('timezone.selected.zoneId') || 'UTC';
    const dateTimeFormat = selectedDateFormat ? `${selectedDateFormat} ${selectedTimeFormat}` : 'YYYY/MM/DD HH:mm:ss';
    const dateTimeString =
      moment(get(this.item, this.column.field))
        .locale(this.get('i18n.primaryLocale') || 'en')
        .tz(timeZoneId).format(dateTimeFormat.replace(/.SSS/, ''));
    const timeAgo = moment(get(this.item, this.column.field)).locale(this.get('i18n.primaryLocale') || 'en').fromNow();
    return `${dateTimeString} (${timeAgo})`;
  }),

  link: computed('item', 'column', function() {
    return window.location.origin.concat(this.column.path.replace('{0}', get(this.item, this.column.linkField || this.column.field)));
  })
});
