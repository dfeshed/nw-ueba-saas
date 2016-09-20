import Ember from 'ember';
import Ecat from 'sa/context/ecat';
import LiveConnect from 'sa/context/live-connect';

const {
    Route,
    Logger,
    set,
    setProperties,
    isArray,
    inject: {
        service
    }
} = Ember;

export default Route.extend({
  layoutService: service('layout'),

  activate() {
    this.set('journalPanel', 'hidden');
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'hidden');
    this.set('layoutService.panelB', 'half');
    this.set('layoutService.contextPanel', 'half');
  },

  deactivate() {
    this.set('layoutService.journalPanel', 'hidden');
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.contextPanel', 'hidden');
  },

  model(options) {

    let contextModels = {
      displayContextPanel: true,
      lookupKey: options.context_id,
      meta: 'IP',
      ips: [],
      prefetch: [],
      contextData: {
        ecatData: null,
        liveConnectData: null
      }
    };

    this._doCHLookup(contextModels.lookupKey, contextModels.meta);
    return contextModels;
  },

  actions: {
    closeContextPanel() {
      set(this.currentModel, 'displayContextPanel', false);
    }
  },

  /*
   * Lookup the context data for the given key and meta.
   * @private
   */
  _doCHLookup(lookupKey, meta) {
    Logger.info('fetching context data for lookup key ', lookupKey);
    let filter = [];
    if (lookupKey && meta) {
      filter.push({ field: 'meta', value: meta });
      filter.push({ field: 'value', value: lookupKey });
    }
    this.request.streamRequest({
      method: 'stream',
      modelName: 'context',
      query: {
        filter
      },
      streamOptions: { requireRequestId: false },
      onResponse: ({ data }) => {
        Logger.info('pushing data to context model');

        if (isArray(data)) {
          data.forEach((entry) => {
            this._populateContextsData(entry.data);
          });
        }
      },
      onError(response) {
        Logger.error('Error processing stream call for context lookup.', response);
      }
    });
  },

  _populateContextsData(contextData) {
    let dataSources = contextData.result;

    dataSources.forEach((dataSource) => {

      switch (dataSource.dataSourceType) {
        case 'Incidents':
          {
            break;
          }
        case 'Alerts':
          {
            break;
          }
        case 'ECAT':
          {
            let machine, iocs, processes, network;
            let ecatData = Ecat.create();

            dataSource.resultList.forEach((obj) => {
              if (obj && obj !== '' && obj.minimum_ioc && obj.minimum_ioc !== '') {
              // object is already assigned, reset variables.
                machine = null;
                iocs = null;
                processes = null;
                network = null;
              } else {
                machine = obj.details.Machine;
                iocs = obj.details.Iocs;
                processes = obj.details.Processes;
                network = obj.details.Network;
              }
              // get details and push into the respective objects
              if (machine) {
                set(ecatData, 'host', machine);
              } else if (iocs) {
                iocs.forEach((entry) => ecatData.get('iioc').push(entry));
              } else if (processes) {
                processes.forEach((entry) => ecatData.get('processes').push(entry));
              } else if (network) {
                network.forEach((entry) => ecatData.get('network').push(entry));
              } else {
               // module object details
                ecatData.set('modulesCount', obj.total_modules_count);
                ecatData.set('minIoc', obj.minimum_ioc);
                if (obj.details.Items !== undefined) {
                  obj.details.Items.forEach((entry) => ecatData.get('modules').push(entry));
                }
              }
            });

            set(this.currentModel.contextData, 'ecatData', ecatData);
            break;
          }
        case 'LiveConnect':
          dataSource.resultList.forEach((obj) => {
            let lcData = null;
            if (obj && obj.details && obj.details.IpReputation) {
              let ipRep = obj.details.IpReputation;
              lcData = LiveConnect.create();
              setProperties(lcData, { ...ipRep });
            }
            set(this.currentModel.contextData, 'liveConnectData', lcData);
          });
          break;
        default:
          {
            Logger.error('Data Source is not supported by Context Hub ', dataSource.dataSourceType);
          }
      }

    });

  }

});
