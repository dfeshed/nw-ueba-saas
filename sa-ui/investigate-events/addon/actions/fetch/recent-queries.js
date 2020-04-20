import { queryPromiseRequest } from 'investigate-shared/actions/api/events/utils';

export default function fetchRecentQueries(queryText, cancelPreviouslyExecuting) {

  const streamOptions = {
    cancelPreviouslyExecuting
  };
  const query = {
    predicateRequests: [{
      filterText: queryText
    }],
    stream: {
      limit: 100
    }
  };
  return queryPromiseRequest(
    'recent-queries',
    query,
    streamOptions
  );
}
