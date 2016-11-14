import Ember from 'ember';
import DatasourceList from 'sa/context/datasource-list';
import Ecat from 'sa/context/ecat';
import LiveConnect from 'sa/context/live-connect';

const {
  inject: {
    service
  },
  isArray,
  run,
  set,
  Component,
  Logger
} = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel',

  request: service(),

  model: null,
  contextData: null,
  columnHeader: {
    datasourceList: DatasourceList
  },

  didReceiveAttrs() {
    run.once(this, this._initModel);
  },

  _initModel() {

    const { entityId, entityType } = this.getProperties('entityId', 'entityType');

    const contextModels = {
      displayContextPanel: true,
      lookupKey: entityId,
      meta: entityType,
      ips: [],
      prefetch: [],
      contextData: {
        incidentsData: null,
        alertsData: null,
        ecatData: null,
        liveConnectData: null
      }
    };

    if (entityId && entityType) {
      this._doCHLookup(contextModels.lookupKey, contextModels.meta);
    }
    this.setProperties({
      model: contextModels,
      contextData: contextModels.contextData
    });
  },


  /*
   * Lookup the context data for the given key and meta.
   * @private
   */
  _doCHLookup(lookupKey, meta) {
    Logger.info('fetching context data for lookup key ', lookupKey);
    const filter = [];
    if (lookupKey && meta) {
      filter.push({ field: 'meta', value: meta });
      filter.push({ field: 'value', value: lookupKey });
    }
    this.get('request').streamRequest({
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
            this._populateContextsData(entry);
          });
        }
      },
      onError(response) {
        Logger.error('Error processing stream call for context lookup.', response);
      }
    });
  },

  _populateContextsData(contextData) {
    switch (contextData.dataSourceType) {

      case 'ECAT':
        {
          const ecatData = Ecat.create();

          contextData.resultList.forEach((obj) => {
            const { Machine, Iocs, Processes, Network } = obj.details;
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

          set(this.get('model').contextData, 'ecat', ecatData);
          break;
        }
      case 'LiveConnect-File':
      case 'LiveConnect-Ip':
      case 'LiveConnect-Domain':
        contextData.resultList.forEach((obj) => {
          if (obj && obj.record && obj.record.length) {
            set(this.get('model').contextData, 'liveConnectData', this._parseLiveConnectData(contextData.dataSourceType, obj.record));
          } else {
            set(this.get('model').contextData, 'liveConnectData', null);
          }
        });
        break;
      default:
        {
          const { dataSourceType } = contextData;
          const model = this.get('model');
          const { contextData: allContextData } = model;
          const allSourceTypeData = allContextData[dataSourceType] || [];
          set(allContextData, dataSourceType, allSourceTypeData.concat(contextData));
        }
    }
  },

  _parseLiveConnectData(entityType, record) {
    const lcData = LiveConnect.create();
    switch (entityType) {
      case 'LiveConnect-Ip':
        lcData.set('IpInfo', record[0].IpInfo);
        lcData.set('IpReputation', record[1].IpReputation);
        break;
      case 'LiveConnect-File':
        lcData.set('FileInfo', record[0].FileInfo);
        lcData.set('FileReputation', record[1].FileReputation);
        break;
      case 'LiveConnect-Domain':
        lcData.set('DomainInfo', record[0].DomainInfo);
        lcData.set('DomainReputation', record[1].DomainReputation);
        break;
    }
    return lcData;
  }
});
