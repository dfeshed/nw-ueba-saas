import { assert } from '@ember/debug';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { observer } from '@ember/object';
import { later, next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { toggleReconExpanded } from 'recon/actions/visual-creators';
import { hasReconView } from 'recon/reducers/visuals/selectors';
import layout from './template';
import {
  initializeRecon,
  initializeNotifications,
  setIndexAndTotal,
  teardownNotifications
} from 'recon/actions/data-creators';

const stateToComputed = ({ recon, recon: { files, visuals, notifications } }) => ({
  isMetaShown: visuals.isMetaShown,
  // Recon isn't ready until it has figured out which
  // Recon view is supposed to be displayed
  isViewReady: hasReconView(recon),
  isReconExpanded: visuals.isReconExpanded,
  isReconOpen: visuals.isReconOpen,
  stopNotifications: notifications.stopNotifications,
  status: files.fileExtractStatus,
  apiFatalErrorCode: recon.data.apiFatalErrorCode,
  meta: recon.meta.meta
});

const dispatchToActions = {
  initializeRecon,
  initializeNotifications,
  setIndexAndTotal,
  teardownNotifications,
  toggleReconExpanded
};

const ReconContainer = Component.extend({
  layout,
  tagName: 'vbox',
  classNames: ['recon-container'],
  classNameBindings: ['isReady::loading'],

  accessControl: service(),
  flashMessages: service(),
  i18n: service(),

  // BEGIN Component inputs
  endpointId: null,
  eventId: null,
  eventType: null, // network, log, or endpoint
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
  isAnimationDone: false,
  size: 'max',

  @computed('isViewReady', 'isAnimationDone', 'meta', 'eventType')
  isReady(isViewReady, isAnimationDone, meta, eventType) {
    return isViewReady && isAnimationDone && (meta || eventType);
  },

  @computed('i18n', 'apiFatalErrorCode', 'eventId')
  errorMessage(i18n, code, eventId) {
    return i18n.t(`recon.fatalError.${code}`, { eventId });
  },

  @computed('isReconExpanded')
  toggleEventsClass: (isReconExpanded) => isReconExpanded ? 'shrink-diagonal-2' : 'expand-diagonal-4',

  isReconExpandedChanged: observer('isReconExpanded', function() {
    if (this.get('isReconExpanded')) {
      this.get('expandAction')();
    } else {
      this.get('shrinkAction')();
    }
  }),

  fileExtractStatusWatcher: observer('status', function() {
    const stat = this.get('status');
    if (stat === 'queued') {
      const { flashMessages, i18n } = this.getProperties('flashMessages', 'i18n');
      if (flashMessages && flashMessages.info) {
        const url = `${window.location.origin}/profile#jobs`;
        flashMessages.info(i18n.t('recon.extractWarning', { url }), { sticky: true });
      }
    }
  }),

  closeRecon: observer('isReconOpen', function() {
    if (!this.get('isReconOpen')) {
      this.get('closeAction')();
    }
  }),

  didReceiveAttrs() {
    this._super(...arguments);
    const {
      oldEventId,
      index,
      total
    } = this.getProperties('oldEventId', 'index', 'total');
    const inputs = this.getProperties('endpointId', 'eventId', 'eventType', 'language', 'meta', 'aliases', 'linkToFileAction', 'size', 'queryInputs');

    // Checking whether or not Recon is open in standalone mode by checking
    // if closeAction is present or not. If a parent/containing addon/engine
    // exists, it would pass a closeAction
    const close = this.getProperties('closeAction');
    inputs.isStandalone = !close.closeAction;

    assert('Cannot instantiate recon without endpointId and eventId.', inputs.endpointId && inputs.eventId);

    // guard against re-running init on any redux state change,
    // if same id, no need to do anything
    if (!oldEventId || (oldEventId && inputs.eventId !== oldEventId)) {
      this.send('initializeRecon', inputs);
    }

    this.set('oldEventId', inputs.eventId);

    this.send('setIndexAndTotal', index, total);
  },

  didInsertElement() {
    this._super(...arguments);

    // start listening for notifications for the lifetime of this component
    // Use run.next because this action will trigger an update to the
    // `stopNotifications` attr, and without run.next Ember would then throw a
    // warning that we modified an attr during didInsertElement.
    next(() => {
      this.send('initializeNotifications');
    });

    // This ties into the amount of time it takes the recon panel to slide open.
    // We intentionally delay switching from the spinner to the Recon content so
    // that the panel smoothly opens. Once it's open, render all the things.
    // This does not delay any API calls/data fetching.
    later(this, () => {
      if (!this.isDestroyed) {
        this.set('isAnimationDone', true);
      }
    }, 2000);
  },

  willDestroyElement() {
    // stop listening for notifications
    const stopFn = this.get('stopNotifications');
    if (stopFn) {
      stopFn();
      this.send('teardownNotifications');
    }

    this._super(...arguments);
  },

  actions: {
    closeReconWhenFatalError() {
      this.get('closeAction')();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ReconContainer);
