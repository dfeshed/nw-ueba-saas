import DetailBase from '../base/component';
import layout from './template';

export default DetailBase.extend({
  layout,

  retrieveData(query) {
    this.get('request').promiseRequest({
      method: 'query',
      modelName: 'reconstruction-file-data',
      query
    }).then(({ data }) => {
      this.set('reconData', data);
    }).catch(this.handleError);
  }
});
