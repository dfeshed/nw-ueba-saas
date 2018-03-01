import { computed } from '@ember/object';
import Service, { inject as service } from '@ember/service';
import config from 'ember-get-config';
import { isNone, isEmpty } from '@ember/utils';

export default Service.extend({

  request: service(),

  options: [{
    key: 'HR12',
    label: 'userPreferences.timeFormat.twelveHour',
    format: 'hh:mm:ss.SSS a'
  }, {
    key: 'HR24',
    label: 'userPreferences.timeFormat.twentyFourHour',
    format: 'HH:mm:ss.SSS'
  }],

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          timeFormat: value
        }
      }
    }).catch(() => {
      this.get('flashMessages').error(this.get('i18n').t('userPreferences.timeFormatError'));
    });
  },

  selected: computed({
    get() {
      isEmpty(this.get('_selected')) ? this.get('options').findBy('key', config.timeFormatDefault) : this.get('_selected');
    },

    set(key, value) {
      if (value && value.key) {
        if (!isNone(this.get('_selected'))) {
          this.persist(value.key);
        }
        this.set('_selected', value);
        return value;
      } else {
        if (!isNone(this.get('_selected'))) {
          this.persist(value);
        }
        this.set('_selected', this.get('options').findBy('key', value));
        return this.get('_selected');
      }
    }
  })

});
