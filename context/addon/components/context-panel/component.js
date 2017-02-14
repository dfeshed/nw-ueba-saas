import Ember from 'ember';
import MultiColumnList from 'context/config/tree-table';
import LiveConnect from 'context/config/live-connect';
import TabList from 'context/config/dynamic-tab';
import endpointColumns from 'context/config/endpoint-columns';
import imColumns from 'context/config/im-columns';
import liveConnectColumns from 'context/config/liveconnect-columns';
import layout from './template';
import machineData from 'context/config/machines';
import userDetails from 'context/config/users';

const {
  inject: {
    service
  },
  isArray,
  run,
  set,
  Component,
  Logger,
  String: {
    htmlSafe
  }
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel-header',
  entity: null,
  active: 'overview',
  hasResponse: false,
  errorMessage: null,
  dynamictabs: {
    tabs: TabList
  },
  request: service(),

  model: null,
  contextData: null,
  liveConnectObj: {
    allTags: 'allTags',
    allReasons: 'allReasons',
    'LiveConnect-Ip': {
      info: 'IpInfo',
      Reputation: 'IpReputation',
      fetchRelatedEntities: ['lcRelatedFiles', 'lcRelatedDomains'],
      relatedEntityResponse: 'ips',
      relatedEntity: 'RelatedIps',
      relatedEntities_count: ['relatedFilesCount', 'relatedDomainsCount']
    },
    'LiveConnect-Domain': {
      info: 'DomainInfo',
      Reputation: 'DomainReputation',
      fetchRelatedEntities: ['lcRelatedIps', 'lcRelatedFiles'],
      relatedEntityResponse: 'domains',
      relatedEntity: 'RelatedDomains',
      relatedEntities_count: ['relatedIpsCount', 'relatedFilesCount']
    },
    'LiveConnect-File': {
      info: 'FileInfo', Reputation: 'FileReputation',
      fetchRelatedEntities: ['lcRelatedDomains', 'lcRelatedIps'],
      relatedEntityResponse: 'files',
      relatedEntity: 'RelatedFiles',
      relatedEntities_count: ['relatedIpsCount', 'relatedDomainsCount']
    }
  },

  contextColumnsConfig: endpointColumns.concat(imColumns),
  lcColumnsConfig: liveConnectColumns,
  machineColumns: machineData,
  userColumns: userDetails,

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
      liveConnectErrorMessage: null,
      contextData: {
        incidentsData: null,
        alertsData: null,
        endpointData: null,
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
        set(this, 'hasResponse', true);
        if (!data || data.length === 0) {
          set(this, 'errorMessage', this.get('i18n').t('context.error.error'));
          return;
        }
        Logger.info('pushing data to context model');
        if (isArray(data)) {
          data.forEach((entry) => {
            if (entry.dataSourceGroup) {
              this._populateContextsData(entry);
            } else {
              Logger.error('DataSource group for', entry.dataSourceName, 'is not configured');
            }
          });
        }
      },
      onError: (response) => {
        if (this.hasResponse) {
          return;
        }
        set(this, 'hasResponse', true);
        set(this, 'errorMessage', this.get('i18n').t('context.error.error') + response);
        Logger.error('Error processing stream call for context lookup.', response);
      }
    });
  },
  _displayDataSourceError(contextData) {
    if (contextData.errorMessage) {
      let errorMessage = this.get('i18n').t(`context.error.${contextData.errorMessage}`);
      if (errorMessage.string) {
        if (contextData.errorParameters) {
          errorMessage = errorMessage.string;
          for (const [key, value] of Object.entries(contextData.errorParameters)) {
            errorMessage = errorMessage.replace(`{${key}}`, value);
          }
        }
        contextData.errorMessage = htmlSafe(errorMessage);
      }
      Logger.error('Error processing stream call for context lookup for data source ->', contextData.dataSourceName);
    }
    return contextData;
  },

  _displayTimeRange(contextData) {
    if (contextData.resultMeta && contextData.resultMeta.timeQuerySubmitted) {
      const timeWindow = contextData.resultMeta['timeFilter.timeUnitCount'] +
        contextData.resultMeta['timeFilter.timeUnit'];
      this.get('model').contextData[`${contextData.dataSourceGroup}_LASTUPDATED`] =
          contextData.resultMeta.timeQuerySubmitted;
      set(this.get('model').contextData, `${contextData.dataSourceGroup}_TIMEWINDOW`, timeWindow || 'All Data');
    }
    return contextData;
  },

  _populateContextsData(contextData) {
    contextData = this._displayDataSourceError(contextData);
    if (contextData.errorMessage) {
      set(this.get('model').contextData, `${contextData.dataSourceGroup}_ERROR`, contextData.errorMessage);
      return;
    }
    contextData = this._displayTimeRange(contextData);
    switch (contextData.dataSourceGroup) {
      case 'Modules': {
        set(this.get('model').contextData, 'additionalData', contextData.resultMeta);
        set(this.get('model').contextData, `${contextData.dataSourceGroup}_HEADER`, ` (IIOC Score > ${contextData.resultMeta.iocScore_gte})`);
        set(this.get('model').contextData, contextData.dataSourceGroup, contextData.resultList);
        break;
      }

      case 'LIST': {
        const { dataSourceGroup } = contextData;
        const model = this.get('model');
        const { contextData: allContextData } = model;
        const allSourceTypeData = allContextData[dataSourceGroup] || [];
        set(allContextData, dataSourceGroup, allSourceTypeData.concat(contextData));
        break;
      }

      case 'LiveConnect-File':
      case 'LiveConnect-Ip':
      case 'LiveConnect-Domain':
        if (contextData.failed) {
          set(this.get('model'), 'liveConnectErrorMessage', contextData.errorMessage);
        } else {
          contextData.resultList.forEach((obj) => {
            if (obj && obj.record && obj.record.length > 2) {
              this._parseLiveConnectData(contextData.dataSourceGroup, obj.record);
              this._fetchRelatedEntities(this.liveConnectObj[contextData.dataSourceType].fetchRelatedEntities);
            } else {
              set(this.get('model').contextData, 'liveConnectData', null);
            }
          });
        }
        break;
      default:
        set(this.get('model').contextData, contextData.dataSourceGroup, contextData.resultList);
    }
  },

  _parseLiveConnectData(dataSourceType, record) {
    const lcData = this.get('model').contextData.liveConnectData;
    const entityType = this.liveConnectObj[dataSourceType];
    lcData.set(entityType.info, record[0][entityType.info]);
    lcData.set(entityType.Reputation, record[1][entityType.Reputation]);
    lcData.set(this.liveConnectObj.allTags, record[2].LiveConnectApi.riskTagTypes);
    lcData.set(this.liveConnectObj.allReasons, record[2].LiveConnectApi.riskReasonTypes);
    entityType.relatedEntities_count.forEach((obj)=> {
      set(this.get('model').contextData, obj, record[1][entityType.Reputation][obj]);
    });
  },
  _fetchRelatedEntities(relatedEntities) {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');
    const filter = [];
    if (entityId && entityType) {
      filter.push({ field: 'meta', value: entityType });
      filter.push({ field: 'value', value: entityId });
      filter.push({ field: 'pageNumber', value: 1 }); // hard-coded for now. will change in a later US
      filter.push({ field: 'pageSize', value: 5 }); // hard-coded for now. will change in a later US
      filter.push({ field: 'relatedEntities', value: relatedEntities });
    }
    this.get('request').streamRequest({
      method: 'stream',
      modelName: 'related-entity',
      query: {
        filter
      },
      streamOptions: { requireRequestId: false },
      onResponse: ({ data }) => {
        Logger.info('pushing data to relatedEntity model');
        if (isArray(data)) {
          data.forEach((entry) => {
            this._populateRelatedEntities(entry);
          });
        }
      },
      onError(response) {
        Logger.error('Error processing stream call for context lookup.', response);
      }
    });
  },
  _populateRelatedEntities(relatedEntities) {
    const { resultList } = relatedEntities;
    if (resultList) {
      resultList.forEach((obj) => {
        if (obj && obj.record && obj.record.length) {
          const entityType = this.liveConnectObj[relatedEntities.dataSourceType];
          set(this.get('model').contextData, entityType.relatedEntity, obj.record[0][entityType.relatedEntity][entityType.relatedEntityResponse]);
        }
      });
    }
  },

  actions: {
    activate(option) {
      this.set('active', option);
    },

    closeAction() {
      this.sendAction('closePanel');
      set(this, 'hasResponse', false);
      set(this, 'errorMessage', null);
    }
  },


  getDataSourceLength(data) {
    if (data) {
      return data.length;
    } else {
      return 0;
    }

  }

});
