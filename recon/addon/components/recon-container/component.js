import { assert } from 'ember-metal/utils';
import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import observer from 'ember-metal/observer';
import run from 'ember-runloop';

import layout from './template';
import {
  initializeRecon,
  initializeNotifications,
  teardownNotifications
} from '../../actions/data-creators';
import { toggleReconExpanded } from '../../actions/visual-creators';

const stateToComputed = ({ recon: { visuals, notifications } }) => ({
  isMetaShown: visuals.isMetaShown,
  isReconExpanded: visuals.isReconExpanded,
  isReconOpen: visuals.isReconOpen,
  stopNotifications: notifications.stopNotifications
});

const dispatchToActions = {
  initializeRecon,
  initializeNotifications,
  teardownNotifications,
  toggleReconExpanded
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
      'endpointId', 'eventId', 'language', 'meta', 'aliases', 'index', 'total', 'linkToFileAction');

    assert('Cannot instantiate recon without endpointId and eventId.', inputs.endpointId && inputs.eventId);

    const oldEventId = this.get('oldEventId');
    // guard against re-running init on any redux state change,
    // if same id, no need to do anything
    if (oldEventId && inputs.eventId === oldEventId) {
      return;
    }

    this.set('oldEventId', inputs.eventId);
    this.send('initializeRecon', inputs);

    // Containing application can pass in an initial expanded state
    const isExpanded = this.get('isExpanded');
    if (isExpanded !== undefined) {
      this.send('toggleReconExpanded', isExpanded);
    }
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
