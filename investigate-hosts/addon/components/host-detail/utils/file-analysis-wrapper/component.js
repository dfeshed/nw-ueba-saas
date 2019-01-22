import Component from '@ember/component';
import { connect } from 'ember-redux';

import { toggleFileAnalysisView } from 'investigate-shared/actions/data-creators/file-analysis-creators';
import { componentConfig } from 'investigate-hosts/reducers/details/file-analysis/selectors';

import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  componentConfig: componentConfig(state),
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab.toLowerCase(),
  filePropertiesData: state.endpoint.fileAnalysis.filePropertiesData,
  fileData: state.endpoint.fileAnalysis.fileData,
  fileDataLoadingStatus: state.endpoint.fileAnalysis.fileDataLoadingStatus
});

const dispatchToActions = {
  toggleFileAnalysisView
};

const FileAnalysisWrapper = Component.extend({
  classNames: ['file-analysis-wrapper'],
  searchText: '',

  @computed('componentConfig')
  isStringsView({ format }) {
    return format === 'string';
  },

  @computed('activeHostDetailTab')
  backToActiveHostDatailTab(activeHostDetailTab) {
    return `investigateHosts.tabs.${activeHostDetailTab}`;
  },

  actions: {
    closeFileAnalysis() {
      this.set('searchText', '');
      this.send('toggleFileAnalysisView');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileAnalysisWrapper);