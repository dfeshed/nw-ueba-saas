import Component from '@ember/component';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
import computed, { not } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import {
  didDownloadFiles,
  extractFiles
} from 'recon/actions/interaction-creators';

import ReconExport from 'recon/mixins/recon-export';
import layout from './template';

const stateToComputed = ({ recon: { files, visuals } }) => ({
  extractLink: files.fileExtractLink,
  status: files.fileExtractStatus,
  defaultLogFormat: visuals.defaultLogFormat,
  isAutoDownloadFile: files.isAutoDownloadFile
});

const dispatchToActions = {
  didDownloadFiles,
  extractFiles
};

const downloadFormat = [{ key: 'LOG', value: 'downloadLog' },
  { key: 'CSV', value: 'downloadCsv' },
  { key: 'XML', value: 'downloadXml' },
  { key: 'JSON', value: 'downloadJson' } ];

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return htmlSafe(`top: ${elRect.height - 1}px`);
  } else {
    return null;
  }
};

const DownloadLogsComponent = Component.extend(ReconExport, {

  accessControl: service(),
  layout,
  classNameBindings: ['isExpanded:expanded:collapsed'],
  isExpanded: false,
  offsetsStyle: null,
  downloadFormats: downloadFormat,

  @computed('isDownloading')
  isDisabled(isDownloading) {
    return isDownloading;
  },

  @not('accessControl.hasInvestigateContentExportAccess') isHidden: true,

  @computed('i18n', 'isDownloading', 'defaultLogFormat')
  caption(i18n, isDownloading, defaultLogFormat) {
    if (isDownloading) {
      return i18n.t('recon.textView.isDownloading');
    }
    const logFormat = downloadFormat.find((x) => x.key === defaultLogFormat);
    return i18n.t(`recon.textView.${logFormat.value}`);
  },

  actions: {
    processFiles(type) {
      if (this.get('isExpanded')) {
        this.toggleProperty('isExpanded');
      }
      this.send('extractFiles', type);
    },
    processMenuItem(type) {
      this.toggleProperty('isExpanded');
      this.send('extractFiles', type);
    },
    clickOutside() {
      if (this.get('isExpanded')) {
        this.toggleProperty('isExpanded');
      }
    },
    toggleExpand() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
      this.toggleProperty('isExpanded');
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(DownloadLogsComponent);
