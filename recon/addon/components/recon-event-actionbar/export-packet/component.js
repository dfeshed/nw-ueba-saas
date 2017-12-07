import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed, { not } from 'ember-computed-decorators';
import { extractFiles, didDownloadFiles } from 'recon/actions/interaction-creators';
import ReconExport from 'recon/mixins/recon-export';
import layout from './template';
import { hasPayload } from 'recon/reducers/packets/selectors';
import service from 'ember-service/inject';

const stateToComputed = ({ recon, recon: { files, visuals } }) => ({
  status: files.fileExtractStatus,
  extractLink: files.fileExtractLink,
  hasPayload: hasPayload(recon),
  defaultPacketFormat: visuals.defaultPacketFormat,
  isAutoDownloadFile: files.isAutoDownloadFile
});

const dispatchToActions = {
  extractFiles,
  didDownloadFiles
};

const downloadFormat = [{ key: 'PCAP', value: 'downloadPCAP' },
  { key: 'PAYLOAD', value: 'downloadPayload' },
  { key: 'PAYLOAD1', value: 'downloadPayload1' },
  { key: 'PAYLOAD2', value: 'downloadPayload2' } ];

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return `top: ${elRect.height - 1}px`.htmlSafe();
  } else {
    return null;
  }
};

const DownloadPacketComponent = Component.extend(ReconExport, {
  accessControl: service(),
  layout,

  classNameBindings: ['isExpanded:expanded:collapsed'],

  isExpanded: false,

  offsetsStyle: null,

  downloadFormats: downloadFormat,

  @computed('isDownloading', 'defaultPacketFormat')
  caption(isDownloading, defaultPacketFormat) {
    if (isDownloading) {
      return this.get('i18n').t('recon.packetView.isDownloading');
    }
    const packetFormat = downloadFormat.find((x) => x.key === defaultPacketFormat);
    return this.get('i18n').t(`recon.packetView.${packetFormat.value}`);
  },

  @computed('hasPayload', 'isDownloading')
  isDisabled(hasPayload, isDownloading) {
    if (hasPayload && !isDownloading) {
      return false;
    }
    return true;
  },

  @not('accessControl.hasInvestigateContentExportAccess') isHidden: true,

  @computed('isDisabled')
  isPayloadDisabled(isDisabled) {
    if (isDisabled) {
      return 'disabled';
    }
    return 'enabled';
  },
  actions: {
    processDefault(type) {
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

export default connect(stateToComputed, dispatchToActions)(DownloadPacketComponent);
