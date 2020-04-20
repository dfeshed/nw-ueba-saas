import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { componentConfig } from 'investigate-hosts/reducers/details/file-analysis/selectors';

const stateToComputed = (state) => ({
  componentConfig: componentConfig(state),
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab,
  filePropertiesData: state.endpoint.fileAnalysis.filePropertiesData,
  fileData: state.endpoint.fileAnalysis.fileData,
  fileDataLoadingStatus: state.endpoint.fileAnalysis.fileDataLoadingStatus,
  agentId: state.endpoint.detailsInput.agentId
});

@classic
@classNames('file-analysis-wrapper')
class FileAnalysisWrapper extends Component {
  searchText = '';

  @computed('componentConfig')
  get isStringsView() {
    return this.componentConfig.format === 'string';
  }

  @computed('activeHostDetailTab')
  get backToActiveHostDatailTab() {
    const activeTabName = this.activeHostDetailTab.toLowerCase();
    return `investigateHosts.tabs.${activeTabName}`;
  }
}

export default connect(stateToComputed)(FileAnalysisWrapper);