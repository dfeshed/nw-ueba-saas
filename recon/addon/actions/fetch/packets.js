import { streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming } from './util/query-util';

const fetchPacketData = ({ endpointId, eventId }, dispatchPage, dispatchError) => {

  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-packet-data',
    query: streamingQuery,
    onResponse({ data }) {
      const packetData = data.map((p) => {
        p.side = (p.side === 1) ? 'request' : 'response';
        return p;
      });
      dispatchPage(packetData);
    },
    onError: dispatchError
  });
};

export default fetchPacketData;