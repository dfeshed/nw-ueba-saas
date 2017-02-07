import { streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming } from './util/query-util';

const fetchPacketData = ({ endpointId, eventId, packetsPageSize }, dispatchPage, dispatchError) => {

  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-packet-data',
    query: streamingQuery,
    onResponse({ data }) {
      dispatchPage(data);
    },
    onError: dispatchError
  });
};

export default fetchPacketData;
