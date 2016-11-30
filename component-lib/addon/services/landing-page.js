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
    key: '/unified',
    label: 'userPreferences.defaultLandingPage.dashboard'
  }, {
    key: 'protected.respond',
    label: 'userPreferences.defaultLandingPage.respond'
  }, {
    key: 'protected.investigate',
    label: 'userPreferences.defaultLandingPage.investigate'
  }, {
    key: '/investigate',
    label: 'userPreferences.defaultLandingPage.investigateClassic'
  }, {
    key: '/live',
    label: 'userPreferences.defaultLandingPage.live'
  }, {
    key: '/admin',
    label: 'userPreferences.defaultLandingPage.admin'
  }],

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        defaultComponentUrl: value
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
