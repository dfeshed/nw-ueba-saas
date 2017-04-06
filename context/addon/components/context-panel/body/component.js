import connect from 'ember-redux/components/connect';
import Component from 'ember-component';
import layout from './template';
import * as ContextActions from 'context/actions/context-creators';
import dataSourceCoulmns from 'context/config/data-sources';
import computed from 'ember-computed-decorators';
import dataSourceMetaMap from 'context/config/dynamic-tab';


const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  activeTabName: context.activeTabName,
  meta: context.meta
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const BodyComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel',

  @computed('meta', 'dataSources', 'activeTabName', 'contextData')
  dataSourceList(meta, dataSources, activeTabName, contextData) {
    return dataSourceMetaMap.find((dataSource) => {
      return dataSource.tabType === meta;
    }).columns.filter((tab) => {
      return dataSources.includes(tab.dataSourceType) && this._needToRender(activeTabName, contextData, tab.dataSourceType);
    }).map((tab) => ({
      ...tab,
      details: dataSourceCoulmns[tab.dataSourceType.toUpperCase()]
    }));
  },

  _needToRender(activeTabName, contextData, dataSourceType) {
    let hasData = false;
    if (dataSourceType.includes('LiveConnect')) {
      hasData = contextData.get('liveConnectData');
    } else {
      hasData = contextData.get(dataSourceType) && contextData.get(dataSourceType).data.length > 0;
    }
    return (activeTabName === 'overview' && hasData) || activeTabName === dataSourceType;
  },

  // List of dataSources with no data in localized string format
  @computed('meta', 'dataSources', 'contextData')
  dataSourcesWithNoData(meta, dataSources, contextData) {
    const i18n = this.get('i18n');
    return dataSourceMetaMap.find((dataSource) => {
      return dataSource.tabType === meta;
    }).columns.filter((tab) => {
      const filterDataSource = dataSources.includes(tab.dataSourceType);
      const dataSourceObject = contextData.get(tab.dataSourceType);
      if (tab.dataSourceType.includes('LiveConnect')) {
        return filterDataSource && !contextData.get('liveConnectData');
      } else {
        return filterDataSource && dataSourceObject && dataSourceObject.data.length === 0;
      }
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
  }

});

export default connect(stateToComputed, dispatchToActions)(BodyComponent);
