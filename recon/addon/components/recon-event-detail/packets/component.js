import DetailBase from '../base/component';
import layout from './template';

export default DetailBase.extend({
  layout,

  packetFields: null,

  retrieveData(basicQuery) {
    // add streaming stuffs
    const query = {
      ...basicQuery,
      page: {
        index: 0,
        size: 100
      },
      stream: {
        batch: 10,
        limit: 100000
      }
    };

    this.get('request').streamRequest({
      method: 'stream',
      modelName: 'reconstruction-packet-data',
      query,
      onResponse: ({ data }) => {
        const packetData = data.map((p) => {
          p.side = (p.side === 1) ? 'request' : 'response';
          return p;
        });
        this.get('reconData').pushObjects(packetData);
      },
      onError: this.handleError
    });
  }
});
