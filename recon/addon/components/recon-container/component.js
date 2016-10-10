import Ember from 'ember';
import connect from 'ember-redux/components/connect';

import layout from './template';
import * as DataActions from '../../actions/data-creators';

const {
  Component,
  assert,
  observer
} = Ember;

const stateToComputed = ({ visuals }) => ({
  isMetaShown: visuals.isMetaShown,
  isReconExpanded: visuals.isReconExpanded,
  isReconOpen: visuals.isReconOpen
});

const dispatchToActions = (dispatch) => ({
  initializeRecon: (inputs) => dispatch(DataActions.initializeRecon(inputs))
});

const ReconContainer = Component.extend({
  layout,
  tagName: 'vbox',
  classNameBindings: [':recon-container'],

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
  },

  // Temporary observer hacks while only doing redux half-way
  // If container participated in redux, then it would simply
  // bind to the same isReconExpanded and act directly, without
  // any need to pass expand/shrink/close actions into recon
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
    const inputs = this.getProperties('endpointId', 'eventId', 'language', 'meta', 'aliases', 'index', 'total');
    assert('Cannot instantiate recon without endpointId and eventId.', inputs.endpointId && inputs.eventId);
    this.send('initializeRecon', inputs);
  }

});

export default connect(stateToComputed, dispatchToActions)(ReconContainer);