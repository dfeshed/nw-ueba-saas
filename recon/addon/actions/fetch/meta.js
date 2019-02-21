import RSVP from 'rsvp';
import { lookup } from 'ember-dependency-lookup';
import {
  addStreaming,
  addSessionQueryFilter,
  endpointFilter,
  addCatchAllTimeRange
} from '../util/query-util';

const fetchMeta = ({ endpointId, eventId }) => {
  const request = lookup('service:request');
  let query = endpointFilter(endpointId);
  query = addStreaming(query);
  query = addSessionQueryFilter(query, eventId);
  query = addCatchAllTimeRange(query);
  return new RSVP.Promise((resolve, reject) => {
    request.streamRequest({
      method: 'stream',
      modelName: 'core-event',
      query,
      onError: reject,
      onResponse({ data, meta }) {
        if (meta.complete) {
          // call to events returns array of events
          // but this just has single event, yank that out
          // and pass along
          resolve((data[0] && data[0].metas) || []);
        }
      }
    });
  });
};

export default fetchMeta;