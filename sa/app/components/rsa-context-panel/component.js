import Ember from 'ember';
import DatasourceList from 'sa/context/datasource-list';
import MultiColumnList from 'sa/context/tree-table';
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
  multicolumn: {
    multiColumnList: MultiColumnList

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
        liveConnectData: LiveConnect.create()
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
      case 'Modules': {
        set(this.get('model').contextData, 'additionalData', contextData.resultMeta);
        set(this.get('model').contextData, contextData.dataSourceType, contextData.resultList);
        break;
      }

      case 'LIST': {
        const { dataSourceType } = contextData;
        const model = this.get('model');
        const { contextData: allContextData } = model;
        const allSourceTypeData = allContextData[dataSourceType] || [];
        set(allContextData, dataSourceType, allSourceTypeData.concat(contextData));
        break;
      }

      case 'LiveConnect-File':
      case 'LiveConnect-Ip':
      case 'LiveConnect-Domain':
        contextData.resultList.forEach((obj) => {
          if (obj && obj.record && obj.record.length > 2) {
            this._parseLiveConnectData(contextData.dataSourceType, obj.record);
          } else {
            set(this.get('model').contextData, 'liveConnectData', null);
          }
        });
        break;
      default:
        set(this.get('model').contextData, contextData.dataSourceType, contextData.resultList);

    }
  },

  _parseLiveConnectData(entityType, record) {
    const lcData = this.get('model').contextData.liveConnectData;
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
    lcData.set('allTags', record[2].LiveConnectApi.riskTagTypes);
    lcData.set('allReasons', record[2].LiveConnectApi.riskReasonTypes);
  }
});