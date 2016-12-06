import Ember from 'ember';
import computed from 'ember-computed-decorators';
import layout from '../templates/components/rsa-application-user-preferences';

const {
  getOwner,
  Component,
  inject: {
    service
  },
  run,
  Logger
 } = Ember;

export default Component.extend({

  layout,

  classNames: ['user-preferences-panel'],

  tagName: 'section',

  request: service(),

  eventBus: service(),

  moment: service(),

  timezone: service('timezone'),

  timeFormat: service('time-format'),

  landingPage: service('landing-page'),

  dateFormat: service('date-format'),

  withoutChanges: true,

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
      this.set('withoutChanges', false);
      this.set('pendingTimeFormat', selectedTimeFormat);
      return selectedTimeFormat;
    }
  },

  saveUserPreferences() {
    if (this.get('pendingLocale')) {
      const pendingLocale = this.get('pendingLocale');

      this.set('i18n.locale', pendingLocale);
      this.get('request').promiseRequest({
        method: 'setPreference',
        modelName: 'preferences',
        query: {
          data: {
            userLocale: pendingLocale.replace(/en-us/, 'en_US')
          }
        }
      }).then(() => {
        localStorage.setItem('rsa-i18n-default-locale', pendingLocale);
      }).catch(() => {
        Logger.error('Error updating preferences');
      });
    }

    if (this.get('pendingTimezone')) {
      this.set('timezone.selected', this.get('pendingTimezone'));
    }

    if (this.get('pendingDateFormat')) {
      this.set('dateFormat.selected', this.get('pendingDateFormat'));
    }

    if (this.get('pendingLandingPage')) {
      this.set('landingPage.selected', this.get('pendingLandingPage'));
    }

    if (this.get('pendingTimeFormat')) {
      this.set('timeFormat.selected', this.get('pendingTimeFormat.key'));
    }

    this.get('eventBus').trigger('rsa-application-modal-close-all');

    run.next(this, function() {
      this.set('withoutChanges', true);
    });
  },

  revertUserPreferences() {
    this.set('pendingLocale', null);
    this.set('pendingLandingPage', null);
    this.set('pendingTimeZone', null);
    this.set('pendingDateFormat', null);

    this.set('selectedTimeFormat', this.get('timeFormat.selected'));
    this.set('pendingTimeFormat', null);

    this.get('eventBus').trigger('rsa-application-modal-close-all');

    run.next(this, function() {
      this.set('withoutChanges', true);
    });
  },

  actions: {
    setDefaultLandingPage(selection) {
      this.set('pendingLandingPage', selection);
      this.set('withoutChanges', false);
    },

    setLocale(selection) {
      this.set('pendingLocale', selection);
      this.set('withoutChanges', false);
    },

    setTimezone(selection) {
      this.set('pendingTimezone', selection);
      this.set('withoutChanges', false);
    },

    setDateFormat(selection) {
      this.set('pendingDateFormat', selection);
      this.set('withoutChanges', false);
    },

    saveUserPreferences() {
      this.saveUserPreferences();
    },

    revertUserPreferences() {
      this.revertUserPreferences();
    }
  }

});
