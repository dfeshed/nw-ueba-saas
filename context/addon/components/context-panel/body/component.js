import { connect } from 'ember-redux';
import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';


const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  activeTabName: context.activeTabName,
  meta: context.meta,
  lookupData: context.lookupData
});

const BodyComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__body',

  @computed('dataSources', 'activeTabName')
  dataSourceList(dataSources, activeTabName) {
    return dataSources.filter((dataSource) => {
      return activeTabName === dataSource.dataSourceType;
    });
  },

  @computed('activeTabName', 'model.contextData.liveConnectData')
  bodyStyleClass: (activeTabName, liveConnectData) => {
    return activeTabName === 'liveConnect' && liveConnectData ? 'rsa-context-panel__body feedback-margin' : 'rsa-context-panel__body';
  }

});

export default connect(stateToComputed)(BodyComponent);
