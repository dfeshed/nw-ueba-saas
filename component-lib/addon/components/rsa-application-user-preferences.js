import Ember from 'ember';
import computed from 'ember-computed-decorators';
import layout from '../templates/components/rsa-application-user-preferences';

const {
  getOwner,
  Component,
  inject: {
    service
  },
  isEmpty,
  run,
  isNone
 } = Ember;

export default Component.extend({

  layout,

  classNames: ['user-preferences-panel'],

  tagName: 'section',

  eventBus: service('event-bus'),

  moment: service(),

  timezone: service('timezone'),

  notifications: service('notifications'),

  contextMenus: service('context-menus'),

  spacing: service('spacing'),

  timeFormat: service('time-format'),

  usernameFormat: service('username-format'),

  landingPage: service('landing-page'),

  dateFormat: service('date-format'),

  password: null,

  passwordConfirm: null,

  withoutChanges: true,

  didInsertElement() {
    run.schedule('afterRender', this, function() {
      if (this.get('i18n.locale')) {
        if (localStorage.getItem('rsa-i18n-default-locale')) {
          this.set('i18n.locale', localStorage.getItem('rsa-i18n-default-locale'));
        } else {
          let config = getOwner(this).resolveRegistration('config:environment');

          this.set('i18n.locale', config.i18n.defaultLocale);
          localStorage.setItem('rsa-i18n-default-locale', config.i18n.defaultLocale);
        }
      }
    });
  },

  @computed('password', 'passwordConfirm')
  hasPasswordError(password, passwordConfirm) {
    return !isEmpty(password) && !isEmpty(passwordConfirm) && (password !== passwordConfirm);
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

  @computed('i18n.locale')
  selectedLocale: {
    get: (locale) => [locale],

    set(locales) {
      this.set('withoutChanges', false);
      this.set('pendingLocale', locales);
      return locales;
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

  @computed('theme.selected')
  selectedTheme: {
    get: (selectedTheme) => selectedTheme,

    set(selectedTheme) {
      this.set('withoutChanges', false);
      this.set('pendingTheme', selectedTheme);
      return selectedTheme;
    }
  },

  @computed('spacing.selected')
  selectedSpacing: {
    get: (selectedSpacing) => selectedSpacing,

    set(selectedSpacing) {
      this.set('withoutChanges', false);
      this.set('pendingSpacing', selectedSpacing);
      return selectedSpacing;
    }
  },

  @computed('landingPage.selected.key')
  selectedLandingPage: {
    get: (selectedLandingPage) => [selectedLandingPage],

    set(selectedLandingPages) {
      this.set('withoutChanges', false);
      this.set('pendingLandingPage', selectedLandingPages.get('firstObject'));
      return selectedLandingPages;
    }
  },

  @computed('timezone.selected')
  selectedTimeZone: {
    get: (selectedTimeZone) => [selectedTimeZone],

    set(selectedTimeZones) {
      this.set('withoutChanges', false);
      this.set('pendingTimezone', selectedTimeZones);
      return selectedTimeZones;
    }
  },

  @computed('dateFormat.selected')
  selectedDateFormat: {
    get: (selectedDateFormat) => [selectedDateFormat.key],

    set(selectedDateFormats) {
      this.set('withoutChanges', false);
      this.set('pendingDateFormat', selectedDateFormats.get('firstObject'));
      return selectedDateFormats;
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
    this.set('password', null);
    this.set('passwordConfirm', null);

    if (this.get('pendingLocale')) {
      this.set('i18n.locale', this.get('pendingLocale'));
      localStorage.setItem('rsa-i18n-default-locale', this.get('pendingLocale'));
    }

    if (this.get('pendingTimezone')) {
      this.set('timezone.selected', this.get('pendingTimezone.firstObject'));
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

    if (this.get('pendingTheme')) {
      this.set('theme.selected', this.get('pendingTheme.key'));
    }

    if (this.get('pendingSpacing')) {
      this.set('spacing.selected', this.get('pendingSpacing.key'));
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
    this.set('password', null);
    this.set('passwordConfirm', null);

    this.set('selectedLocale', [this.get('i18n.locale')]);
    this.set('pendingLocale', null);

    this.set('selectedTimeZone', [this.get('timezone.selected')]);
    this.set('pendingTimeZone', null);

    this.set('selectedDateFormat', [this.get('dateFormat.selected.key')]);
    this.set('pendingDateFormat', null);

    this.set('selectedTimeFormat', this.get('timeFormat.selected'));
    this.set('pendingTimeFormat', null);

    this.set('selectedTheme', this.get('theme.selected'));
    this.set('pendingTheme', null);

    this.set('selectedSpacing', this.get('spacing.selected'));
    this.set('pendingSpacing', null);

    this.set('selectedLandingPage', [this.get('landingPage.selected.key')]);
    this.set('pendingLandingPage', null);

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
    saveUserPreferences() {
      this.saveUserPreferences();
    },

    revertUserPreferences() {
      this.revertUserPreferences();
    }
  }

});
