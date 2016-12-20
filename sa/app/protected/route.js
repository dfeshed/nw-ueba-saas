/**
 * @file Protected route
 * Container for all sub-routes that require authentication.
 * @public
 */
import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

const {
  Route,
  Logger,
  isNone,
  inject: {
    service
  },
  RSVP: {
    Promise,
    all
  },
  run: {
    cancel,
    later
  }
 } = Ember;

/**
 * Add AuthenticatedRouteMixin to ensure the routes extending from this
 * route are not accessible for unauthenticated users. Without authentication,
 * the routes will redirect to the '/login' route.
 * @public
 */
export default Route.extend(AuthenticatedRouteMixin, {
  session: service(),

  dateFormat: service(),
  landingPage: service(),
  timeFormat: service(),
  timezone: service(),
  i18n: service(),

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
    if (!this.get('session.isAuthenticated') && isNone(localStorage.getItem('rsa-post-auth-redirect'))) {
      localStorage.setItem('rsa-post-auth-redirect', transition.targetName);
    }

    this._super(...arguments);
  },

  model() {
    const timezonesPromise = new Promise((resolve) => {
      const forceResolve = later(() => {
        this.set('timezone.options', [{
          'displayLabel': 'UTC (GMT+00:00)',
          'offset': 'GMT+00:00',
          'zoneId': 'UTC'
        }]);
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
      });
    });

    const preferencesPromise = new Promise((resolve) => {
      const forceResolve = later(() => {
        localStorage.setItem('rsa-i18n-default-locale', 'en-us');

        this.setProperties({
          'i18n.locale': 'en-us',
          'dateFormat.selected': 'MM/dd/yyyy',
          'timeFormat.selected': 'HR24',
          'timezone.selected': 'UTC',
          'landingPage.selected': '/do/respond'
        });

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
      });
    });

    return all([preferencesPromise, timezonesPromise]);
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
