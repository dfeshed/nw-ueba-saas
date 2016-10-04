import Ember from 'ember';
import { promiseRequest, streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming } from './util/query-util';

const { RSVP } = Ember;

const fetchPacketData = ({ endpointId, eventId }) => {

  const basicQuery = buildBaseQuery(endpointId, eventId);

  // For now the API to retrieve packetField information is
  // the same as to retrieve recon summary data. This will change
  // in the future, so, for now, this is a duplicate call
  // given we will have made this call to retrieve summary data
  // elsewhere
  const summaryPromise = promiseRequest({
    method: 'query',
    modelName: 'reconstruction-summary',
    query: basicQuery
  });

  const streamingQuery = addStreaming(basicQuery);
  const packetPromise = new RSVP.Promise((resolve, reject) => {
    streamRequest({
      method: 'stream',
      modelName: 'reconstruction-packet-data',
      query: streamingQuery,
      onResponse: resolve,
      onError: reject
    });
  });

  return new RSVP.Promise((resolve, reject) => {
    RSVP.all([summaryPromise, packetPromise])
      .then(([{ data: summaryData }, { data: _packetData }]) => {
        const packetData = _packetData.map((p) => {
          p.side = (p.side === 1) ? 'request' : 'response';
          return p;
        });
        resolve([summaryData.packetFields, packetData]);
      })
      .catch((response) => reject(response));
  });

};

export default fetchPacketData;