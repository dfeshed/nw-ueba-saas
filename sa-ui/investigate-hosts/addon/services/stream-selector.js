import Service, { inject as service } from '@ember/service';
import config from 'ember-get-config';

export default Service.extend({

  redux: service(),

  streamOptionSelector({ modelName, method, customOptions = {} }) {

    const modelConfig = ((config.socketRoutes || {})[modelName] || {});
    const modelMethod = modelConfig[method] || {};
    const endpointPreference = modelMethod.endpointPreference || '';

    const requiredSocketUrl = 'endpoint/socket';

    const state = this.get('redux').getState();
    const { serverId: socketUrlPostfix, selectedMachineServerId } = state.endpointQuery;

    let streamOptionConfig = {};

    switch (endpointPreference) {
      case 'any': {
        streamOptionConfig = {
          socketUrlPostfix: 'any',
          requiredSocketUrl
        };
        break;
      }
      case 'specific': {
        streamOptionConfig = {
          socketUrlPostfix: selectedMachineServerId,
          requiredSocketUrl,
          ...customOptions
        };
        break;
      }
      default: {
        streamOptionConfig = {
          socketUrlPostfix,
          requiredSocketUrl,
          ...customOptions
        };
        break;
      }
    }

    return streamOptionConfig;
  }
});