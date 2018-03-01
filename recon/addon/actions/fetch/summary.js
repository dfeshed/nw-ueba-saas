import RSVP from 'rsvp';
import { A } from '@ember/array';
import { basicPromiseRequest } from '../util/query-util';
import EmberObject from '@ember/object';

const _generateHeaderItems = (items) => (
  items.reduce(function(headerItems, item) {
    if (!item.id && item.name) {
      item.id = item.name;
    }

    headerItems.pushObject(EmberObject.create(item));

    return headerItems;
  }, A([]))
);

const fetchReconSummary = ({ endpointId, eventId }) => {
  return new RSVP.Promise((resolve, reject) => {
    basicPromiseRequest(
      endpointId,
      eventId,
      'reconstruction-summary',
      { cancelPreviouslyExecuting: true } // can only have one event in recon at a time
    ).then(({ data }) => {
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
