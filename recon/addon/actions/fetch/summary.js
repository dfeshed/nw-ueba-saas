import Ember from 'ember';
import { basicPromiseRequest } from './util/query-util';

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
    basicPromiseRequest(endpointId, eventId, 'reconstruction-summary')
      .then(({ data }) => {
        const headerItems = _generateHeaderItems(data.summaryAttributes);
        // eventually packetFields should be moved out of this request
        // but for now need to dig them out of response and expose them
        resolve([headerItems, data.packetFields]);
      }).catch((response) => {
        reject(response);
      });
  });
};

export default fetchReconSummary;
