import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';
import baseColumnsConfig from './columns-config';

const { Component } = Ember;

const stateToComputed = ({ data }) => ({
  files: data.files
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

  @computed('files')
  columnsConfig(files) {
    if (!files) {
      return [];
    }

    return baseColumnsConfig.map((conf) => {
      const { field } = conf;

      // find longest string in data for this field
      const longestFieldData = files.reduce((a, b) => a[field].length > b[field].length ? a[field] : b[field]);

      // Calculate width of that longest field
      const calculatedWidth = calculateColumnWidth(longestFieldData[field]);

      // treat the configured width as a minimum to accommodate header
      const width = Math.max(conf.width, calculatedWidth + 5);

      return { ...conf, width };
    });
  }
});

export default connect(stateToComputed)(FileReconComponent);