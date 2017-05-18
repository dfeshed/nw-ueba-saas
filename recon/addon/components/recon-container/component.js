import { assert } from 'ember-metal/utils';
import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import observer from 'ember-metal/observer';
import run from 'ember-runloop';

import { hasReconView } from 'recon/reducers/visuals/selectors';
import layout from './template';
import {
  initializeRecon,
  initializeNotifications,
  setIndexAndTotal,
  teardownNotifications
} from 'recon/actions/data-creators';

const stateToComputed = ({ recon, recon: { visuals, notifications } }) => ({
  isMetaShown: visuals.isMetaShown,
  // Recon isn't ready until it has figured out which
  // Recon view is supposed to be displayed
  isReady: hasReconView(recon),
  isReconExpanded: visuals.isReconExpanded,
  isReconOpen: visuals.isReconOpen,
  stopNotifications: notifications.stopNotifications
});

const dispatchToActions = {
  initializeRecon,
  initializeNotifications,
  setIndexAndTotal,
  teardownNotifications
};

const ReconContainer = Component.extend({
  layout,
  tagName: 'vbox',
  classNames: ['recon-container'],

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
  linkToFileAction: null,
  // END Component inputs

  oldEventId: null,

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
    this._super(...arguments);
    const inputs = this.getProperties(
      'endpointId', 'eventId', 'language', 'meta', 'aliases', 'linkToFileAction');

    assert('Cannot instantiate recon without endpointId and eventId.', inputs.endpointId && inputs.eventId);

    const oldEventId = this.get('oldEventId');
    // guard against re-running init on any redux state change,
    // if same id, no need to do anything
    if (!oldEventId || (oldEventId && inputs.eventId !== oldEventId)) {
      this.send('initializeRecon', inputs);
    }

    this.set('oldEventId', inputs.eventId);

    const { index, total } = this.getProperties('index', 'total');
    this.send('setIndexAndTotal', index, total);
  },

  didInsertElement() {
    this._super(...arguments);

    // start listening for notifications for the lifetime of this component
    // Use run.next because this action will trigger an update to the `stopNotifications` attr,
    // and without run.next Ember would then throw a warning that we modified an attr during didInsertElement.
    run.next(() => {
      this.send('initializeNotifications');
    });
  },

  willDestroyElement() {
    // stop listening for notifications
    const stopFn = this.get('stopNotifications');
    if (stopFn) {
      stopFn();
      this.send('teardownNotifications');
    }

    this._super(...arguments);
  }
});

export default connect(stateToComputed, dispatchToActions)(ReconContainer);
