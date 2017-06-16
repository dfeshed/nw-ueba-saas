/**
 * @file Protected route
 * Container for all sub-routes that require authentication.
 * @public
 */
import Ember from 'ember';
import Route from 'ember-route';
import RSVP from 'rsvp';
import service from 'ember-service/inject';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
import config from '../config/environment';

const {
  Logger
} = Ember;

const contextAddToListModalId = 'addToList';

/**
 * Add AuthenticatedRouteMixin to ensure the routes extending from this
 * route are not accessible for unauthenticated users. Without authentication,
 * the routes will redirect to the '/login' route.
 * @public
 */
export default Route.extend(AuthenticatedRouteMixin, {

  accessControl: service(),
  dateFormat: service(),
  landingPage: service(),
  session: service(),
  timeFormat: service(),
  timezone: service(),
  userIdle: service(),
  userActivity: service(),
  eventBus: service(),

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

  beforeModel(transition) {
    this._super(...arguments);

    const toPassToTransition = [];
    const redirectName = localStorage.getItem('rsa-post-auth-redirect-name');
    const ids = localStorage.getItem('rsa-post-auth-redirect-ids');
    const params = localStorage.getItem('rsa-post-auth-redirect-params');
    const key = this.get('landingPage.selected.key');

    if (ids) {
      const array = JSON.parse(ids);
      array.forEach((id) => {
        toPassToTransition.push(id);
      });
    }

    if (this.get('session.isAuthenticated') && redirectName && redirectName !== transition.targetName) {
      localStorage.removeItem('rsa-post-auth-redirect-name');
      localStorage.removeItem('rsa-post-auth-redirect-ids');
      localStorage.removeItem('rsa-post-auth-redirect-params');

      if (toPassToTransition.length >= 1) {
        this.transitionTo(redirectName, ...toPassToTransition, { queryParams: JSON.parse(params) });
      } else {
        this.transitionTo(redirectName);
      }
    } else if (transition.targetName === 'protected.index') {
      this._checkAccessAndTransition(key);
    }
  },

  model() {
    localStorage.setItem('rsa-i18n-default-locale', config.i18n.defaultLocale);
    this.set('i18n.locale', config.i18n.defaultLocale);

    if (config.adminServerAvailable) {
      const permissionsPromise = new RSVP.Promise((resolve, reject) => {
        this.request.promiseRequest({
          method: 'getPermissions',
          modelName: 'permissions',
          query: {}
        }).then((response) => {
          this.set('accessControl.roles', response.data);
          resolve();
        }).catch((error) => {
          Logger.error('Error loading permissions', error);
          reject(error);
        });
      });

      const timezonesPromise = new RSVP.Promise((resolve, reject) => {
        this.request.promiseRequest({
          method: 'getTimezones',
          modelName: 'timezones',
          query: {}
        }).then((response) => {
          this.set('timezone.options', response.data);
          resolve();
        }).catch((error) => {
          Logger.error('Error loading timezones', error);
          reject(error);
        });
      });

      const preferencesPromise = new RSVP.Promise((resolve, reject) => {
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
            'timezone.selected': timeZone
          });

          this.get('landingPage').setDefaultLandingPage(defaultComponentUrl);

          resolve();
        }).catch((error) => {
          Logger.error('Error loading preferences', error);
          reject(error);
        });
      });

      return RSVP.all([preferencesPromise, timezonesPromise, permissionsPromise]).catch(() => {
        Logger.error('There was an issue loading your profile. Please try again.');
      });
    }
  },

  actions: {
    openContextPanel(entity) {
      const { type, id } = entity || {};
      this.get('controller').setProperties({
        entityId: id,
        entityType: type
      });
    },
    closeContextPanel() {
      this.get('controller').setProperties({
        entityId: undefined,
        entityType: undefined
      });
    },

    // Actions to open & close the Context addon's "Add To List" application dialog.
    openContextAddToList(entity) {
      const { type, id } = entity || {};
      const eventName = (type && id) ?
        `rsa-application-modal-open-${contextAddToListModalId}` :
        `rsa-application-modal-close-${contextAddToListModalId}`;
      this.get('controller').set('entityToAddToList', entity);
      this.get('eventBus').trigger(eventName);
    },
    closeContextAddToList() {
      this.get('eventBus').trigger(`rsa-application-modal-close-${contextAddToListModalId}`);
      this.get('controller').set('entityToAddToList', undefined);
    }
  },

  _checkAccessAndTransition(key) {
    if ((key === '/investigate' && this.get('accessControl.hasInvestigateAccess')) ||
      (key === '/respond' && this.get('accessControl.hasRespondAccess'))) {
      this.transitionTo(key);
    } else if ((key === this.get('accessControl.adminUrl') && this.get('accessControl.hasAdminAccess')) ||
      (key === this.get('accessControl.configUrl') && this.get('accessControl.hasConfigAccess')) ||
      (key === '/investigation' && this.get('accessControl.hasInvestigateAccess')) ||
      (key === '/unified' && this.get('accessControl.hasMonitorAccess'))) {
      window.location.href = key;
    } else {
      window.location.href = '/unified';
    }
  }
});
