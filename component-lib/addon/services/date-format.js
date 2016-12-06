import Ember from 'ember';

const {
  Service,
  inject: {
    service
  },
  isNone,
  computed,
  Logger
} = Ember;

export default Service.extend({

  request: service(),

  moment: service(),

  options: [{
    key: 'MM/dd/yyyy',
    label: 'userPreferences.dateFormat.monthFirst',
    format: 'MM/DD/YYYY'
  }, {
    key: 'dd/MM/yyyy',
    label: 'userPreferences.dateFormat.dayFirst',
    format: 'DD/MM/YYYY'
  }, {
    key: 'yyyy/MM/dd',
    label: 'userPreferences.dateFormat.yearFirst',
    format: 'YYYY/MM/DD'
  }],

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          dateFormat: value
        }
      }
    }).catch(() => {
      Logger.error('Error updating preferences');
    });
  },

  selected: computed({
    get() {
      if (isNone(this.get('_selected'))) {
        this.set('_selected', this.get('options').findBy('key', 'MM/dd/yyyy'));
      }
      return this.get('_selected');
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
