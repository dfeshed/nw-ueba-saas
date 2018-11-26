import Component from '@ember/component';
import { connect } from 'ember-redux';

import { toggleFileAnalysisView } from 'investigate-hosts/actions/data-creators/file-analysis';
import { componentConfig } from 'investigate-hosts/reducers/details/file-analysis/selectors';

const stateToComputed = (state) => ({
  componentConfig: componentConfig(state),
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab
});

const dispatchToActions = {
  toggleFileAnalysisView
};

const FileAnalysisWrapper = Component.extend({
  classNames: ['file-analysis-wrapper']
});

export default connect(stateToComputed, dispatchToActions)(FileAnalysisWrapper);