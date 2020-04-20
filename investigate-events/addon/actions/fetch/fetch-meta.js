import {
  streamingRequest
} from 'investigate-shared/actions/api/events/utils';


export default function executeMetaValuesRequest(query, handlers) {
  return streamingRequest(
    'core-meta-value',
    query,
    handlers
  );
}
