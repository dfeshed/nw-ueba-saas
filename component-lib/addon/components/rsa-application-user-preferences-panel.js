import Ember from 'ember';
import computed from 'ember-computed-decorators';
import layout from '../templates/components/rsa-application-user-preferences-panel';

const {
  getOwner,
  Component,
  inject: {
    service
  },
  RSVP,
  run,
  Logger,
  $
 } = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-application-user-preferences-panel'],

  classNameBindings: ['isExpanded'],

  isExpanded: false,

  layoutService: service('layout'),

  request: service(),

  eventBus: service(),

  moment: service(),

  timezone: service('timezone'),

  timeFormat: service('time-format'),

  landingPage: service('landing-page'),

  dateFormat: service('date-format'),

  init() {
    this._super(arguments);

    this.get('eventBus').on('rsa-application-user-preferences-panel-will-toggle', () => {
      this.toggleProperty('isExpanded');
    });
  },

  didInsertElement() {
    run.schedule('afterRender', this, function() {
      if (this.get('i18n.locale')) {
        if (localStorage.getItem('rsa-i18n-default-locale')) {
          this.set('i18n.locale', localStorage.getItem('rsa-i18n-default-locale'));
        } else {
          const config = getOwner(this).resolveRegistration('config:environment');

          this.set('i18n.locale', config.i18n.defaultLocale);
          localStorage.setItem('rsa-i18n-default-locale', config.i18n.defaultLocale);
        }
      }
    });
  },

  locales: ['en-us', 'ja'],

  @computed('timeFormat.selected')
  selectedTimeFormat: {
    get: (selectedTimeformat) => selectedTimeformat,

    set(selectedTimeFormat) {
      this.set('timeFormat.selected', selectedTimeFormat.key);
      return selectedTimeFormat;
    }
  },

  actions: {
    setLocale(selection) {
      this.set('i18n.locale', selection);
      this.get('request').promiseRequest({
        method: 'setPreference',
        modelName: 'preferences',
        query: {
          data: {
            userLocale: selection.replace(/en-us/, 'en_US')
          }
        }
      }).then(() => {
        localStorage.setItem('rsa-i18n-default-locale', selection);
      }).catch(() => {
        Logger.error('Error updating locale');
      });
    },

    setDefaultLandingPage(selection) {
      this.set('landingPage.selected', selection);
    },

    setTimezone(selection) {
      this.set('timezone.selected', selection);
    },

    setDateFormat(selection) {
      this.set('dateFormat.selected', selection);
    },

    toggleUserPreferences() {
      this.get('layoutService').toggleUserPreferences();
    },

    logout() {
      return new RSVP.Promise((resolve) => {
        $.ajax({
          type: 'POST',
          url: '/oauth/logout',
          timeout: 3000,
          data: {
            access_token: this.get('session').get('data.authenticated.access_token')
          }
        })
          .always(() => {
            this.get('session').invalidate();
            resolve();
          });
      });
    }
  }
});
