import Ember from 'ember';

const {
  Service,
  inject: {
    service
  },
  computed,
  Logger
} = Ember;

export default Service.extend({

  request: service(),

  moment: service(),

  options: [{
    key: 'MM/DD/YYYY',
    label: 'userPreferences.dateFormat.monthFirst'
  }, {
    key: 'DD/MM/YYYY',
    label: 'userPreferences.dateFormat.dayFirst'
  }, {
    key: 'YYYY/MM/DD',
    label: 'userPreferences.dateFormat.yearFirst'
  }],

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        dateFormat: value
      }
    }).catch(() => {
      Logger.error('Error updating preferences');
    });
  },

  selected: computed({
    get() {
      return this.get('_selected');
    },

    set(key, value) {
      if (value.key) {
        this.set('_selected', value);
        this.persist(value.key);
        return value;
      } else {
        this.set('_selected', this.get('options').findBy('key', value));
        this.persist(value);
        return this.get('_selected');
      }
    }
  })

});
