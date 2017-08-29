import $ from 'jquery';
import Component from 'ember-component';
import computed, { alias } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import ReconPager from 'recon/mixins/recon-pager';
import ReconPanelHelp from 'recon/mixins/recon-panel-help';

import layout from './template';
import baseColumnsConfig from './columns-config';
import {
  filesRetrieved,
  filesWithSelection,
  hasNoFiles,
  hasSelectedFiles,
  hasMultipleSessionFiles
} from 'recon/reducers/files/selectors';
import {
  deselectAllFiles,
  fileSelected,
  selectAllFiles
} from 'recon/actions/interaction-creators';

const stateToComputed = ({ recon, recon: { data, files } }) => ({
  dataIndex: data.index,
  eventTotal: data.total,
  files: filesWithSelection(recon),
  filesRetrieved: filesRetrieved(recon),
  hasNoFiles: hasNoFiles(recon),
  hasSelectedFiles: hasSelectedFiles(recon),
  linkToFileAction: files.linkToFileAction,
  hasMultipleSessionFiles: hasMultipleSessionFiles(recon)
});

const dispatchToActions = {
  fileSelected,
  selectAllFiles,
  deselectAllFiles
};

const calculateColumnWidth = (text) => {
  const canvas = document.createElement('canvas');
  const context = canvas.getContext('2d');
  context.font = "11.9px 'Open Sans'";
  const { width } = context.measureText(text);
  return width;
};

const FileReconComponent = Component.extend(ReconPager, ReconPanelHelp, {
  layout,
  classNames: ['recon-event-detail-files'],
  classNameBindings: ['hasSelectedFiles:warning'],

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
      const longestFieldData = files.reduce((a, b) => a[field].length > b[field].length ? a : b);

      // Calculate width of that longest field
      const calculatedWidth = calculateColumnWidth(longestFieldData[field]);

      // treat the configured width as a minimum to accommodate header
      const width = Math.max(conf.width, calculatedWidth + 5);

      return { ...conf, width };
    });

    this.set('calculatedConfig', calculatedConfig);

    return calculatedConfig;
  },

  @alias('contextualHelp.invFileAnalysis') topic: null,

  actions: {
    toggleAll() {
      if (this.get('allSelected')) {
        this.send('selectAllFiles');
      } else {
        this.send('deselectAllFiles');
      }

      this.toggleProperty('allSelected');
    },

    toggleOne(id, e) {
      // get click events on span and input
      // only want input
      if (e.target.tagName === 'INPUT') {
        this.send('fileSelected', id);
      }
    },

    openLinkedFile(file) {
      const callback = this.get('linkToFileAction');
      if ($.isFunction(callback)) {
        callback(file);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(FileReconComponent);
