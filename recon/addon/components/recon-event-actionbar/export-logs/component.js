import Ember from 'ember';
import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';

import { eventType } from 'recon/reducers/meta/selectors';
import * as InteractionActions from 'recon/actions/interaction-creators';
import ReconExport from 'recon/mixins/recon-export';
import layout from './template';

const { String } = Ember;

const stateToComputed = ({ recon, recon: { files } }) => ({
  status: files.fileExtractStatus,
  extractLink: files.fileExtractLink,
  eventType: eventType(recon)
});

const dispatchToActions = (dispatch) => ({
  extractFiles: (type) => dispatch(InteractionActions.extractFiles(type)),
  didDownloadFiles: () => dispatch(InteractionActions.didDownloadFiles())
});

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return String.htmlSafe(`top: ${elRect.height - 1}px`);
  } else {
    return null;
  }
};

const DownloadLogsComponent = Component.extend(ReconExport, {
  layout,

  classNameBindings: ['isExpanded:expanded:collapsed'],

  isExpanded: false,

  offsetsStyle: null,

  @computed('isDownloading', 'i18n')
  caption(isDownloading, i18n) {
    return isDownloading ? i18n.t('recon.textView.isDownloading') :
      i18n.t('recon.textView.downloadLog');
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
