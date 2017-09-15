import Route from 'ember-route';
import service from 'ember-service/inject';
import $ from 'jquery';
import config from 'ember-get-config';

const { useMockServer, mockServerUrl, environment } = config;

export default Route.extend({

  contextualHelp: service(),
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

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.respondModule'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
  }
});
