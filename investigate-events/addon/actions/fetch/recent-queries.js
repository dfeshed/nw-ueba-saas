import { queryPromiseRequest } from 'investigate-shared/actions/api/events/utils';

export default function fetchRecentQueries(queryText) {

  const query = {
    predicateRequest: [{
      filterText: queryText
    }],
    stream: {
      limit: 100,
      cancelPreviouslyExecuting: false
    }
  };
  return queryPromiseRequest(
    'recent-queries',
    query
  );
}
