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

        incidentsData: null,
        alertsData: null,
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
            set(this.currentModel.contextData, 'incidentsData', dataSource.resultList);
            break;
          }
        case 'Alerts':
          {
            set(this.currentModel.contextData, 'alertsData', dataSource.resultList);
            break;
          }
        case 'ECAT':
          {

            let ecatData = Ecat.create();

            dataSource.resultList.forEach((obj) => {
              let { Machine, Iocs, Processes, Network } = obj.details;
              // get details and push into the respective objects
              if (Machine) {
                set(ecatData, 'host', Machine);
              } else if (Iocs) {
                ecatData.get('iioc').pushObjects(Iocs);
              } else if (Processes) {
                ecatData.get('processes').pushObjects(Processes);
              } else if (Network) {
                ecatData.get('network').pushObjects(Network);
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
