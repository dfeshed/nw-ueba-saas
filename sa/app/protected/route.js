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
  inject: {
    service
  }
 } = Ember;

/**
 * Add AuthenticatedRouteMixin to ensure the routes extending from this
 * route are not accessible for unauthenticated users. Without authentication,
 * the routes will redirect to the '/login' route.
 * @public
 */
export default Route.extend(AuthenticatedRouteMixin, {

  contextMenus: service(),
  notifications: service(),

  dateFormat: service(),
  landingPage: service(),
  timeFormat: service(),
  timezone: service(),

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

  model() {
    // Fetch user preferences
    return this.request.promiseRequest({
      method: 'getPreference',
      modelName: 'preferences',
      query: {}
    }).then((response) => {
      const {
        contextMenuEnabled,
        notificationEnabled,
        dateFormat,
        timeFormat,
        timeZone,
        defaultComponentUrl
      } = response.data;

      this.setProperties({
        'contextMenus.enabled': contextMenuEnabled,
        'notifications.enabled': notificationEnabled,
        'dateFormat.selected': dateFormat,
        'timeFormat.selected': timeFormat,
        'timezone.selected': timeZone,
        'landingPage.selected': defaultComponentUrl
      });
    }).catch(() => {
      Logger.error('Error loading preferences');
    });
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
