import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { componentConfig } from 'investigate-files/reducers/file-analysis/selectors';

const stateToComputed = (state) => ({
  componentConfig: componentConfig(state),
  filePropertiesData: state.files.fileAnalysis.filePropertiesData,
  fileData: state.files.fileAnalysis.fileData,
  fileDataLoadingStatus: state.files.fileAnalysis.fileDataLoadingStatus
});


@classic
@classNames('global-files', 'file-analysis-wrapper')
@tagName('box')
class FileAnalysisWrapper extends Component {
  searchText = '';

  @computed('componentConfig')
  get isStringsView() {
    return this.componentConfig.format === 'string';
  }
}

export default connect(stateToComputed)(FileAnalysisWrapper);