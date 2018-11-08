/**
 * @file Protected route
 * Container for all sub-routes that require authentication.
 * @public
 */
import Route from '@ember/routing/route';
import RSVP from 'rsvp';
import { get } from '@ember/object';
import { bindActionCreators } from 'redux';
import { inject as service } from '@ember/service';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
import { updateLocaleByKey } from 'sa/actions/creators/preferences';
import * as ACTION_TYPES from 'sa/actions/types';
import config from '../config/environment';
import { jwt_decode as jwtDecode } from 'ember-cli-jwt-decode';
import $ from 'jquery';

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
  investigatePage: service(),
  session: service(),
  timeFormat: service(),
  timezone: service(),
  userIdle: service(),
  userActivity: service(),
  eventBus: service(),
  features: service(),

  queryParams: {
    /**
     * Indicated whether route is included in iframe or not
     * @type {boolean}
     * @public
     */
    iframedIntoClassic: {
      refreshModel: false,
      replace: true
    }
  },

  iframedIntoClassic: false,

  // source management (a.k.a. USM)
  getSourceManagementFeatures() {
    const request = get(this, 'request');
    return new RSVP.Promise((resolve, reject) => {
      request.promiseRequest({
        method: 'getSupportedFeatures',
        modelName: 'sourceManagementFeatures',
        query: {}
      }).then((response) => {
        this.get('features').setFeatureFlags(response.data);
        resolve(response);
      }).catch((error) => {
        // eslint-disable-next-line no-console
        console.error('Error loading source-management (USM) feature flags', error);
        reject(error);
      });
    });
  },

  getPermissions() {
    const request = get(this, 'request');
    return new RSVP.Promise((resolve, reject) => {
      request.promiseRequest({
        method: 'getPermissions',
        modelName: 'permissions',
        query: {}
      }).then((response) => {
        this.set('accessControl.roles', response.data);
        resolve(response);
      }).catch((error) => {
        // eslint-disable-next-line no-console
        console.error('Error loading permissions', error);
        reject(error);
      });
    });
  },

  getTimezones() {
    const request = get(this, 'request');
    return new RSVP.Promise((resolve, reject) => {
      request.promiseRequest({
        method: 'getTimezones',
        modelName: 'timezones',
        query: {}
      }).then((response) => {
        this.set('timezone.options', response.data);
        resolve(response);
      }).catch((error) => {
        // eslint-disable-next-line no-console
        console.error('Error loading timezones', error);
        reject(error);
      });
    });
  },

  getPreferences() {
    const redux = get(this, 'redux');
    const request = get(this, 'request');
    return new RSVP.Promise((resolve, reject) => {
      request.promiseRequest({
        method: 'getPreference',
        modelName: 'preferences',
        query: {}
      }).then((response) => {
        const {
          userLocale,
          themeType,
          dateFormat,
          timeFormat,
          defaultComponentUrl,
          defaultInvestigatePage
        } = response.data;

        // Note: the timeZone preference will be set later in the model() hook because of its dependency on the timezone list
        this.setProperties({
          'dateFormat.selected': dateFormat,
          'timeFormat.selected': timeFormat
        });

        const updateLocale = bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux));
        updateLocale(userLocale);

        redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: themeType });

        if (defaultComponentUrl) {
          this.get('landingPage').setDefaultLandingPage(defaultComponentUrl);
        }

        if (defaultInvestigatePage) {
          this.get('investigatePage').setDefaultInvestigatePage(defaultInvestigatePage);
        }

        resolve(response);
      }).catch((error) => {
        // eslint-disable-next-line no-console
        console.error('Error loading preferences', error);
        reject(error);
      });
    });
  },

  // Resolves the user's roles/authorities from the token
  getRoles() {
    const authConfig = config['ember-simple-auth'];

    if (authConfig) {
      const tokenKey = authConfig.accessTokenKey;
      const token = localStorage.getItem(tokenKey);

      if (token) {
        const decodedToken = jwtDecode(token);
        return decodedToken.authorities;
      }
    }
  },

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

    // Set feature flags
    this.getSourceManagementFeatures();

    const permissionsPromise = this.getPermissions();
    const timezonesPromise = this.getTimezones();
    const preferencesPromise = this.getPreferences();

    // Resolve the user's roles/authorities from the JWT token and update accessControl
    // These are used only for UEBA permission handling, since for the iframed UEBA app
    // no real permissions exist, only user roles.
    const roles = this.getRoles();
    this.set('accessControl.authorities', roles);

    return RSVP.all([
      preferencesPromise,
      timezonesPromise,
      permissionsPromise
    ]).then((responses) => {
      // set the user preference timezone after timezones have been loaded, since the timezone service depends
      // on having the full list of timezone options for values to be properly set.
      const [preferences] = responses;
      this.set('timezone.selected', preferences.data.timeZone);
    }).catch(() => {
      // eslint-disable-next-line no-console
      console.error('There was an issue loading your profile. Please try again.');
    });
  },

  _checkAccessAndTransition(key, transitionName) {
    if ( // known transition into ember with perms
      (transitionName && transitionName.includes('configure')) ||
      (transitionName && transitionName.includes('respond') && this.get('accessControl.hasRespondAccess')) ||
      (transitionName && transitionName.includes('packager')) ||
      (transitionName && transitionName.includes('investigate') && this.get('accessControl.hasInvestigateAccess')) ||
      (transitionName && transitionName.includes('admin') && this.get('accessControl.hasAdminAccess'))
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
