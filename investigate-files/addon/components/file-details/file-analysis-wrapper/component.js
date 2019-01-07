import Component from '@ember/component';
import { connect } from 'ember-redux';
import { componentConfig } from 'investigate-files/reducers/file-analysis/selectors';

import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  componentConfig: componentConfig(state),
  filePropertiesData: state.files.fileAnalysis.filePropertiesData,
  fileData: state.files.fileAnalysis.fileData,
  fileDataLoadingStatus: state.files.fileAnalysis.fileDataLoadingStatus
});


const FileAnalysisWrapper = Component.extend({
  classNames: ['global-files', 'file-analysis-wrapper'],
  searchText: '',
  tagName: 'box',

  @computed('componentConfig')
  isStringsView({ format }) {
    return format === 'string';
  }

});

export default connect(stateToComputed)(FileAnalysisWrapper);