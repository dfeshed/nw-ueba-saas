import { connect } from 'ember-redux';
import Component from '@ember/component';
import { fileExportLink, isExportButtonDisabled } from 'investigate-files/reducers/file-list/selectors';
import { exportFileAsCSV } from 'investigate-files/actions/data-creators';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  downloadStatus: state.files.fileList.downloadStatus,
  exportLink: fileExportLink(state),
  isExportButtonDisabled: isExportButtonDisabled(state)
});
const dispatchToActions = {
  exportFileAsCSV
};

const ExportButton = Component.extend({
  tagName: '',
  classNames: ['file-export-button'],

  @computed('isExportButtonDisabled')
  exportTitle(isExportButtonDisabled) {
    return isExportButtonDisabled.isEndpointBroker ? 'investigateFiles.button.brokerExportToCSV' : 'investigateFiles.button.exportToCSV';
  }
});

export default connect(stateToComputed, dispatchToActions)(ExportButton);
