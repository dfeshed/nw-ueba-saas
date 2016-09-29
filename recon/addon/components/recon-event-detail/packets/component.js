import Ember from 'ember';
import DetailBase from '../base/component';
import { addStreaming } from '../../../utils/query-util';
import layout from './template';

const { RSVP } = Ember;

export default DetailBase.extend({
  layout,
  classNameBindings: [':recon-event-detail-packets'],

  packetFields: null,

  retrieveData(basicQuery) {
    // For now the API to retrieve packetField information is
    // the same as to retrieve recon summary data. This will change
    // in the future, so, for now, this is a duplicate call
    // given we will have made this call to retrieve summary data
    // elsewhere
    const summaryPromise = this.get('request').promiseRequest({
      method: 'query',
      modelName: 'reconstruction-summary',
      query: basicQuery
    });

    const streamingQuery = addStreaming(basicQuery);
    const packetPromise = new RSVP.Promise((resolve, reject) => {
      this.get('request').streamRequest({
        method: 'stream',
        modelName: 'reconstruction-packet-data',
        query: streamingQuery,
        onResponse: resolve,
        onError: reject
      });
    });

    RSVP.all([summaryPromise, packetPromise])
      .then(([{ data: summaryData }, { data: _packetData }]) => {
        const packetData = _packetData.map((p) => {
          p.side = (p.side === 1) ? 'request' : 'response';
          return p;
        });
        this.get('reconData').pushObjects(packetData);
        this.set('packetFields', summaryData.packetFields);
      })
      .catch(this.handleError.bind(this));

  }
});
