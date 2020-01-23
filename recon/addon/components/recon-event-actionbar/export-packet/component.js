import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed, { not, match } from 'ember-computed-decorators';
import { extractFiles, didDownloadFiles } from 'recon/actions/interaction-creators';
import layout from './template';
import { hasPackets, getNetworkDownloadOptions, getDefaultDownloadFormat } from 'recon/reducers/packets/selectors';
import { inject as service } from '@ember/service';

const stateToComputed = ({ recon, recon: { files } }) => ({
  status: files.fileExtractStatus,
  extractLink: files.fileExtractLink,
  hasPackets: hasPackets(recon),
  downloadFormats: getNetworkDownloadOptions(recon),
  defaultDownloadFormat: getDefaultDownloadFormat(recon),
  isAutoDownloadFile: files.isAutoDownloadFile
});

const dispatchToActions = {
  extractFiles,
  didDownloadFiles
};

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return `top: ${elRect.height - 1}px`.htmlSafe();
  } else {
    return null;
  }
};

const DownloadPacketComponent = Component.extend({
  accessControl: service(),
  i18n: service(),

  layout,

  classNameBindings: ['isExpanded:expanded:collapsed'],

  isExpanded: false,

  offsetsStyle: null,

  @match('status', /init|wait/)
  isDownloading: false,

  @computed('isDownloading', 'defaultDownloadFormat', 'i18n.locale')
  caption(isDownloading, defaultDownloadFormat) {
    if (isDownloading) {
      return this.get('i18n').t('recon.packetView.isDownloading');
    }
    return this.get('i18n').t(`recon.packetView.${defaultDownloadFormat.value}`);
  },

  // Default download button will be disabled if the selected default format is disabled or download is in progress
  @computed('isDownloading', 'defaultDownloadFormat')
  isCaptionDisabled(isDownloading, defaultDownloadFormat) {
    if (defaultDownloadFormat.isEnabled && !isDownloading) {
      return false;
    }
    return true;
  },

  @not('accessControl.hasInvestigateContentExportAccess') isHidden: true,

  // if there are no packets, none of the options will be enabled, thus eliminating the need to toggle
  // if the download is in progress, downloading from other options should not be allowed
  @computed('isDownloading', 'hasPackets')
  isToggleDisabled(isDownloading, hasPackets) {
    if (!isDownloading && hasPackets) {
      return false;
    }
    return true;
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

export default connect(stateToComputed, dispatchToActions)(DownloadPacketComponent);
