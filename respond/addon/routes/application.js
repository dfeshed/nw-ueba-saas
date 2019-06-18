import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import $ from 'jquery';
import config from 'ember-get-config';
import { riac as riacApi } from 'respond/actions/api';
import creators from 'respond/actions/creators';

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

    // don't transition to any sub-routes until the healthcheck[ and riac WS call ] completes.
    return healthCheck.then(() => {
      controller.set('respondServerOffline', false); // in case the server was previously offline but is now back online
      const promise = riacApi.fetchRiacValue();
      this.redux.dispatch(creators.riac.createRiacAction(promise));
      return promise;
    }, () => {
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
