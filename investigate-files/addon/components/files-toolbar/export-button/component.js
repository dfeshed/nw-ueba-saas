import { connect } from 'ember-redux';
import Component from '@ember/component';
import { fileExportLink, hasFiles } from 'investigate-files/reducers/file-list/selectors';
import { exportFileAsCSV } from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  downloadStatus: state.files.fileList.downloadStatus,
  exportLink: fileExportLink(state),
  isExportButtonDisabled: !hasFiles(state)
});
const dispatchToActions = {
  exportFileAsCSV
};

const ExportButton = Component.extend({
  tagName: '',
  classNames: ['file-export-button']
});

export default connect(stateToComputed, dispatchToActions)(ExportButton);
