import Component from '@ember/component';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
import computed, { not, match } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { isEndpointEvent } from 'recon/reducers/meta/selectors';
import {
  didDownloadFiles,
  extractFiles
} from 'recon/actions/interaction-creators';

import layout from './template';

const stateToComputed = ({ recon, recon: { files, visuals } }) => ({
  extractLink: files.fileExtractLink,
  isEndpointEvent: isEndpointEvent(recon),
  status: files.fileExtractStatus,
  defaultLogFormat: visuals.defaultLogFormat,
  isAutoDownloadFile: files.isAutoDownloadFile
});

const dispatchToActions = {
  didDownloadFiles,
  extractFiles
};

const downloadFormat = [{ key: 'TEXT', value: 'downloadText' },
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

const DownloadLogsComponent = Component.extend({

  accessControl: service(),
  layout,
  classNameBindings: ['isExpanded:expanded:collapsed'],
  isExpanded: false,
  offsetsStyle: null,
  downloadFormats: downloadFormat,

  @match('status', /init|wait/)
  isDownloading: false,

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

  @computed('isEndpointEvent', 'isHidden')
  isShowDownload(isEndpointEvent, isHidden) {
    return !isHidden && !isEndpointEvent;
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
    },
    // to execute download on pressing ENTER key in the menu
    handleKeydown(type, e) {
      if (e.keyCode === 13) {
        this.toggleProperty('isExpanded');
        this.send('extractFiles', type);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(DownloadLogsComponent);
