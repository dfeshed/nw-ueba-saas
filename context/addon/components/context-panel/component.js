import Ember from 'ember';
import LiveConnect from 'context/config/live-connect';
import layout from './template';

const {
  inject: {
    service
  },
  isArray,
  run,
  Component,
  Logger,
  String: {
    htmlSafe
  },
  Object: EmberObject
} = Ember;

const liveConnectObj = {
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
};

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel-header',

  request: service(),

  active: 'overview',
  contextData: null,
  entity: null,
  errorMessage: null,
  hasResponse: false,
  model: null,

  didReceiveAttrs() {
    run.once(this, this._initModel);
  },

  _initModel() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');

    // nothing to do unless passed parameters
    if (!entityId || !entityType) {
      return;
    }

    const contextModels = EmberObject.create({
      displayContextPanel: true,
      lookupKey: entityId,
      meta: entityType,
      ips: [],
      prefetch: [],
      liveConnectErrorMessage: null,
      contextData: EmberObject.create({
        incidentsData: null,
        alertsData: null,
        endpointData: null,
        liveConnectData: LiveConnect.create()
      })
    });

    this.set('model', contextModels);
    this._doCHLookup(entityId, entityType);
  },

  /*
   * Lookup the context data for the given key and meta.
   * @private
   */
  _doCHLookup(lookupKey, meta) {
    Logger.info('fetching context data for lookup key ', lookupKey);

    this.get('request').streamRequest({
      method: 'stream',
      modelName: 'context',
      query: {
        filter: [
          { field: 'meta', value: meta },
          { field: 'value', value: lookupKey }
        ]
      },
      streamOptions: { requireRequestId: false },
      onResponse: ({ data }) => {
        this.set('hasResponse', true);
        if (!data || data.length === 0) {
          this.set('errorMessage', this.get('i18n').t('context.error.error'));
          return;
        }
        if (isArray(data)) {
          Logger.info('pushing data to context model');
          data.forEach((entry) => {
            if (entry.dataSourceGroup) {
              this._populateContextData(entry);
            } else {
              Logger.error('DataSource group for', entry.dataSourceName, 'is not configured');
            }
          });
        }
      },
      onError: (response) => {
        if (this.get('hasResponse') === true) {
          return;
        }
        this.set('hasResponse', true);
        this.set('errorMessage', this.get('i18n').t('context.error.error') + response);
        Logger.error('Error processing stream call for context lookup.', response);
      }
    });
  },

  _enrichDataSourceError(contextDatum) {
    let errorMessage = this.get('i18n').t(`context.error.${contextDatum.errorMessage}`);
    if (errorMessage.string) {
      if (contextDatum.errorParameters) {
        errorMessage = errorMessage.string;
        for (const [key, value] of Object.entries(contextDatum.errorParameters)) {
          errorMessage = errorMessage.replace(`{${key}}`, value);
        }
      }
      contextDatum.errorMessage = htmlSafe(errorMessage);
    }
  },

  _setTimeRangeData(contextData) {
    if (contextData.resultMeta && contextData.resultMeta.timeQuerySubmitted) {
      const timeWindow = contextData.resultMeta['timeFilter.timeUnitCount'] +
        contextData.resultMeta['timeFilter.timeUnit'];
      this.get('model').contextData[`${contextData.dataSourceGroup}_LASTUPDATED`] =
          contextData.resultMeta.timeQuerySubmitted;
      this.get('model').contextData[`${contextData.dataSourceGroup}_TIMEWINDOW`] = timeWindow || 'All Data';
    }
    return contextData;
  },

  _populateContextData(contextDatum) {
    const contextData = this.get('model.contextData');

    // Did individual piece of context data have error?
    // store that error so it can be displayed by renderer
    if (contextDatum.errorMessage) {
      Logger.error('Error processing stream call for context lookup for data source ->', contextDatum.dataSourceName);
      this._enrichDataSourceError(contextDatum);
      contextData.set(`${contextDatum.dataSourceGroup}_ERROR`, contextDatum.errorMessage);
      return;
    }

    this._setTimeRangeData(contextDatum);

    switch (contextDatum.dataSourceGroup) {
      case 'Modules': {
        contextData.set('additionalData', contextDatum.resultMeta);
        contextData.set(`${contextDatum.dataSourceGroup}_HEADER`, ` (IIOC Score > ${contextDatum.resultMeta.iocScore_gte})`);
        contextData.set(contextDatum.dataSourceGroup, contextDatum.resultList);
        break;
      }

      case 'LIST': {
        contextData.set('LIST', (contextData.get('LIST') || []).concat(contextDatum));
        break;
      }

      case 'LiveConnect-File':
      case 'LiveConnect-Ip':
      case 'LiveConnect-Domain':
        if (contextDatum.failed) {
          this.set('model.liveConnectErrorMessage', contextDatum.errorMessage);
        } else {
          contextDatum.resultList.forEach((obj) => {
            if (obj && obj.record && obj.record.length > 2) {
              this._parseLiveConnectData(contextDatum.dataSourceGroup, obj.record);
              this._fetchRelatedEntities(liveConnectObj[contextDatum.dataSourceType].fetchRelatedEntities);
            } else {
              contextData.set('liveConnectData', null);
            }
          });
        }
        break;

      default:
        contextData.set(contextDatum.dataSourceGroup, contextDatum.resultList);
    }
  },

  _parseLiveConnectData(dataSourceType, record) {
    const lcData = this.get('model.contextData.liveConnectData');
    const entityType = liveConnectObj[dataSourceType];
    lcData.set(entityType.info, record[0][entityType.info]);
    lcData.set(entityType.Reputation, record[1][entityType.Reputation]);
    lcData.set(liveConnectObj.allTags, record[2].LiveConnectApi.riskTagTypes);
    lcData.set(liveConnectObj.allReasons, record[2].LiveConnectApi.riskReasonTypes);
    entityType.relatedEntities_count.forEach((obj)=> {
      this.get('model').contextData[obj] = record[1][entityType.Reputation][obj];
    });
  },

  _fetchRelatedEntities(relatedEntities) {
    this.get('request').streamRequest({
      method: 'stream',
      modelName: 'related-entity',
      query: {
        filter: [
          { field: 'meta', value: this.get('entityType') },
          { field: 'value', value: this.get('entityId') },
          { field: 'pageNumber', value: 1 }, // hard-coded for now. will change in a later US
          { field: 'pageSize', value: 5 }, // hard-coded for now. will change in a later US
          { field: 'relatedEntities', value: relatedEntities }
        ]
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
          const entityType = liveConnectObj[relatedEntities.dataSourceType];
          this.get('model').contextData[entityType.relatedEntity] =
            obj.record[0][entityType.relatedEntity][entityType.relatedEntityResponse];
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
      this.set('hasResponse', false);
      this.set('errorMessage', null);
    }
  }

});