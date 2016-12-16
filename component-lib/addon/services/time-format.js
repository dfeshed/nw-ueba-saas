import Ember from 'ember';

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

  selected: computed({
    get() {
      return this.get('_selected');
    },

    set(key, value) {
      const option = this.get('options').findBy('key', value);

      if (!isNone(this.get('_selected'))) {
        this.get('request').promiseRequest({
          method: 'setPreference',
          modelName: 'preferences',
          query: {
            data: {
              timeFormat: value
            }
          }
        }).then(() => {
          this.set('_selected', option);
        }).catch(() => {
          Logger.error('Error updating preferences');
        });
      } else {
        this.set('_selected', option);
      }

      return option;
    }
  })

});
