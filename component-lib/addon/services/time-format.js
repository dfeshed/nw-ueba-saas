import Ember from 'ember';

const {
  Service,
  computed,
  inject: {
    service
  },
  Logger
} = Ember;

export default Service.extend({

  request: service(),

  options: [{
    key: '12hr',
    label: 'userPreferences.timeFormat.twelveHour',
    format: 'hh:mma'
  }, {
    key: '24hr',
    label: 'userPreferences.timeFormat.twentyFourHour',
    format: 'HH:mm'
  }],

  selected: computed({
    get() {
      return this.get('_selected');
    },

    set(key, value) {
      const option = this.get('options').findBy('key', value);

      this.get('request').promiseRequest({
        method: 'setPreference',
        modelName: 'preferences',
        query: {
          timeFormat: value
        }
      }).then(() => {
        this.set('_selected', option);
      }).catch(() => {
        Logger.error('Error updating preferences');
      });

      return option;
    }
  })

});
