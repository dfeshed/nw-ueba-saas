import Ember from 'ember';
import { streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming } from './util/query-util';

const { RSVP } = Ember;

const fetchPacketData = ({ endpointId, eventId }) => {

  const basicQuery = buildBaseQuery(endpointId, eventId);
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
    // Leaving this as an array of promises
    // because we should have extra endpoint
    // to fetch packetFields created soon. Right
    // now the summary data (another call)
    // handles fetching that information
    RSVP.all([packetPromise])
      .then(([{ data: _packetData }]) => {
        const packetData = _packetData.map((p) => {
          p.side = (p.side === 1) ? 'request' : 'response';
          return p;
        });
        resolve([packetData]);
      })
      .catch((response) => reject(response));
  });

};

export default fetchPacketData;