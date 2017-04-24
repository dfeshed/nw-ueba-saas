import Ember from 'ember';
import { basicPromiseRequest } from '../util/query-util';
import Object from 'ember-object';

const { RSVP, A } = Ember;

const _generateHeaderItems = (items) => (
  items.reduce(function(headerItems, item) {
    if (!item.id && item.name) {
      item.id = item.name;
    }

    headerItems.pushObject(Object.create(item));

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
        resolve({ headerItems, packetFields: data.packetFields });
      }).catch((response) => {
        reject(response);
      });
  });
};

export default fetchReconSummary;
