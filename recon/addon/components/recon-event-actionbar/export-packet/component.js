import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import * as InteractionActions from 'recon/actions/interaction-creators';
import ReconExport from 'recon/mixins/recon-export';
import layout from './template';
import { hasPayload } from 'recon/selectors/packet-selectors';

const stateToComputed = ({ recon, recon: { data } }) => ({
  status: data.fileExtractStatus,
  extractLink: data.fileExtractLink,
  hasPayload: hasPayload(recon)
});

const dispatchToActions = (dispatch) => ({
  extractFiles: (type) => dispatch(InteractionActions.extractFiles(type)),
  didDownloadFiles: () => dispatch(InteractionActions.didDownloadFiles())
});

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return `top: ${elRect.height - 1}px`.htmlSafe();
  } else {
    return null;
  }
};

const DownloadPacketComponent = Component.extend(ReconExport, {
  layout,

  classNameBindings: ['isExpanded:expanded:collapsed'],

  isExpanded: false,

  offsetsStyle: null,

  @computed('isDownloading')
  caption(isDownloading) {
    return isDownloading ? this.get('i18n').t('recon.packetView.isDownloading') :
      this.get('i18n').t('recon.packetView.defaultDownloadPCAP');
  },

  @computed('hasPayload', 'isDownloading')
  isDisabled(hasPayload, isDownloading) {
    if (hasPayload && !isDownloading) {
      return false;
    }
    return true;
  },
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