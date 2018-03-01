import Component from '@ember/component';
import { isEmpty } from '@ember/utils';
import layout from './template';
import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { alias, readOnly, gt } from 'ember-computed-decorators';
import csrfToken from '../../mixins/csrf-token';
import config from 'ember-get-config';
import { warn } from '@ember/debug';

export default Component.extend(csrfToken, {

  layout,

  classNames: ['rsa-application-user-preferences-panel'],

  classNameBindings: ['isExpanded'],

  appVersion: service(),
  eventBus: service(),
  dateFormat: service(),
  landingPage: service(),
  investigatePage: service(),
  layoutService: service('layout'),
  moment: service(),
  request: service(),
  timeFormat: service(),
  timezone: service(),

  isExpanded: false,
  locales: config.i18n.includedLocales,

  @readOnly @alias('appVersion.version') version: null,
  @readOnly @gt('locales.length', 1) displayLocales: null,

  init() {
    this._super(arguments);

    this.get('eventBus').on('rsa-application-user-preferences-panel-will-toggle', () => {
      this.toggleProperty('isExpanded');
    });

    run.schedule('afterRender', () => {
      if (isEmpty(this.get('timezone.selected'))) {
        this.set('timezone.selected', this.get('timezone.options').findBy('zoneId', config.timezoneDefault));
      }

      if (isEmpty(this.get('dateFormat.selected'))) {
        this.set('dateFormat.selected', this.get('dateFormat.options').findBy('key', config.dateFormatDefault));
      }

      if (isEmpty(this.get('timeFormat.selected'))) {
        this.set('timeFormat.selected', this.get('timeFormat.options').findBy('key', config.timeFormatDefault));
      }

      if (isEmpty(this.get('landingPage.selected'))) {
        this.set('landingPage.selected', this.get('landingPage.options').findBy('key', config.landingPageDefault));
      }

      if (isEmpty(this.get('investigatePage.selected'))) {
        this.set('investigatePage.selected', this.get('investigatePage.options').findBy('key', config.investigatePageDefault));
      }
    });
  },

  actions: {
    logout() {
      this.logout();
    },
    setLocale(selection) {
      if (!isEmpty(selection)) {
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
          warn('Error updating locale', { id: 'component-lib.components.rsa-application-user-preferences-panel.component' });
        });
      }
    },

    setDefaultLandingPage(selection) {
      this.get('landingPage').setDefaultLandingPage(selection);
    },

    setDefaultInvestigatePage(selection) {
      this.get('investigatePage').setDefaultInvestigatePage(selection);
    },

    setTimezone(selection) {
      this.set('timezone.selected', selection);
    },

    setDateFormat(selection) {
      this.set('dateFormat.selected', selection);
    },

    setTimeFormat(selection) {
      this.set('timeFormat.selected', selection);
    },

    toggleUserPreferences() {
      this.get('layoutService').toggleUserPreferences();
    }
  }
});
