import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import moment from 'moment';

export default Component.extend({
  dateFormat: service(),
  timezone: service(),

  @computed('timestamp')
  alertDate(timestamp) {
    const selectedDateFormat = this.get('dateFormat.selected.format');
    const timeZoneId = this.get('timezone.selected.zoneId') || 'UTC';
    const dateTimeFormat = selectedDateFormat ? selectedDateFormat : 'YYYY/MM/DD';
    const dateTimeString = moment(parseInt(timestamp, 10))
      .locale(this.get('i18n.locale') || 'en')
      .tz(timeZoneId).format(dateTimeFormat);
    return dateTimeString;
  }
});
