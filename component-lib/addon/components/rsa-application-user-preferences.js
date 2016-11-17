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
  isNone
 } = Ember;

export default Component.extend({

  layout,

  classNames: ['user-preferences-panel'],

  tagName: 'section',

  eventBus: service(),

  moment: service(),

  timezone: service('timezone'),

  notifications: service('notifications'),

  contextMenus: service('context-menus'),

  timeFormat: service('time-format'),

  usernameFormat: service('username-format'),

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

  @computed('i18n.locales')
  locales(locales) {
    if (locales) {
      return locales.uniq();
    } else {
      return [];
    }
  },

  @computed('usernameFormat.friendlyUsername')
  selectedFriendlyName: {
    get: (userName) => userName,

    set(userName) {
      this.set('withoutChanges', false);
      this.set('pendingFriendlyName', userName);
      return userName;
    }
  },

  @computed('timeFormat.selected')
  selectedTimeFormat: {
    get: (selectedTimeformat) => selectedTimeformat,

    set(selectedTimeFormat) {
      this.set('withoutChanges', false);
      this.set('pendingTimeFormat', selectedTimeFormat);
      return selectedTimeFormat;
    }
  },

  @computed('notifications.enabled')
  selectedNotifications: {
    get: (selectedNotifications) => selectedNotifications,

    set(selectedNotifications) {
      this.set('withoutChanges', false);
      this.set('pendingNotifications', selectedNotifications);
      return selectedNotifications;
    }
  },

  @computed('contextMenus.enabled')
  selectedContextMenus: {
    get: (selectedContextMenus) => selectedContextMenus,

    set(selectedContextMenus) {
      this.set('withoutChanges', false);
      this.set('pendingContextMenus', selectedContextMenus);
      return selectedContextMenus;
    }
  },

  saveUserPreferences() {
    if (this.get('pendingLocale')) {
      this.set('i18n.locale', this.get('pendingLocale'));
      localStorage.setItem('rsa-i18n-default-locale', this.get('pendingLocale'));
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

    if (!isNone(this.get('pendingNotifications'))) {
      this.set('notifications.enabled', this.get('pendingNotifications'));
    }

    if (!isNone(this.get('pendingContextMenus'))) {
      this.set('contextMenus.enabled', this.get('pendingContextMenus'));
    }

    if (this.get('pendingFriendlyName') || this.get('pendingFriendlyName') === '') {
      this.set('usernameFormat.username', this.get('pendingFriendlyName'));
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


    this.set('selectedNotifications', this.get('notifications.enabled'));
    this.set('pendingNotifications', null);

    this.set('selectedContextMenus', this.get('contextMenus.enabled'));
    this.set('pendingContextMenus', null);

    this.set('selectedFriendlyName', this.get('usernameFormat.friendlyUsername'));
    this.set('pendingFriendlyName', null);

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
