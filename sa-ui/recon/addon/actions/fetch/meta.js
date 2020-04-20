import RSVP from 'rsvp';
import {
  basicPromiseRequest
} from '../util/query-util';
import { formatResponse } from '../util/meta-util';

const fetchMeta = ({ endpointId, eventId }) => {
  return new RSVP.Promise((resolve, reject) => {
    basicPromiseRequest(
      endpointId,
      eventId,
      'reconstruction-meta',
      { cancelPreviouslyExecuting: true } // can only have one event in recon at a time
    ).then(({ data }) => {
      const formattedData = formatResponse(data);
      // call to events returns array of events
      // but this just has single event, yank that out
      // and pass along
      resolve((formattedData[0] && formattedData[0].metas) || []);
    }).catch((response) => {
      reject(response);
    });
  });
};

export default fetchMeta;
