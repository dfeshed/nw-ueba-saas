import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import * as InteractionActions from 'recon/actions/interaction-creators';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  files: data.files,
  status: data.fileExtractStatus,
  extractLink: data.fileExtractLink
});

const dispatchToActions = (dispatch) => ({
  downloadFiles: () => dispatch(InteractionActions.downloadFiles())
});

const ExportFilesComponent = Component.extend({
  layout,

  // URL from which to download a completed file extraction job's result
  extractLink: null,

  // Flag for showing/hiding modal during export.
  // Temporarily commenting out until UX gets finalized.
  // showModal: false,

  actions: {
    launchDownload() {
      // this.set('showModal', true);
      this.send('downloadFiles');
    },

    closeModal() {
      this.set('showModal', false);
    }
  },

  // Resolves to `true` only if `status` is 'init' (extract job is being created) or 'wait' (extract job executing).
  @computed('status')
  isExportInProgress(status) {
    return !!((status || '').match(/init|wait/));
  },

  @computed('isExportInProgress', 'files.length')
  isExportDisabled(isExportInProgress, fileCount) {
    return !fileCount || isExportInProgress;
  },

  @computed('isExportInProgress', 'files.length')
  exportCaption(isExportInProgress, fileCount = 0) {
    if (isExportInProgress) {
      return 'Exporting...';
    } else if (fileCount > 1) {
      return `Export Files (${fileCount})`;
    } else {
      return 'Export File';
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ExportFilesComponent);