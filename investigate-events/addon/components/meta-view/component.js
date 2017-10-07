import Component from 'ember-component';
import run from 'ember-runloop';
import observer from 'ember-metal/observer';

import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import safeCallback from 'component-lib/utils/safe-callback';

const stateToComputed = ({ investigate }) => ({
  size: investigate.data.metaPanelSize
});

const MetaViewComponent = Component.extend({
  tagName: 'article',
  classNames: 'rsa-investigate-meta',
  classNameBindings: ['_sizeClass'],

  /**
   * Duration (in millisec) of delay between opening of component & revealing
   * its DOM content.
   * @public
   */
  unhideDelay: 250,

  /**
   * Configurable action; invoked when user clicks UI element to set `size` to `min`.
   * @type {function}
   * @public
   */
  minSizeAction: undefined,

  /**
   * Configurable action; invoked when user clicks UI element to set `size` to `max`.
   * @type {function}
   * @public
   */
  maxSizeAction: undefined,

  /**
   * Configurable action; invoked when user clicks UI element to set `size` to `default`.
   * @type {function}
   * @public
   */
  defaultSizeAction: undefined,

  /**
   * The current query's definition. Used to fetch meta values and to create links for drill-downs on the meta values.
   * @see state/query
   * @type {object}
   * @public
   */
  query: undefined,

  /**
   * Configurable callback to be invoked whenever a meta key is opened/closed.
   * @type {function}
   * @public
   */
  toggleAction: undefined,

  /**
   * Configurable callback to be invoked whenever a meta value is clicked.
   * @type {function}
   * @public
   */
  clickValueAction: undefined,

  /**
   * Configurable callback to be invoked whenever user requests a context lookup on a meta value.
   * @type {function}
   * @public
   */
  contextLookupAction: undefined,

  /**
   * The meta group for user to browse.
   * @type {{ keys: object[]}}
   * @public
   */
  group: undefined,

  /**
   * An array of state objects, one for each meta key in `language`. These objects represent the data streams
   * that fetch the meta values for the meta keys in `language`.
   * @see state/meta-key
   * @type {object[]}
   * @public
   */
  metaKeyStates: undefined,

  /**
   * Private size tracker.
   * @private
   */
  _size: 'default',

  /**
   * Converts `size` to CSS class equivalent.
   * @private
   */
  @computed('size')
  _sizeClass: (size) => `meta-size-${size}`,

  /**
   * Reacts to the size specified for this component. Sizes are either
   * minimized ('min'), maximized ('max') or default ('default').
   * @private
   */
  _sizeWatcher: observer('size', (sender) => {
    const prevSize = sender.get('_size');
    const currentSize = sender.get('size');
    const changed = prevSize !== currentSize;
    if (changed) {
      if (currentSize === 'min') {
        sender._didClose();
      } else if (prevSize === 'min') {
        sender._didOpen();
      }
      sender.set('_size', currentSize);
    }
  }),

  willDestroy() {
    this._cancelUnhideTimer();
    this._super(...arguments);
  },

  /**
   * Invoked after `size` changes to `min`. Responsible for hiding DOM content.
   * Cancels any pending timer to unhide content.
   * @private
   */
  _didClose() {
    this._cancelUnhideTimer();
    this.set('hideDom', true);
  },

  /**
   * Invoked after `size` changes from `min` to something else. Responsible for
   * un-hiding DOM content after a delay, which gives the resize animation time
   * to render smoothly and improves perceived performance. If a timer to
   * un-hide the DOM is already in progress, let it continue and exit.
   * @private
   */
  _didOpen() {
    if (!this._unhideTimer) {
      this._unhideTimer = run.later(() => {
        this.set('hideDom', false);
      }, this.get('unhideDelay'));
    }
  },

  /**
   * Cancels any pending timer for unhiding the DOM.
   * @private
   */
  _cancelUnhideTimer() {
    if (this._unhideTimer) {
      run.cancel(this._unhideTimer);
      this._unhideTimer = null;
    }
  },

  actions: {
    // Used for invoking actions from template that may be undefined (without throwing an error).
    safeCallback
  }
});

export default connect(stateToComputed)(MetaViewComponent);
