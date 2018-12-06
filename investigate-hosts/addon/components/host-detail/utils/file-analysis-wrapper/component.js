import Component from '@ember/component';
import { connect } from 'ember-redux';

import { toggleFileAnalysisView } from 'investigate-shared/actions/data-creators/file-analysis-creators';
import { componentConfig } from 'investigate-hosts/reducers/details/file-analysis/selectors';

const stateToComputed = (state) => ({
  componentConfig: componentConfig(state),
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab.toLowerCase(),
  filePropertiesData: state.endpoint.fileAnalysis.filePropertiesData,
  fileData: state.endpoint.fileAnalysis.fileData
});

const dispatchToActions = {
  toggleFileAnalysisView
};

const FileAnalysisWrapper = Component.extend({
  classNames: ['file-analysis-wrapper']
});

export default connect(stateToComputed, dispatchToActions)(FileAnalysisWrapper);