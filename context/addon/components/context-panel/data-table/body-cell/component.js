import layout from './template';
import computed from 'ember-computed-decorators';
import { get } from '@ember/object';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import moment from 'moment';

export default Component.extend({
  layout,
  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),

  @computed('item', 'column')
  dateTime(item, column) {
    const selectedDateFormat = this.get('dateFormat.selected.format');
    const selectedTimeFormat = this.get('timeFormat.selected.format');
    const timeZoneId = this.get('timezone.selected.zoneId') || 'UTC';
    const dateTimeFormat = selectedDateFormat ? `${selectedDateFormat} ${selectedTimeFormat}` : 'YYYY/MM/DD HH:mm:ss';
    const dateTimeString = moment(get(item, column.field))
                          .locale(this.get('i18n.locale') || 'en')
                          .tz(timeZoneId).format(dateTimeFormat.replace(/.SSS/, ''));
    const timeAgo = moment(get(item, column.field)).locale(this.get('i18n.locale') || 'en').fromNow();
    return `${dateTimeString} (${timeAgo})`;
  },

  @computed('item', 'column')
  link(item, column) {
    return window.location.origin.concat(column.path.replace('{0}', get(item, column.linkField || column.field)));
  }
});
