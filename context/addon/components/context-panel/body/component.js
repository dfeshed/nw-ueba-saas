import Ember from 'ember';
import connect from 'ember-redux/components/connect';

import layout from './template';
import * as ContextActions from 'context/actions/context-creators';
import endpointColumns from 'context/config/endpoint-columns';
import imColumns from 'context/config/im-columns';
import machineData from 'context/config/machines';
import userData from 'context/config/users';
import computed from 'ember-computed-decorators';

const {
  Component
} = Ember;

const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  activeTabName: context.activeTabName
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const BodyComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel',
  datasourceList: endpointColumns.concat(imColumns),
  machineData,
  userData,

  @computed('activeTabName', 'model.contextData.liveConnectData')
  bodyStyleClass: (activeTabName, liveConnectData) => {
    return activeTabName === 'liveConnect' && liveConnectData ? 'rsa-context-panel__body feedback-margin' : 'rsa-context-panel__body';
  }
});

export default connect(stateToComputed, dispatchToActions)(BodyComponent);
