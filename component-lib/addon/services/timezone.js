import Ember from 'ember';
import config from 'ember-get-config';

const {
  Service,
  computed,
  inject: {
    service
  },
  isNone,
  Logger
} = Ember;

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
      Logger.error('Error updating preferences');
    });
  },

  selected: computed({
    get() {
      return this.get('_selected') || this.get('options').findBy('zoneId', config.timezoneDefault);
    },

    set(key, value) {
      if (value.zoneId) {
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
