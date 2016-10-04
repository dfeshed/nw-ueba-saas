import Ember from 'ember';
import { promiseRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery } from './util/query-util';

const { RSVP, A } = Ember;

const _generateHeaderItems = (items) => (
  items.reduce(function(headerItems, item) {
    if (item.name === 'destination' || item.name === 'source') {
      headerItems.pushObjects([{
        name: `${item.name} IP:PORT`,
        value: item.value
      }]);
    } else {
      headerItems.pushObject(item);
    }
    return headerItems;
  }, A([]))
);

const fetchReconSummary = ({ endpointId, eventId }) => {
  return new RSVP.Promise((resolve, reject) => {
    promiseRequest({
      method: 'query',
      modelName: 'reconstruction-summary',
      query: buildBaseQuery(endpointId, eventId)
    }).then(({ data }) => {
      resolve(_generateHeaderItems(data.summaryAttributes));
    }).catch((response) => {
      reject(response);
      // TODO, dispatch error
    });
  });
};

export default fetchReconSummary;

