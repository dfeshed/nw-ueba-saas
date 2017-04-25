import connect from 'ember-redux/components/connect';
import Component from 'ember-component';
import layout from './template';
import * as ContextActions from 'context/actions/context-creators';
import computed from 'ember-computed-decorators';


const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  activeTabName: context.activeTabName,
  meta: context.meta,
  lookupData: context.lookupData
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const BodyComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__body',

  @computed('dataSources', 'activeTabName')
  dataSourceList(dataSources, activeTabName) {
    return dataSources.filter((dataSource) => {
      return (activeTabName === 'overview' || activeTabName === dataSource. dataSourceType) && dataSource.isConfigured;
    });
  },

  _needToRender(activeTabName, lookupData, dataSourceType) {
    return activeTabName === 'overview' || activeTabName === dataSourceType;
  },

  // List of dataSources with no data in localized string format
  @computed('dataSources', 'lookupData.[]')
  dataSourcesWithNoData(dataSources, [lookupData]) {
    if (!lookupData) {
      return;
    }
    const i18n = this.get('i18n');
    return dataSources.filter((dataSource) => {
      const dataSourceObject = lookupData[dataSource.dataSourceType];
      return dataSourceObject && dataSourceObject.resultList.length === 0;
    }).map(function(val) {
      return i18n.t(val.title);
    });
  },

  @computed('dataSources')
  dataSourcesNotConfigured(dataSources) {
    const i18n = this.get('i18n');
    return dataSources.filter((dataSource) => {
      return dataSource.dataSourceType !== 'overview' && !dataSource.isConfigured;
    }).map(function(val) {
      return i18n.t(val.title);
    });
  },

  @computed('activeTabName', 'model.contextData.liveConnectData')
  bodyStyleClass: (activeTabName, liveConnectData) => {
    return activeTabName === 'liveConnect' && liveConnectData ? 'rsa-context-panel__body feedback-margin' : 'rsa-context-panel__body';
  },

  @computed('activeTabName', 'dataSourcesWithNoData')
  needToRenderNoDs: (activeTabName, dataSourcesWithNoData) => {
    return activeTabName === 'overview' && dataSourcesWithNoData;
  },

  @computed('activeTabName', 'dataSourcesNotConfigured')
  needToRenderNoDsConfigured: (activeTabName, dataSourcesNotConfigured) => {
    return activeTabName === 'overview' && dataSourcesNotConfigured;
  }

});

export default connect(stateToComputed, dispatchToActions)(BodyComponent);
