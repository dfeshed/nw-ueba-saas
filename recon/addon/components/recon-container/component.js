import Ember from 'ember';
import layout from './template';
import { TYPES_BY_NAME } from '../../utils/reconstruction-types';
import { buildBaseQuery } from '../../utils/query-util';

const {
  Component,
  inject: {
    service
  },
  assert
} = Ember;

export default Component.extend({
  request: service(),

  layout,
  tagName: '',

  // Component state
  reconstructionType: TYPES_BY_NAME.PACKET, // defaults to packet view
  showMetaDetails: false,
  showRequestData: true,
  showResponseData: true,
  // END Component state

  // BEGIN Component inputs
  endpointId: null,
  eventId: null,
  index: null,
  meta: null,
  total: null,

  // Lookups
  aliases: null,
  language: null,

  // Actions
  closeAction: null,
  expandAction: null,
  shrinkAction: null,
  // END Component inputs

  didReceiveAttrs() {
    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    assert('Cannot instantiate recon without endpointId and eventId.', endpointId && eventId);
    this.bootstrapRecon(endpointId, eventId);
  },

  bootstrapRecon(endpointId, eventId) {

    const query = buildBaseQuery(endpointId, eventId);

    if (!this.get('language')) {
      this.get('request').promiseRequest({
        method: 'query',
        modelName: 'core-meta-key',
        query
      }).then(({ data }) => {
        this.set('language', data);
      });
    }

    if (!this.get('aliases')) {
      this.get('request').promiseRequest({
        method: 'query',
        modelName: 'core-meta-alias',
        query
      }).then(({ data }) => {
        this.set('aliases', data);
      });
    }
  },

  actions: {
    toggleMetaDetails(forceShrink = false) {
      if (forceShrink) {
        this.set('showMetaDetails', false);
      } else {
        this.toggleProperty('showMetaDetails');
      }
    },
    toggleRequest() {
      this.toggleProperty('showRequestData');
    },
    toggleResponse() {
      this.toggleProperty('showResponseData');
    },
    updateReconstructionView(viewType) {
      this.set('reconstructionType', viewType);
    }
  }
});
