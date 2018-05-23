import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import $ from 'jquery';
import { get } from '@ember/object';
import config from 'ember-get-config';
import { recon } from 'respond/actions/api';
import { bindActionCreators } from 'redux';

const { useMockServer, mockServerUrl, environment } = config;

export default Route.extend({

  contextualHelp: service(),
  redux: service(),
  i18n: service(),

  title(tokens) {
    const i18n = this.get('i18n');
    tokens = (tokens || []).concat([
      i18n.t('respond.title'),
      i18n.t('appTitle')
    ]);
    return tokens.join(' - ');
  },

  /**
   * Returns the url for the socket info endpoint, which is used here as a health check against the service to ensure
   * that it is running and available.
   * @method _socketInfoUrl
   * @private
   */
  _socketInfoUrl() {
    let socketInfoUrl = '/api/respond/socket/info';
    if (environment === 'development' || environment === 'test') {
      socketInfoUrl = useMockServer ? `${mockServerUrl}/socket/info` : '/respond/socket/info';
    }
    return socketInfoUrl;
  },

  beforeModel() {
    // Health check against the Respond service. If it returns an error, treat the service as offline
    const healthCheck = $.ajax({ url: this._socketInfoUrl(), cache: false });
    const controller = this.controllerFor('application');
    healthCheck.then(() => {
      controller.set('respondServerOffline', false); // in case the server was previously offline but is now back online
    });
    healthCheck.catch(() => {
      controller.set('respondServerOffline', true); // in case the server is offline
    });
  },

  getServices(redux) {
    const getServices = bindActionCreators(recon.getServices, redux.dispatch.bind(redux));
    try {
      getServices();
    } catch (e) {
      // eslint-disable-next-line no-console
      console.log('Error fetching core services: ', e);
    }
  },

  model() {
    const redux = get(this, 'redux');
    this.getServices(redux);
    return {};
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.respondModule'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
  }
});
