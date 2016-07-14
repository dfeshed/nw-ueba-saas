import Ember from 'ember';
import layout from '../templates/components/rsa-application-user-preferences';

const {
  getOwner,
  Component,
  inject: {
    service
  },
  computed,
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

  hasPasswordError: computed('password', 'passwordConfirm', function() {
    return !isEmpty(this.get('password')) && !isEmpty(this.get('passwordConfirm')) && (this.get('password') !== this.get('passwordConfirm'));
  }),

  locales: computed('i18n.locales', function() {
    if (this.get('i18n.locales')) {
      return this.get('i18n.locales').uniq();
    } else {
      return [];
    }
  }),

  selectedFriendlyName: computed('usernameFormat.friendlyUsername', {
    get() {
      return this.get('usernameFormat.friendlyUsername');
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingFriendlyName', value);
      return value;
    }
  }),

  selectedLocale: computed('i18n.locale', {
    get() {
      return [this.get('i18n.locale')];
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingLocale', value);
      return value;
    }
  }),

  selectedTimeFormat: computed('timeFormat.selected', {
    get() {
      return this.get('timeFormat.selected');
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingTimeFormat', value);
      return value;
    }
  }),

  selectedTheme: computed('theme.selected', {
    get() {
      return this.get('theme.selected');
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingTheme', value);
      return value;
    }
  }),

  selectedSpacing: computed('spacing.selected', {
    get() {
      return this.get('spacing.selected');
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingSpacing', value);
      return value;
    }
  }),

  selectedLandingPage: computed('landingPage.selected.key', {
    get() {
      return [this.get('landingPage.selected.key')];
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingLandingPage', value.get('firstObject'));
      return value;
    }
  }),

  selectedTimeZone: computed('timezone.selected', {
    get() {
      return [this.get('timezone.selected')];
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingTimezone', value);
      return value;
    }
  }),

  selectedDateFormat: computed('dateFormat.selected', {
    get() {
      return [this.get('dateFormat.selected.key')];
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingDateFormat', value.get('firstObject'));
      return value;
    }
  }),

  selectedNotifications: computed('notifications.enabled', {
    get() {
      return this.get('notifications.enabled');
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingNotifications', value);
      return value;
    }
  }),

  selectedContextMenus: computed('contextMenus.enabled', {
    get() {
      return this.get('contextMenus.enabled');
    },

    set(key, value) {
      this.set('withoutChanges', false);
      this.set('pendingContextMenus', value);
      return value;
    }
  }),

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
