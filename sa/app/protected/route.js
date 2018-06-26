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
import $ from 'jquery';

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

  getEndpointFeatures() {
    const request = get(this, 'request');
    return new RSVP.Promise((resolve, reject) => {
      request.promiseRequest({
        method: 'getSupportedFeatures',
        modelName: 'endpointFeatures',
        query: {}
      }).then((response) => {
        this.get('features').setFeatureFlags(response.data);
        resolve();
      }).catch((error) => {
        // eslint-disable-next-line no-console
        console.error('Error loading endpoint feature flags', error);
        reject();
      });
    });
  },

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
        // TODO delete this whole try/catch block once USM is GA
        // this is a temporary ('til 11.3) special case to also let the ExtJS side of the app get to the feature flag
        // without adding any Java controller/rabbitmq code on the SA Classic server side
        try {
          if (this.get('features').isEnabled('rsa.usm')) {
            sessionStorage.setItem('features.rsaUsm', true);
          } else {
            sessionStorage.removeItem('features.rsaUsm');
          }
        } catch (e) {
          // eslint-disable-next-line no-console
          console.error('Error storing source-management (USM) feature flags to sessionStorage', e);
          sessionStorage.removeItem('features.rsaUsm');
        }
        resolve();
      }).catch((error) => {
        // eslint-disable-next-line no-console
        console.error('Error loading source-management (USM) feature flags', error);
        reject();
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
        resolve();
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
        resolve();
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
          timeZone,
          defaultComponentUrl,
          defaultInvestigatePage
        } = response.data;

        this.setProperties({
          'dateFormat.selected': dateFormat,
          'timeFormat.selected': timeFormat,
          'timezone.selected': timeZone
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

        resolve();
      }).catch((error) => {
        // eslint-disable-next-line no-console
        console.error('Error loading preferences', error);
        reject(error);
      });
    });
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

    const permissionsPromise = this.getPermissions();
    const timezonesPromise = this.getTimezones();
    const preferencesPromise = this.getPreferences();

    // Set feature flags
    this.getEndpointFeatures();
    this.getSourceManagementFeatures();

    return RSVP.all([
      preferencesPromise,
      timezonesPromise,
      permissionsPromise
    ]).catch(() => {
      // eslint-disable-next-line no-console
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
