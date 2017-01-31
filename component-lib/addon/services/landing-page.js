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

  accessControl: service(),
  request: service(),

  options: computed(function() {
    return [
      {
        key: '/do/respond',
        label: 'userPreferences.defaultLandingPage.respond'
      }, {
        key: '/do/investigate',
        label: 'userPreferences.defaultLandingPage.investigate'
      }, {
        key: '/investigation',
        label: 'userPreferences.defaultLandingPage.investigateClassic'
      }, {
        key: '/unified',
        label: 'userPreferences.defaultLandingPage.dashboard'
      }, {
        key: this.get('accessControl.configUrl'),
        label: 'userPreferences.defaultLandingPage.live'
      }, {
        key: this.get('accessControl.adminUrl'),
        label: 'userPreferences.defaultLandingPage.admin'
      }
    ];
  }),

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          defaultComponentUrl: value
        }
      }
    }).catch(() => {
      Logger.error('Error updating preferences');
    });
  },

  selected: computed({
    get() {
      return this.get('_selected') || this.get('options').findBy('key', config.landingPageDefault);
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
