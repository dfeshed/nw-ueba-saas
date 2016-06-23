import Ember from 'ember';
import layout from '../templates/components/rsa-application-user-preferences';
const { getOwner } = Ember;

export default Ember.Component.extend({

  layout,

  classNames: ['user-preferences-panel'],

  tagName: 'section',

  eventBus: Ember.inject.service('event-bus'),

  moment: Ember.inject.service(),

  timezone: Ember.inject.service('timezone'),

  notifications: Ember.inject.service('notifications'),

  contextMenus: Ember.inject.service('context-menus'),

  spacing: Ember.inject.service('spacing'),

  timeFormat: Ember.inject.service('time-format'),

  usernameFormat: Ember.inject.service('username-format'),

  landingPage: Ember.inject.service('landing-page'),

  dateFormat: Ember.inject.service('date-format'),

  password: null,

  passwordConfirm: null,

  withoutChanges: true,

  didInsertElement() {
    Ember.run.schedule('afterRender', this, function() {
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

  hasPasswordError: Ember.computed('password', 'passwordConfirm', function() {
    return !Ember.isEmpty(this.get('password')) && !Ember.isEmpty(this.get('passwordConfirm')) && (this.get('password') !== this.get('passwordConfirm'));
  }),

  locales: Ember.computed('i18n.locales', function() {
    if (this.get('i18n.locales')) {
      return this.get('i18n.locales').uniq();
    } else {
      return [];
    }
  }),

  selectedFriendlyName: Ember.computed('usernameFormat.friendlyUsername', function() {
    if (this.get('usernameFormat.friendlyUsername')) {
      return this.get('usernameFormat.friendlyUsername');
    }
  }),

  selectedLocale: Ember.computed('i18n.locale', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return [this.get('i18n.locale')];
    }
  }),

  selectedTimeFormat: Ember.computed('timeFormat.selected', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return this.get('timeFormat.selected');
    }
  }),

  selectedTheme: Ember.computed('theme.selected', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return this.get('theme.selected');
    }
  }),

  selectedSpacing: Ember.computed('spacing.selected', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return this.get('spacing.selected');
    }
  }),

  selectedLandingPage: Ember.computed('landingPage.selected.key', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return [this.get('landingPage.selected.key')];
    }
  }),

  selectedDateFormat: Ember.computed('dateFormat.selected', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return [this.get('dateFormat.selected.key')];
    }
  }),

  selectedTimeZone: Ember.computed('timezone.selected', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return [this.get('timezone.selected')];
    }
  }),

  selectedNotifications: Ember.computed('notifications.enabled', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return this.get('notifications.enabled');
    }
  }),

  selectedContextMenus: Ember.computed('contextMenus.enabled', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return this.get('contextMenus.enabled');
    }
  }),

  selectedLocaleDidChange: Ember.observer('selectedLocale.firstObject', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingLocale', _this.get('selectedLocale.firstObject'));
    });
  }),

  selectedTimeFormatDidChange: Ember.observer('selectedTimeFormat', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingTimeFormat', _this.get('selectedTimeFormat'));
    });
  }),

  selectedFriendlyNameDidChange: Ember.observer('selectedFriendlyName', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingFriendlyName', _this.get('selectedFriendlyName'));
    });
  }),

  selectedThemeDidChange: Ember.observer('selectedTheme', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingTheme', _this.get('selectedTheme'));
    });
  }),

  selectedSpacingDidChange: Ember.observer('selectedSpacing', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingSpacing', _this.get('selectedSpacing'));
    });
  }),

  selectedTimeZoneDidChange: Ember.observer('selectedTimeZone.firstObject', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingTimezone', _this.get('selectedTimeZone.firstObject'));
    });
  }),

  selectedDateFormatDidChange: Ember.observer('selectedDateFormat.firstObject', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingDateFormat', _this.get('selectedDateFormat.firstObject'));
    });
  }),

  selectedLandingPageDidChange: Ember.observer('selectedLandingPage.firstObject', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingLandingPage', _this.get('selectedLandingPage.firstObject'));
    });
  }),

  selectedNotificationsDidChange: Ember.observer('selectedNotifications', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingNotifications', _this.get('selectedNotifications'));
    });
  }),

  selectedContextMenusDidChange: Ember.observer('selectedContextMenus', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('withoutChanges', false);
      _this.set('pendingContextMenus', _this.get('selectedContextMenus'));
    });
  }),

  saveUserPreferences() {
    this.set('password', null);
    this.set('passwordConfirm', null);

    if (this.get('pendingLocale')) {
      this.set('i18n.locale', this.get('pendingLocale'));
      localStorage.setItem('rsa-i18n-default-locale', this.get('pendingLocale'));
    }

    if (this.get('pendingTimezone')) {
      this.set('timezone.selected', this.get('pendingTimezone'));
    }

    if (this.get('pendingDateFormat')) {
      let dateFormatSelectedOption = this.get('dateFormat.options').findBy('key', this.get('pendingDateFormat'));
      this.set('dateFormat.selected', dateFormatSelectedOption);
    }

    if (this.get('pendingLandingPage')) {
      let landingPageSelectedOption = this.get('landingPage.options').findBy('key', this.get('pendingLandingPage'));
      this.set('landingPage.selected', landingPageSelectedOption);
    }

    if (this.get('pendingTimeFormat')) {
      this.set('timeFormat.selected', this.get('pendingTimeFormat'));
    }

    if (this.get('pendingTheme')) {
      this.set('theme.selected', this.get('pendingTheme'));
    }

    if (this.get('pendingSpacing')) {
      this.set('spacing.selected', this.get('pendingSpacing'));
    }

    if (!Ember.isNone(this.get('pendingNotifications'))) {
      this.set('notifications.enabled', this.get('pendingNotifications'));
    }

    if (!Ember.isNone(this.get('pendingContextMenus'))) {
      this.set('contextMenus.enabled', this.get('pendingContextMenus'));
    }

    if (this.get('pendingFriendlyName') || this.get('pendingFriendlyName') === '') {
      this.set('usernameFormat.username', this.get('pendingFriendlyName'));
    }

    this.get('eventBus').trigger('rsa-application-modal-close-all');

    Ember.run.next(this, function() {
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

    Ember.run.next(this, function() {
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
