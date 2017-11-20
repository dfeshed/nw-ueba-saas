import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';

import { extractFiles, didDownloadFiles } from 'recon/actions/interaction-creators';
import { selectedFiles } from 'recon/reducers/files/selectors';
import ReconExport from 'recon/mixins/recon-export';
import layout from './template';

const stateToComputed = ({ recon, recon: { files } }) => ({
  selectedFiles: selectedFiles(recon),
  extractLink: files.fileExtractLink,
  status: files.fileExtractStatus,
  isAutoDownloadFile: files.isAutoDownloadFile
});

const dispatchToActions = {
  extractFiles: () => extractFiles('FILES'),
  didDownloadFiles
};

const ExportFilesComponent = Component.extend(ReconExport, {
  accessControl: service(),
  layout,

  @computed('isDownloading', 'selectedFiles.length')
  caption(isDownloading, count) {
    if (isDownloading) {
      return this.get('i18n').t('recon.fileView.isDownloading');
    } else {
      return (count > 1) ? this.get('i18n').t('recon.fileView.downloadFiles', { 'fileCount': count }) : this.get('i18n').t('recon.fileView.downloadFile');
    }
  },

  @computed('isDownloading', 'selectedFiles.length', 'accessControl.hasInvestigateContentExportAccess')
  isDisabled(isDownloading, count, hasInvestigateContentExportAccess) {
    return !count || isDownloading || !hasInvestigateContentExportAccess;
  }
});

export default connect(stateToComputed, dispatchToActions)(ExportFilesComponent);
