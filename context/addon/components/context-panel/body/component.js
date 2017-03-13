import Ember from 'ember';
import connect from 'ember-redux/components/connect';

import layout from './template';
import * as ContextActions from 'context/actions/context-creators';
import endpointColumns from 'context/config/endpoint-columns';
import imColumns from 'context/config/im-columns';
import machineData from 'context/config/machines';
import userData from 'context/config/users';
import archerData from 'context/config/archer';
import computed from 'ember-computed-decorators';
import { dataSourceEnabled } from 'context/helpers/data-source-enabled';

const {
  Component,
  set
} = Ember;

const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  activeTabName: context.activeTabName
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const enrichArcherData = (archerDetails, propertyToEnrich, newProperty) => {
  if (archerDetails[propertyToEnrich]) {
    set(archerDetails, newProperty, [].concat(archerDetails[propertyToEnrich]).length);
  }
};

const BodyComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel',
  datasourceList: endpointColumns.concat(imColumns),
  machineData,
  userData,
  archerData,

  @computed('activeTabName', 'model.contextData.liveConnectData')
  bodyStyleClass: (activeTabName, liveConnectData) => {
    return activeTabName === 'liveConnect' && liveConnectData ? 'rsa-context-panel__body feedback-margin' : 'rsa-context-panel__body';
  },

  @computed('activeTabName', 'dataSources')
  archerDataRequired: (activeTabName, dataSources) => {
    if (dataSources) {
      return activeTabName === 'overview' && dataSourceEnabled([dataSources, 'Archer']);
    }
  },

  @computed('contextData.Archer.[]')
  archerLookupData(archerData) {
    if (archerData) {
      archerData.forEach((archerDetails) => {
        enrichArcherData(archerDetails, 'Business Unit', 'businessUnitCount');
        enrichArcherData(archerDetails, 'Facilities', 'facilitiesCount');
        enrichArcherData(archerDetails, 'Device Owner', 'deviceCount');
      });
      return archerData;
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(BodyComponent);
