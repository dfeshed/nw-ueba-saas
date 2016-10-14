import Ember from 'ember';
import connect from 'ember-redux/components/connect';

import * as InteractionActions from 'recon/actions/interaction-creators';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  files: data.files
});

const dispatchToActions = (dispatch) => ({
  downloadFiles: () => dispatch(InteractionActions.downloadFiles())
});

const ExportFilesComponent = Component.extend({
  layout,
  showModal: false,

  actions: {
    launchDownload() {
      if (this.get('files').findBy('selected')) {
        this.set('showModal', true);
        // this.send('downloadFiles');
      } else {
        // doing nothing
      }
    },

    closeModal() {
      this.set('showModal', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ExportFilesComponent);