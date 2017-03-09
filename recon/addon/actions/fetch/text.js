import { streamRequest } from 'streaming-data/services/data-access/requests';
import {
  buildBaseQuery,
  addStreaming,
  addDecode
} from './util/query-util';
import Ember from 'ember';

const { run } = Ember;
const wait = 50;

const fetchTextData = ({ endpointId, eventId, packetsPageSize, decode }, dispatchPage, dispatchError) => {
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize);
  const decodeQuery = addDecode(streamingQuery, decode);
  let count = 1;
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-text-data',
    query: decodeQuery,
    onResponse({ data }) {
      run.later(this, dispatchPage, data, wait * count++);
    },
    onError: dispatchError
  });
};

export default fetchTextData;
