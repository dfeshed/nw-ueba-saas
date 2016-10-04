import Ember from 'ember';
import connect from 'ember-redux/components/connect';

import layout from './template';
import { buildBaseQuery } from '../../utils/query-util';
import * as Actions from '../../actions/visual-creators';

const {
  Component,
  inject: {
    service
  },
  assert,
  observer
} = Ember;

const stateToComputed = ({ visuals }) => ({
  isMetaShown: visuals.isMetaShown,
  isReconExpanded: visuals.isReconExpanded,
  isReconOpen: visuals.isReconOpen
});

const dispatchToActions = (dispatch) => ({
  initializeRecon: () => dispatch(Actions.initializeRecon())
});

const ReconContainer = Component.extend({
  request: service(),
  layout,
  tagName: '',

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

  init() {
    this._super(...arguments);

    // containing UI may not remember if recon was expanded when
    // it was last used, if it was, expand it now
    if (this.get('isReconExpanded')) {
      this.sendAction('expandAction');
    }

    this.send('initializeRecon');
  },

  // Temporary observer hacks while only doing redux half-way
  isReconExpandedChanged: observer('isReconExpanded', function() {
    if (this.get('isReconExpanded')) {
      this.sendAction('expandAction');
    } else {
      this.sendAction('shrinkAction');
    }
  }),
  closeRecon: observer('isReconOpen', function() {
    if (!this.get('isReconOpen')) {
      this.sendAction('closeAction');
    }
  }),

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
  }

});

export default connect(stateToComputed, dispatchToActions)(ReconContainer);