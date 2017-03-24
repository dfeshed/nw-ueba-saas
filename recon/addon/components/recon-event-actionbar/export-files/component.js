import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed, { filterBy } from 'ember-computed-decorators';
import * as InteractionActions from 'recon/actions/interaction-creators';
import ReconExport from 'recon/mixins/recon-export';
import { isLogEvent } from 'recon/selectors/event-type-selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { data } }) => ({
  files: data.files,
  status: data.fileExtractStatus,
  extractLink: data.fileExtractLink,
  eventType: data.eventType,
  isLogEvent: isLogEvent(recon)
});

const dispatchToActions = (dispatch) => ({
  extractFiles: () => dispatch(InteractionActions.extractFiles('FILES')),
  didDownloadFiles: () => dispatch(InteractionActions.didDownloadFiles())
});

const ExportFilesComponent = Component.extend(ReconExport, {
  layout,

  @computed('isDownloading', 'selectedFiles.length')
  caption(isDownloading, count) {
    if (isDownloading) {
      return 'Exporting...';
    } else {
      return (count > 1) ? `Export Files (${count})` : 'Export File';
    }
  },

  @filterBy('files', 'selected', true)
  selectedFiles: [],

  @computed('isDownloading', 'selectedFiles.length')
  isDisabled(isDownloading, count) {
    return !count || isDownloading;
  }
});

export default connect(stateToComputed, dispatchToActions)(ExportFilesComponent);
