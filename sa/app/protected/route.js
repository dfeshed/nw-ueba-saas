/**
 * @file Protected route
 * Container for all sub-routes that require authentication.
 * @public
 */
import Ember from 'ember';
import { cancel, later } from 'ember-runloop';
import { isNone } from 'ember-utils';
import Route from 'ember-route';
import RSVP from 'rsvp';
import service from 'ember-service/inject';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
import config from '../config/environment';

const {
  Logger,
  testing
} = Ember;

/**
 * Add AuthenticatedRouteMixin to ensure the routes extending from this
 * route are not accessible for unauthenticated users. Without authentication,
 * the routes will redirect to the '/login' route.
 * @public
 */
export default Route.extend(AuthenticatedRouteMixin, {

  accessControl: service(),

  session: service(),

  dateFormat: service(),
  landingPage: service(),
  timeFormat: service(),
  timezone: service(),
  i18n: service(),

  userIdle: service(),
  userActivity: service(),

  queryParams: {
    /**
     * The type of entity to be looked up in the Context Panel.
     * Entity types are defined in configurable Admin settings, but typically include 'IP', 'USER', 'DOMAIN', 'HOST', etc.
     * @type {string}
     * @public
     */
    entityType: {
      refreshModel: false,
      replace: true
    },
    /**
     * The ID of the entity to be looked up in the Context Panel (e.g., an IP address, a user name, a domain name, etc).
     * @type {string|number}
     * @public
     */
    entityId: {
      refreshModel: false,
      replace: true
    }
  },

  init() {
    if (!testing) {
      // When the user performs an action, update last session access
      this.get('userActivity').on('userActive', this, () => {
        localStorage.setItem('rsa-nw-last-session-access', new Date().getTime());
      });

      // After configured idle timeout period, logout
      this.get('userIdle').on('idleChanged', (isIdle) => {
        if (isIdle) {
          this.send('logout');
        }
      });
    }

    this._super(...arguments);
  },

  beforeModel(transition) {
    if (!this.get('session.isAuthenticated') && isNone(localStorage.getItem('rsa-post-auth-redirect'))) {
      localStorage.setItem('rsa-post-auth-redirect', transition.targetName);
    }

    this._super(...arguments);
  },

  model() {
    localStorage.setItem('rsa-i18n-default-locale', config.i18n.defaultLocale);
    this.set('i18n.locale', config.i18n.defaultLocale);

    const permissionsPromise = new RSVP.Promise((resolve, reject) => {
      const forceResolve = later(() => {
        resolve();
      }, 3500);

      this.request.promiseRequest({
        method: 'getPermissions',
        modelName: 'permissions',
        query: {}
      }).then((response) => {
        this.set('accessControl.roles', response.data);
        cancel(forceResolve);
        resolve();
      }).catch((error) => {
        Logger.error('Error loading permissions', error);
        reject(error);
      });
    });

    const timezonesPromise = new RSVP.Promise((resolve, reject) => {
      const forceResolve = later(() => {
        resolve();
      }, 3500);

      this.request.promiseRequest({
        method: 'getTimezones',
        modelName: 'timezones',
        query: {}
      }).then((response) => {
        this.set('timezone.options', response.data);
        cancel(forceResolve);
        resolve();
      }).catch((error) => {
        Logger.error('Error loading timezones', error);
        reject(error);
      });
    });

    const preferencesPromise = new RSVP.Promise((resolve, reject) => {
      const forceResolve = later(() => {
        resolve();
      }, 3500);

      // Fetch user preferences
      this.request.promiseRequest({
        method: 'getPreference',
        modelName: 'preferences',
        query: {}
      }).then((response) => {
        const {
          userLocale,
          dateFormat,
          timeFormat,
          timeZone,
          defaultComponentUrl
        } = response.data;

        localStorage.setItem('rsa-i18n-default-locale', userLocale.replace(/_/, '-').toLowerCase());

        this.setProperties({
          'i18n.locale': userLocale.replace(/_/, '-').toLowerCase(),
          'dateFormat.selected': dateFormat,
          'timeFormat.selected': timeFormat,
          'timezone.selected': timeZone,
          'landingPage.selected': defaultComponentUrl
        });

        cancel(forceResolve);
        resolve();
      }).catch((error) => {
        Logger.error('Error loading preferences', error);
        reject(error);
      });
    });

    return RSVP.all([preferencesPromise, timezonesPromise, permissionsPromise]);
  },

  actions: {
    closeContextPanel() {
      this.get('controller').setProperties({
        entityId: undefined,
        entityType: undefined
      });
    }
  }
});
