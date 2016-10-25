import Ember from 'ember';
import computed, { empty, gt } from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';
import baseColumnsConfig from './columns-config';
import * as InteractionActions from 'recon/actions/interaction-creators';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  files: data.files
});

const dispatchToActions = (dispatch) => ({
  fileSelectionToggled: (fileId) => dispatch(InteractionActions.fileSelected(fileId)),
  selectAllFiles: () => dispatch(InteractionActions.selectAllFiles()),
  deselectAllFiles: () => dispatch(InteractionActions.deselectAllFiles())
});

const calculateColumnWidth = (text) => {
  const canvas = document.createElement('canvas');
  const context = canvas.getContext('2d');
  context.font = "11.9px 'Open Sans'";
  const { width } = context.measureText(text);
  return width;
};

const FileReconComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-detail-files'],

  allSelected: false,
  calculatedConfig: null,

  @computed('files')
  columnsConfig(files) {
    if (!files) {
      return [];
    }

    // if already calculated, don't calc again
    // as it doesn't change when files change.
    // Component starts over with new set of files
    if (this.get('calculatedConfig')) {
      return this.get('calculatedConfig');
    }

    const calculatedConfig = baseColumnsConfig.map((conf) => {
      const { field } = conf;

      // first column has empty field, not calculating anything for it
      if (field === '') {
        return conf;
      }

      // find longest string in data for this field
      const longestFieldData = files.reduce((a, b) => a[field].length > b[field].length ? a[field] : b[field]);

      // Calculate width of that longest field
      const calculatedWidth = calculateColumnWidth(longestFieldData[field]);

      // treat the configured width as a minimum to accommodate header
      const width = Math.max(conf.width, calculatedWidth + 5);

      return { ...conf, width };
    });

    this.set('calculatedConfig', calculatedConfig);

    return calculatedConfig;
  },

  @empty('files') noFiles: null,

  @gt('files.length', 1) hasMultipleFiles: null,

  actions: {
    toggleAll(e) {
      // get click events on span and input
      // only want input
      if (e.target.tagName === 'INPUT') {
        if (this.get('allSelected')) {
          this.send('selectAllFiles');
        } else {
          this.send('deselectAllFiles');
        }

        this.toggleProperty('allSelected');
      }
    },

    toggleOne(id, e) {
      // get click events on span and input
      // only want input
      if (e.target.tagName === 'INPUT') {
        this.send('fileSelectionToggled', id);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(FileReconComponent);