/**
 * @file Protected route
 * Container for all sub-routes that require authentication.
 * @public
 */
import Route from 'ember-route';
import RSVP from 'rsvp';
import service from 'ember-service/inject';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
import * as ACTION_TYPES from 'sa/actions/types';
import config from '../config/environment';
import $ from 'jquery';

const {
  console
} = window;

const contextAddToListModalId = 'addToList';

/**
 * Add AuthenticatedRouteMixin to ensure the routes extending from this
 * route are not accessible for unauthenticated users. Without authentication,
 * the routes will redirect to the '/login' route.
 * @public
 */
export default Route.extend(AuthenticatedRouteMixin, {

  redux: service(),
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
     * Indicated whether route is included in iframe or not
     * @type {boolean}
     * @public
     */
    iframedIntoClassic: {
      refreshModel: false,
      replace: true
    },
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

  iframedIntoClassic: false,

  afterModel(models, transition) {
    this._super(...arguments);

    const key = this.get('landingPage.selected.key');
    const classicRedirect = localStorage.getItem('rsa-post-auth-redirect');

    if (classicRedirect) {
      window.location = classicRedirect;
      return localStorage.removeItem('rsa-post-auth-redirect');
    } else {
      this._checkAccessAndTransition(key, transition.targetName);
    }
  },

  model({ iframedIntoClassic }) {

    // If packager route is from the classic SA then hide the application navigation as this route is mounted in iframe
    if (iframedIntoClassic) {
      $('body').addClass('iframed-into-classic');
    } else {
      $('body').removeClass('iframed-into-classic');
    }
    const permissionsPromise = new RSVP.Promise((resolve, reject) => {
      this.request.promiseRequest({
        method: 'getPermissions',
        modelName: 'permissions',
        query: {}
      }).then((response) => {
        this.set('accessControl.roles', response.data);
        resolve();
      }).catch((error) => {
        console.error('Error loading permissions', error);
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
        console.error('Error loading timezones', error);
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
          themeType,
          userLocale,
          dateFormat,
          timeFormat,
          timeZone,
          defaultComponentUrl
        } = response.data;

        if (userLocale && config.i18n.includedLocales.length > 1) {
          const locale = userLocale.replace(/_/, '-').toLowerCase();
          localStorage.setItem('rsa-i18n-default-locale', locale);
          this.set('i18n.locale', locale);
        }

        this.setProperties({
          'dateFormat.selected': dateFormat,
          'timeFormat.selected': timeFormat,
          'timezone.selected': timeZone
        });

        const redux = this.get('redux');
        redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: themeType });

        if (defaultComponentUrl) {
          this.get('landingPage').setDefaultLandingPage(defaultComponentUrl);
        }

        resolve();
      }).catch((error) => {
        console.error('Error loading preferences', error);
        reject(error);
      });
    });

    return RSVP.all([preferencesPromise, timezonesPromise, permissionsPromise]).catch(() => {
      console.error('There was an issue loading your profile. Please try again.');
    });
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

    // Actions to open & close the Context addon's
    // "Add To List" application dialog.
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

  _checkAccessAndTransition(key, transitionName) {
    if ( // known transition into ember with perms
      (transitionName && transitionName.includes('configure')) ||
      (transitionName && transitionName.includes('respond') && this.get('accessControl.hasRespondAccess')) ||
      (transitionName && transitionName.includes('packager')) ||
      (transitionName && transitionName.includes('investigate') && this.get('accessControl.hasInvestigateAccess'))
    ) {
      return this.transitionTo(transitionName);
    } else if ( // classic default landing page transition with perms
      (transitionName && !transitionName.includes('respond') && !transitionName.includes('investigate')) &&
      ((key === this.get('accessControl.adminUrl')) && this.get('accessControl.hasAdminAccess')) ||
      ((key === this.get('accessControl.configUrl')) && this.get('accessControl.hasConfigAccess')) ||
      ((key === '/investigation') && this.get('accessControl.hasInvestigateAccess')) ||
      ((key === '/unified') && this.get('accessControl.hasMonitorAccess'))
    ) {
      return window.location.href = key;
    } else if ( // ember default landing page transition with perms
      (key && key.includes('respond') && this.get('accessControl.hasRespondAccess')) ||
      (key && key.includes('investigate') && this.get('accessControl.hasInvestigateAccess'))
    ) {
      return this.transitionTo(key);
    } else { // neither transition nor default landing page found
      if ((config.landingPageDefault === '/respond') && this.get('accessControl.hasRespondAccess')) {
        return this.transitionTo('protected.respond');
      } else {
        return window.location.href = '/unified';
      }
    }
  }
});
