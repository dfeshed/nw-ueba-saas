import DetailBase from '../base/component';
import layout from './template';
import columnsConfig from './columns-config';

export default DetailBase.extend({
  layout,
  classNameBindings: [':recon-event-detail-files'],

  columnsConfig,

  processData({ data }) {
    this.set('reconData', data);
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