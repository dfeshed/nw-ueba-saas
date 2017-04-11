import unfilteredData from './data';

const CREATED = 'created';
const ASSIGNEE = 'assignee.id';

/**
 * Simple, targeted function to determine whether an item matches a particular incident filter. Used to simulate filtering via
 * the filter controls panel
 * @param item
 * @param filters
 * @public
 * @returns {boolean}
 */
const matchesFilter = (item, filters) => {
  let hasMatchedAllFilters = true;
  let matchedFilters = 0;
  const { assignee } = item;

  filters.forEach(({ field, value, range }) => {
    if (field === CREATED) {
      if (item.created > range.from) {
        matchedFilters++;
      }
    }

    if (field === ASSIGNEE && assignee && assignee.id === value) {
      matchedFilters++;
    } else if (item[field] === value) { // default lookup on root obj properties
      matchedFilters++;
    }
  });

  if (matchedFilters !== (filters.length)) {
    hasMatchedAllFilters = false;
  }
  return hasMatchedAllFilters;
};

export default {
  subscriptionDestination: '/user/queue/incidents',
  requestDestination: '/ws/response/incidents',

  message(frame) {
    let data = [];
    if (!frame) {
      data = unfilteredData;
    } else {
      const { body } = frame;
      const bodyParsed = JSON.parse(body);
      const { filter } = bodyParsed;

      unfilteredData.forEach((item) => {
        if (matchesFilter(item, filter)) {
          data.push(item);
        }
      });
    }

    return {
      data,
      meta: {
        total: 1099
      }
    };
  }
};