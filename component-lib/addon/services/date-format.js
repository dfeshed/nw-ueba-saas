import Ember from 'ember';

const {
  Service,
  inject: {
    service
  },
  isNone,
  computed
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
      this.get('flashMessages').error(this.get('i18n').t('userPreferences.dateFormatError'));
    });
  },

  selected: computed({
    get() {
      return this.get('_selected');
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
