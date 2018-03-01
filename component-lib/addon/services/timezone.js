import { computed } from '@ember/object';
import Service, { inject as service } from '@ember/service';
import config from 'ember-get-config';
import { isNone, isEmpty } from 'ember-utils';

export default Service.extend({

  request: service(),

  options: config.timezones || [],

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          timeZone: value
        }
      }
    }).catch(() => {
      this.get('flashMessages').error(this.get('i18n').t('userPreferences.timezoneError'));
    });
  },

  selected: computed({
    get() {
      isEmpty(this.get('_selected')) ? this.get('options').findBy('zoneId', config.timezoneDefault) : this.get('_selected');
    },

    set(key, value) {
      if (value && value.zoneId) {
        if (!isNone(this.get('_selected'))) {
          this.persist(value.zoneId);
        }
        this.set('_selected', value);
        return value;
      } else {
        if (!isNone(this.get('_selected'))) {
          this.persist(value);
        }
        this.set('_selected', this.get('options').findBy('zoneId', value));
        return this.get('_selected');
      }
    }
  })

});
