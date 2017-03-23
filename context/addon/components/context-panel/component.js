import Ember from 'ember';
import Component from 'ember-component';
import LiveConnect from 'context/config/live-connect';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import * as ContextActions from 'context/actions/context-creators';
import liveConnectObj from 'context/config/liveconnect-response-schema';
import layout from './template';
import service from 'ember-service/inject';

const {
  isArray,
  Logger,
  isEmpty,
  String: {
    htmlSafe
  },
  Object: EmberObject
} = Ember;

const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  lookupData: context.lookupData
});

const dispatchToActions = (dispatch) => ({
  initializeContextPanel: (entityId, entityType) => dispatch(ContextActions.initializeContextPanel(entityId, entityType))
});

const ContextComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel-header',

  i18n: service(),
  request: service(),

  contextData: null,
  entity: null,
  errorMessage: null,
  model: null,

  @computed('lookupData', 'dataSources')
  isReady(lookupData, dataSources) {
    if (!lookupData || !dataSources) {
      return;
    }
    this._initModel();
    this.set('errorMessage', null);

    if (lookupData === 'error') {
      this.set('errorMessage', this.get('i18n').t('context.error.error'));
      Logger.error('Error processing stream call for context lookup. ');
      return true;
    }
    if (isArray(lookupData) && lookupData.length === 0) {
      this.set('errorMessage', this.get('i18n').t('context.error.error'));
      return true;
    }
    Logger.info('pushing data to context model');
    lookupData.forEach((entry) => {
      if (entry.dataSourceGroup) {
        this._populateContextData(entry);
      } else {
        Logger.error('DataSource group for', entry.dataSourceName, 'is not configured');
      }
    });
    this._endOfResponse();
    return true;
  },

  didReceiveAttrs() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');

    // nothing to do unless passed parameters
    if (!entityId || !entityType) {
      return;
    }
    this.send('initializeContextPanel', { entityId, entityType });
  },

  _initModel() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');
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
      let timeWindow = 'All Data';
      const timeCount = contextData.resultMeta['timeFilter.timeUnitCount'];
      if (timeCount) {
        let timeUnitString = contextData.resultMeta['timeFilter.timeUnit'];
        const timeUnit = timeCount > 1 ? `${timeUnitString}S` : `${timeUnitString}`;
        timeUnitString = this.get('i18n').t(`context.timeUnit.${timeUnit}`);
        timeWindow = `${timeCount} ${timeUnitString}`;
      }
      this.get('model').contextData[`${contextData.dataSourceGroup}_LASTUPDATED`] =
          contextData.resultMeta.timeQuerySubmitted;
      this.get('model').contextData[`${contextData.dataSourceGroup}_TIMEWINDOW`] = timeWindow;
    }
    return contextData;
  },

  _populateContextData(contextDatum) {
    const contextData = this.get('model.contextData');

    // Did individual piece of context data have error?
    // store that error so it can be displayed by renderer
    if (contextDatum.errorMessage || contextDatum.failed) {
      Logger.error('Error processing stream call for context lookup for data source ->', contextDatum.dataSourceName);
      this._enrichDataSourceError(contextDatum);
      contextData.set(`${contextDatum.dataSourceGroup}_ERROR`, contextDatum.errorMessage);
      return;
    }

    this._setTimeRangeData(contextDatum);

    switch (contextDatum.dataSourceGroup) {
      case 'Modules': {
        contextData.set('additionalData', contextDatum.resultMeta);
        if (contextDatum.resultMeta.iocScore_gte) {
          contextData.set(`${contextDatum.dataSourceGroup}_HEADER`, ` (IIOC Score > ${contextDatum.resultMeta.iocScore_gte})`);
        }
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
        contextDatum.resultList.forEach((obj) => {
          if (obj && !isEmpty(obj.record)) {
            this._parseLiveConnectData(contextDatum.dataSourceGroup, obj.record);
            this._fetchRelatedEntities(liveConnectObj[contextDatum.dataSourceType].fetchRelatedEntities);
          } else {
            contextData.set('liveConnectData', null);
          }
        });
        break;

      default:
        contextData.set(contextDatum.dataSourceGroup, contextDatum.resultList);
    }
  },

  _endOfResponse() {
    if (!this.get('model.contextData.liveConnectData.allTags')) {
      this.set('model.contextData.liveConnectData', null);
    }
  },

  _parseLiveConnectData(dataSourceType, record) {
    const lcData = this.get('model.contextData.liveConnectData');
    const entityType = liveConnectObj[dataSourceType];
    record.forEach((obj) => {
      if (obj[entityType.info]) {
        this._checkNullForInfo(entityType, obj);
        lcData.set(entityType.info, obj[entityType.info]);
      } else if (obj[entityType.Reputation]) {
        this._checkNullForReputation(entityType, obj);
        lcData.set(entityType.Reputation, obj[entityType.Reputation]);
      } else if (obj.LiveConnectApi) {
        lcData.set(liveConnectObj.allTags, obj.LiveConnectApi.riskTagTypes);
        lcData.set(liveConnectObj.allReasons, obj.LiveConnectApi.riskReasonTypes);
      }
    });
    entityType.relatedEntities_count.forEach((obj) => {
      const reputationObj = record.find((rec) => {
        return !isEmpty(rec[entityType.Reputation]);
      });
      if (reputationObj) {
        this.get('model').contextData[obj] = reputationObj[entityType.Reputation][obj];
      }
    });
  },

  _checkNullForInfo(entityType, obj) {
    entityType.checkNullFields.forEach((field) => {
      if (isEmpty(obj[entityType.info][field])) {
        obj[entityType.info][field] = this.get('i18n').t('context.lc.blankField');
      }
    });
  },

  _checkNullForReputation(entityType, obj) {
    liveConnectObj.reputationCheckNullFields.forEach((field) => {
      if (isEmpty(obj[entityType.Reputation][field])) {
        obj[entityType.Reputation][field] = '0';
      }
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
        Logger.debug('pushing data to relatedEntity model');
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
    closeAction() {
      this.sendAction('closePanel');
      this.set('errorMessage', null);
    }
  }

});
export default connect(stateToComputed, dispatchToActions)(ContextComponent);