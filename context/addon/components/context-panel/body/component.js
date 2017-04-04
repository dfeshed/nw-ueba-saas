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

  @computed('meta', 'dataSources', 'activeTabName')
  dataSourceList: (meta, dataSources, activeTabName) => {
    return dataSourceMetaMap.find((dataSource) => {
      return dataSource.tabType === meta;
    }).columns.filter((tab) => {
      return dataSources.includes(tab.dataSourceType) && (activeTabName === 'overview' || activeTabName === tab.dataSourceType);
    }).map((tab) => ({
      ...tab,
      details: dataSourceCoulmns[tab.dataSourceType.toUpperCase()]
    }));
  },

  @computed('activeTabName', 'model.contextData.liveConnectData')
  bodyStyleClass: (activeTabName, liveConnectData) => {
    return activeTabName === 'liveConnect' && liveConnectData ? 'rsa-context-panel__body feedback-margin' : 'rsa-context-panel__body';
  }

});

export default connect(stateToComputed, dispatchToActions)(BodyComponent);
