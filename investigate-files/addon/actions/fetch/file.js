import { promiseRequest } from 'streaming-data/services/data-access/requests';
import {
  addSortBy,
  addFilter
} from 'investigate-files/actions/utils/query-util';

/**
 * Retrieves all matching global files from the server.
 * @returns Promise that will resolve with the server response.
 * @public
 */
const fetchFiles = (pageNumber, sort, expressionList) => {
  let query = {
    pageNumber: pageNumber || 0,
    pageSize: 100
  };
  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);
  return promiseRequest({
    method: 'search',
    modelName: 'files',
    query: {
      data: query
    }
  });
};

export default { fetchFiles };