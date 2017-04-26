import $ from 'jquery';
import Component from 'ember-component';
import computed, { empty, filterBy, gt } from 'ember-computed-decorators';
import ReconPager from 'recon/mixins/recon-pager';
import connect from 'ember-redux/components/connect';

import layout from './template';
import baseColumnsConfig from './columns-config';
import { filesWithSelection } from 'recon/reducers/files/selectors';
import {
  fileSelected,
  selectAllFiles,
  deselectAllFiles
} from 'recon/actions/interaction-creators';

const stateToComputed = ({ recon, recon: { data, files } }) => ({
  files: filesWithSelection(recon),
  linkToFileAction: files.linkToFileAction,
  eventTotal: data.total,
  dataIndex: data.index
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

const FileReconComponent = Component.extend(ReconPager, {
  layout,
  classNames: ['recon-event-detail-files'],

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

    let calculatedConfig = baseColumnsConfig.map((conf) => {
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
    // Calculate the sum of widths of cells
    const sumWidths = calculatedConfig.reduce((p, { width }) => {
      return p + width;
    }, 0);

    // Convert the widths to relative percentages
    calculatedConfig = calculatedConfig.map((conf) => {
      const width = `${(conf.width / sumWidths) * 100}%`;
      return { ...conf, width };
    });

    this.set('calculatedConfig', calculatedConfig);

    return calculatedConfig;
  },

  @empty('files') noFiles: null,

  @filterBy('files', 'type', 'session') sessionFiles: null,

  @gt('sessionFiles.length', 1) hasMultipleSessionFiles: null,

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
