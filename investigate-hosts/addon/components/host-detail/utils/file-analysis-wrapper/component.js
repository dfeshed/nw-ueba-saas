import Component from '@ember/component';
import { connect } from 'ember-redux';
import { componentConfig } from 'investigate-hosts/reducers/details/file-analysis/selectors';

import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  componentConfig: componentConfig(state),
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab,
  filePropertiesData: state.endpoint.fileAnalysis.filePropertiesData,
  fileData: state.endpoint.fileAnalysis.fileData,
  fileDataLoadingStatus: state.endpoint.fileAnalysis.fileDataLoadingStatus,
  agentId: state.endpoint.detailsInput.agentId
});

const FileAnalysisWrapper = Component.extend({
  classNames: ['file-analysis-wrapper'],
  searchText: '',

  @computed('componentConfig')
  isStringsView({ format }) {
    return format === 'string';
  },

  @computed('activeHostDetailTab')
  backToActiveHostDatailTab(activeHostDetailTab) {
    const activeTabName = activeHostDetailTab.toLowerCase();
    return `investigateHosts.tabs.${activeTabName}`;
  }
});

export default connect(stateToComputed)(FileAnalysisWrapper);