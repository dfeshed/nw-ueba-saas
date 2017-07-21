import $ from 'jquery';
import { warn, log } from 'ember-debug';
import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import { initializeContextPanel, restoreDefault, getContextEntitiesMetas } from 'context/actions/context-creators';
import liveConnectObj from 'context/config/liveconnect-response-schema';
import layout from './template';
import service from 'ember-service/inject';
import { isEmpty } from 'ember-utils';
import { isEmberArray } from 'ember-array/utils';
import { once, next, schedule, later } from 'ember-runloop';
import EmberObject from 'ember-object';

const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  lookupData: context.lookupData,
  errorMessage: context.errorMessage,
  activeTabName: context.activeTabName
});

const dispatchToActions = {
  initializeContextPanel,
  restoreDefault,
  getContextEntitiesMetas
};

const ContextComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel',

  request: service(),
  eventBus: service(),
  context: service(),

  contextData: null,
  isDisplayed: false,
  model: null,

  @computed('lookupData.[]', 'dataSources', 'errorMessage')
  isReady(lookupData, dataSources, errorMessage) {
    if (errorMessage && errorMessage !== '') {
      return true;
    }
    if (!lookupData || !dataSources) {
      return false;
    }
    this._initModel();
    this._initLCData(lookupData);
    this._endOfResponse();
    return true;
  },


  didReceiveAttrs() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');
    // nothing to do unless passed parameters
    if (!entityId || !entityType) {
      return;
    }
    later(() => {
      once(this, this._initializeContextPanel);
    }, 400);
  },

  _initializeContextPanel() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');
    this.send('initializeContextPanel', { entityId, entityType });

    this.get('context').metas('CORE').then(({ data } = {}) => {
      this.send('getContextEntitiesMetas', { data });
    });
  },

  _initModel() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');
    const contextModels = EmberObject.create({
      lookupKey: entityId,
      meta: entityType,
      contextData: EmberObject.create({
        liveConnectData: EmberObject.create()
      })
    });
    this.set('model', contextModels);
  },

  _initLCData([lookupData]) {
    if (lookupData) {
      [lookupData['LiveConnect-Ip'], lookupData['LiveConnect-File'], lookupData['LiveConnect-Domain']].map(this._parseLCData.bind(this));
    }
  },
  _parseLCData(contextDatum) {
    if (!contextDatum || !contextDatum.resultList) {
      return;
    }
    const contextData = this.get('model.contextData')[contextDatum.dataSourceGroup];
    if (contextData) {
      return;
    }
    contextDatum.resultList.forEach((obj) => {
      if (obj && !isEmpty(obj.record)) {
        this._parseLiveConnectData(contextDatum.dataSourceGroup, obj.record);
        this._fetchRelatedEntities(liveConnectObj[contextDatum.dataSourceType].fetchRelatedEntities);
      } else {
        contextData.set('liveConnectData', null);
      }
    });
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
      onResponse: ({ data }) => {
        log('pushing data to relatedEntity model');
        if (isEmberArray(data)) {
          data.forEach((entry) => {
            this._populateRelatedEntities(entry);
          });
        }
      },
      onError(response) {
        warn('Error processing stream call for context lookup.', response);
      }
    });
  },

  _populateRelatedEntities(relatedEntities) {
    const { resultList } = relatedEntities;
    if (resultList) {
      resultList.forEach((obj) => {
        if (obj && obj.record && obj.record.length) {
          const entityType = liveConnectObj[relatedEntities.dataSourceType];
          const contextData = this.get('model.contextData');
          const contextDataForDS = contextData[entityType.relatedEntity] || {};
          contextDataForDS.resultList = obj.record[0][entityType.relatedEntity][entityType.relatedEntityResponse];
          contextData.set(entityType.relatedEntity, contextDataForDS);
        }
      });
    }
  },
  _needToClosePanel(target) {
    return !this.get('isDisplayed') && !$(target).closest('.rsa-context-panel').length > 0;
  },
  _closeContextPanel(target) {
    next(() => {
      if (this._needToClosePanel(target)) {
        this.sendAction('closePanel');
        this.send('restoreDefault');
      }
    });
  },
  mouseEnter() {
    this.set('isDisplayed', true);
  },
  mouseLeave() {
    this.set('isDisplayed', false);
  },

  didInsertElement() {
    schedule('afterRender', () => {
      this.get('eventBus').on('rsa-application-click', (target) => {
        this._closeContextPanel(target);
      });
      this.get('eventBus').on('rsa-application-header-click', (target) => {
        this._closeContextPanel(target);
      });
    });
  }

});
export default connect(stateToComputed, dispatchToActions)(ContextComponent);
