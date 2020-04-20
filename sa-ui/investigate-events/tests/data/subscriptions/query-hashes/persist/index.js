import { hashCache } from '..';
import { recentQueries } from '../../recent-queries';

const CHARS = '0123456789qwertyuiopasdfghjklzxcvbnm';

const _generateId = () => {
  let id = '';
  for (let i = 0; i < 4; i++) {
    const idx = Math.floor(Math.random() * CHARS.length);
    id += CHARS[idx];
  }
  return id;
};

/**
 * Gets predicate info from hash cache, or creates a new predicate, saving it to
 * the cache, and returning that.
 * @param {string} predicateRequests The predicates to persist
 */
const _getPredicate = (predicateRequests) => {
  const hashObjects = [];
  predicateRequests.forEach((predicate) => {
    const { query } = predicate;
    // See if we've seen this query before
    const hashObj = hashCache.find((d) => d.query === query);
    if (hashObj) {
      // We found something in the cache, return it
      console.log(`PERSIST::_getPredicate(): found hash ${hashObj.id} in hashCache: ${hashObj.query} `);// eslint-disable-line
      hashObjects.push(hashObj);
    } else {
      // Nothing in the cache create a new one
      const predicate = {
        id: _generateId(),
        query,
        displayName: query,
        createdBy: 'local',
        createdOn: new Date().toISOString()
      };
      hashCache.push(predicate);
      console.log(`PERSIST::_getPredicate(): predicate not in hashCache, created ${predicate.id} and pushed to hashCache`, hashCache);// eslint-disable-line
      hashObjects.push(predicate);
      recentQueries.push(predicate.query);
    }
  });
  // Only return the request for the full query, which is in response to a
  // predicate request length of one.
  if (predicateRequests.length === 1) {
    return hashObjects;
  }
};

export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get-by-query',
  requestDestination: '/ws/investigate/predicate/get-by-query',
  message(frame) {
    const { predicateRequests } = JSON.parse(frame.body);
    return {
      meta: {
        complete: true// was false. Why?
      },
      data: _getPredicate(predicateRequests)
    };
  }
};