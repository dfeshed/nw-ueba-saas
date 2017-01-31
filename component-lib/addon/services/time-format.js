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

  options: [{
    key: 'HR12',
    label: 'userPreferences.timeFormat.twelveHour',
    format: 'hh:mma'
  }, {
    key: 'HR24',
    label: 'userPreferences.timeFormat.twentyFourHour',
    format: 'HH:mm'
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
      Logger.error('Error updating preferences');
    });
  },

  selected: computed({
    get() {
      return this.get('_selected') || this.get('options').findBy('key', config.timeFormatDefault);
    },

    set(key, value) {
      if (value.key) {
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
