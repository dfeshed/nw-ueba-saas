import DetailBase from '../base/component';
import layout from './template';
import baseColumnsConfig from './columns-config';

const calculateColumnWidth = (text) => {
  const canvas = document.createElement('canvas');
  const context = canvas.getContext('2d');
  context.font = "11.9px 'Open Sans'";
  const { width } = context.measureText(text);
  return width;
};

export default DetailBase.extend({
  layout,
  classNameBindings: [':recon-event-detail-files'],

  columnsConfig: null,

  processData({ data }) {
    const effectiveColumnsConfig = baseColumnsConfig.map((conf) => {
      const { field } = conf;

      // find longest string in data for this field
      const longestFieldData = data.reduce((a, b) => a[field].length > b[field].length ? a[field] : b[field]);

      // Calculate width of that longest field
      const calculatedWidth = calculateColumnWidth(longestFieldData[field]);

      // treat the configured width as a minimum to accommodate header
      const width = Math.max(conf.width, calculatedWidth + 5);

      return { ...conf, width };
    });

    this.setProperties({
      reconData: data,
      columnsConfig: effectiveColumnsConfig
    });
  },

  retrieveData(query) {
    this.get('request').promiseRequest({
      method: 'query',
      modelName: 'reconstruction-file-data',
      query
    })
    .then(this.processData.bind(this))
    .catch(this.handleError.bind(this));
  }
});