import { observer } from '@ember/object';
import Service, { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import Evented from '@ember/object/evented';

export default Service.extend(Evented, {
  eventBus: service(),
  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),
  i18n: service(),

  @computed(
    'timeFormat.selected.format',
    'dateFormat.selected.format',
    'timezone.selected.zoneId',
    'i18n.locale'
  )
  preferences(timeFormat, dateFormat, timezone, locale) {
    return {
      timeFormat,
      dateFormat,
      timezone,
      locale
    };
  },

  preferencesDidChange: observer(
    'timeFormat.selected.format',
    'dateFormat.selected.format',
    'timezone.selected.zoneId',
    'i18n.locale',
    function() {
      this.trigger('rsa-application-user-preferences-did-change', this.preferences);
    }
  ).on('init')

});
