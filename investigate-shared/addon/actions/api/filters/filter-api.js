import { lookup } from 'ember-dependency-lookup';

/**
 * Websocket call for custom search
 * @public
 */

const createCustomSearch = (filter, expressionList, filterTypeParameter) => {
  const request = lookup('service:request');
  const { id } = filter;
  const data = {
    id,
    name: filter.name.trim(),
    description: filter.description,
    filterType: filterTypeParameter
  };
  if (filter) {
    data.criteria = { expressionList, 'predicateType': 'AND' };
  }

  return request.promiseRequest({
    method: 'saveFilter',
    modelName: 'filters',
    query: { data },
    streamOptions: {
      socketUrlPostfix: 'any'
    }
  });
};

const getSavedFilters = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getFilter',
    modelName: 'filters',
    query: {},
    streamOptions: {
      socketUrlPostfix: 'any'
    }
  });
};
const deleteFilter = (id) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'deleteFilter',
    modelName: 'filters',
    query: { data: { id } },
    streamOptions: {
      socketUrlPostfix: 'any'
    }
  });
};

export default {
  createCustomSearch,
  getSavedFilters,
  deleteFilter
};
