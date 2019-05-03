import { assert } from '@ember/debug';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { observer } from '@ember/object';
import { later, next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { toggleReconExpanded } from 'recon/actions/visual-creators';
import layout from './template';
import {
  initializeRecon,
  initializeNotifications,
  setIndexAndTotal,
  teardownNotifications
} from 'recon/actions/data-creators';
// TODO enable flash messaging after a certain fixed time
// import { didQueueDownload } from 'recon/actions/interaction-creators';

const stateToComputed = ({ recon, recon: { files, visuals, notifications } }) => ({
  isMetaShown: visuals.isMetaShown,
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
  // TODO enable flash messaging after a certain fixed time
  // didQueueDownload,
  toggleReconExpanded
};

const ReconContainer = Component.extend({
  layout,
  tagName: 'vbox',
  classNames: ['recon-container'],
  classNameBindings: ['isReady::loading', 'extractWarningClass'],

  accessControl: service(),
  flashMessages: service(),
  i18n: service(),

  // ************************** BEGIN Component API ************************* //
  /**
   * Alias data for a service. If this data is not supplied to this component,
   * it will fetch it itself.
   * @type {Array.<Object>}
   */
  aliases: null,

  /**
   * The unique id of a service (aka endpoint) that captured the event.
   * @type {String}
   * @required
   */
  endpointId: null,

  /**
   * The unique id of the event to be reconstructed.
   * @type {String}
   * @required
   */
  eventId: null,

  /**
   * The meta data for the event being reconstructed. If this data is not
   * supplied to this component, it will fetch it itself.
   * @type {Array.<Object>}
   */
  eventMeta: null,

  /**
   * The type of event that's being reconstructed. This information can be
   * derived from `eventMeta`, but it takes time to pull in that data. While the
   * meta is being fetched, you will see a spinner. To help the UI display
   * relevent components/options quicker, supply an `eventType`.
   * @see `component-lib/constants/event-type`
   * @type {String}
   */
  eventType: null,

  /**
   * Zero based index of item in result set (if viewing item from result set).
   * @type {Number}
   */
  index: null,

  /**
   * Language data for a service. If this data is not supplied to this
   * component, it will fetch it itself.
   * @type {Array.<Object>}
   */
  language: null,

  /**
   * Various params which are part of the query, including the data needed for
   * executing actions in the context menu.
   * @type {Object}
   */
  queryInputs: null,

  /**
   * The initial size to render the Recon window.
   * @type {String}
   */
  size: 'max',

  /**
   * Total number of results in result set (if viewing item from result set).
   * @type {number}
   */
  total: null,

  /**
   * Action to be performed when closing the Recon panel.
   * @type {Function}
   */
  closeAction: null,

  /**
   * Action to be performed when expanding the Recon panel to a wider width.
   * @type {Function}
   */
  expandAction: null,

  /**
   * Action to be performed when linking to a file.
   * @type {Function}
   */
  linkToFileAction: null,

  /**
   * Action to be performed when shrinking the Recon panel to a smaller width.
   * @type {Function}
   */
  shrinkAction: null,
  // *************************** END Component API ************************** //

  _isAnimationDone: false,
  _previousEventId: undefined,

  /**
   * Determines if the UI is in a state that is ready to display UI elements.
   * If this is `false` we show a spinner. If this is `true` we show the title
   * and header sections. If the event reconstruction and meta are available
   * we'll show those too (otherwise we'll show a spinner where that data would
   * be displayed).
   * @param {Boolean} _isAnimationDone - Has the Recon panel finished it's
   * animation into view.
   * @param {String} eventType - Event type that was passed in.
   * @param {Array} eventMeta - Event meta that was passed in.
   * @param {Array} meta - Event meta this component fetched because `eventMeta`
   * was not supplied.
   * @return {Boolean}
   */
  @computed('_isAnimationDone', 'eventType', 'eventMeta', 'meta')
  isReady(_isAnimationDone, eventType, eventMeta, meta) {
    return _isAnimationDone && !!(eventType || eventMeta || meta);
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

  // displays a flash message pointing to job queue and changes fileExtractStatus to notified
  // when file extraction is queued due to navigating away in the middle of download
  // TODO enable flash messaging after a certain fixed time
  /* @computed('status')
  extractWarningClass(status) {

    if (status === 'queued') {
      const { flashMessages, i18n } = this.getProperties('flashMessages', 'i18n');
      if (flashMessages && flashMessages.info) {
        const url = `${window.location.origin}/profile#jobs`;
        flashMessages.info(i18n.t('recon.extractWarning', { url }), { sticky: true });
        this.send('didQueueDownload');
      }
    }
  },
  */

  closeRecon: observer('isReconOpen', function() {
    if (!this.get('isReconOpen')) {
      this.get('closeAction')();
    }
  }),

  didReceiveAttrs() {
    this._super(...arguments);
    const {
      _previousEventId,
      index,
      total
    } = this.getProperties('_previousEventId', 'index', 'total');
    const inputs = this.getProperties('endpointId', 'eventId', 'eventType',
      'language', 'eventMeta', 'aliases', 'linkToFileAction', 'size', 'queryInputs');

    // Checking whether or not Recon is open in standalone mode by checking
    // if closeAction is present or not. If a parent/containing addon/engine
    // exists, it would pass a closeAction
    const close = this.getProperties('closeAction');
    inputs.isStandalone = !close.closeAction;

    assert('Cannot instantiate recon without endpointId and eventId.', inputs.endpointId && inputs.eventId);

    // guard against re-running init on any redux state change,
    // if same id, no need to do anything
    if (inputs.eventId !== _previousEventId) {
      this.set('_previousEventId', inputs.eventId);
      this.send('initializeRecon', inputs);
    }

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
        this.set('_isAnimationDone', true);
      }
    }, 500);
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
