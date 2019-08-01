import {
  queryPromiseRequest,
  serviceIdFilter
} from 'investigate-shared/actions/api/events/utils';

const _createFilterObjects = (field, value) => {
  return { field, value };
};

/**
* Fetch value suggestions for text typed in
* @param {string|number} serviceId Id of the service
* @param {String} metaName selected meta
* @param {String} prefixText value typed in pill-value
* @return {object} RSVP Promise
* @public
*/
export default function fetchValueSuggestions(serviceId, metaName, prefixText) {

  const streamOptions = {
    cancelPreviouslyExecuting: true
  };
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      _createFilterObjects('prefix', prefixText),
      _createFilterObjects('metaName', metaName)
    ],
    stream: {
      limit: 100
    }
  };
  return queryPromiseRequest(
    'value-suggestions',
    query,
    streamOptions
  );
}
