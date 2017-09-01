import Component from 'ember-component';
import { htmlSafe } from 'ember-string';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import {
  isEndpointEvent
} from 'recon/reducers/meta/selectors';
import {
  didDownloadFiles,
  extractFiles
} from 'recon/actions/interaction-creators';

import ReconExport from 'recon/mixins/recon-export';
import layout from './template';

const stateToComputed = ({ recon, recon: { files } }) => ({
  extractLink: files.fileExtractLink,
  isEndpointEvent: isEndpointEvent(recon),
  status: files.fileExtractStatus
});

const dispatchToActions = {
  didDownloadFiles,
  extractFiles
};

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return htmlSafe(`top: ${elRect.height - 1}px`);
  } else {
    return null;
  }
};

const DownloadLogsComponent = Component.extend(ReconExport, {
  layout,
  classNameBindings: ['isExpanded:expanded:collapsed'],
  isExpanded: false,
  offsetsStyle: null,

  @computed('i18n', 'isDownloading', 'defaultOption')
  caption(i18n, isDownloading, defaultOption) {
    return isDownloading ? i18n.t('recon.textView.isDownloading') : defaultOption;
  },

  @computed('i18n', 'isEndpointEvent')
  defaultOption(i18n, isEndpointEvent) {
    return isEndpointEvent ? i18n.t('recon.textView.downloadEndpointEvent') : i18n.t('recon.textView.downloadLog');
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
