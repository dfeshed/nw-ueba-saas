import Ember from 'ember';
import { buildBaseQuery } from '../../utils/query-util';

import layout from './template';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  request: service(),
  layout,
  classNameBindings: [':recon-meta-content', ':scroll-box'],

  // INPUTS
  endpointId: null,
  eventId: null,
  meta: null,
  // END INPUTS

  didReceiveAttrs() {
    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    const query = buildBaseQuery(endpointId, eventId);

    // meta can come into recon as input
    // but if it hasn't need to fetch it'
    if (!this.get('meta')) {
      this.get('request').promiseRequest({
        method: 'stream',
        modelName: 'core-event',
        query
      }).then(({ data }) => {
        this.set('meta', data[0].metas);
      });
    }
  }
});


