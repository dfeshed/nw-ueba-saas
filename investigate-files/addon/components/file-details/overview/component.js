import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import CONFIG from '../base-property-config';

import {
  setSelectedAlert,
  getUpdatedRiskScoreContext,
  expandEvent
} from 'investigate-shared/actions/data-creators/risk-creators';

import { riskState, getDataSourceTab } from 'investigate-files/reducers/visuals/selectors';

import { setDataSourceTab } from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  risk: riskState(state),
  getDataSourceTab: getDataSourceTab(state),
  activeDataSourceTab: state.files.visuals.activeDataSourceTab,
  fileProperty: state.files.fileList.selectedDetailFile

});

const dispatchToActions = {
  getUpdatedRiskScoreContext,
  setSelectedAlert,
  expandEvent,
  setDataSourceTab
};

const Overview = Component.extend({
  tagName: 'box',

  classNames: ['file-overview'],

  propertyConfig: CONFIG,

  @computed('getDataSourceTab')
  dataSourceTabs(tabs) {
    return tabs.filter((tab) => tab.name !== 'RISK_PROPERTIES');
  }
});

export default connect(stateToComputed, dispatchToActions)(Overview);
