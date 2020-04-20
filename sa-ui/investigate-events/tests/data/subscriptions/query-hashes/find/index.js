import { hashCache } from '..';

/**
 * Finds predicate objects in the hash by its Id.
 * @param {object} hashIds[] Array of Ids to look for.
 */
const _getFromHash = (hashIds = []) => {
  console.log(`FIND::_getFromHash(${hashIds}): hashCache =`, hashCache);// eslint-disable-line
  return hashIds.map((hashId) => hashCache.find((d) => d.id === hashId));
};

export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get-by-id',
  requestDestination: '/ws/investigate/predicate/get-by-id',
  message(frame) {
    const { predicateIds } = JSON.parse(frame.body);
    return {
      meta: {
        complete: false
      },
      data: _getFromHash(predicateIds)
    };
  }
};
