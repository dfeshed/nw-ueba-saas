import Ember from 'ember';
import computed from 'ember-computed-decorators';
import safeCallback from 'component-lib/utils/safe-callback';

const { run, Component } = Ember;

export default Component.extend({
  tagName: 'article',
  classNames: 'rsa-investigate-meta',
  classNameBindings: ['sizeClass'],

  /**
   * Specifies the current arrangement of this component; either minimized ('min'), maximized ('max') or default
   * ('default'). Used for CSS layout.
   * @type {string}
   * @public
   */
  @computed()
  size: {
    get() {
      return this.get('_size') || 'default';
    },
    set(value) {
      const wasValue = this.get('_size');
      const changed = wasValue !== value;
      if (changed) {
        if (value === 'min') {
          this._didClose();
        } else if (wasValue === 'min') {
          this._didOpen();
        }
      }
      this.set('_size', value);
      return value;
    }
  },

  // Invoked after `size` changes to `min` to something else.
  // Responsible for hiding DOM content. Cancels any pending timer to unhide content.
  _didClose() {
    this._cancelUnhideTimer();
    this.set('hideDom', true);
  },

  // Invoked after `size` changes from `min` to something else.
  // Responsible for un-hiding DOM content after a delay, which gives the resize animation time to render smoothly
  // and improves perceived performance. If a timer to un-hide the DOM is already in progress, let it continue and exit.
  _didOpen() {
    if (!this._unhideTimer) {
      this._unhideTimer = run.later(() => {
        this.set('hideDom', false);
      }, this.get('unhideDelay'));
    }
  },

  // Cancels any pending timer for unhiding the DOM. If no such timer, does nothing.
  _cancelUnhideTimer() {
    if (this._unhideTimer) {
      run.cancel(this._unhideTimer);
      this._unhideTimer = null;
    }
  },

  willDestroy() {
    this._cancelUnhideTimer();
    this._super(...arguments);
  },

  // Duration (in millisec) of delay between opening of component & revealing its DOM content.
  unhideDelay: 250,

  // Converts `size` to CSS class equivalent.
  @computed('size')
  sizeClass: (size) => `meta-size-${size}`,

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
   * @see protected/investigate/state/query
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
   * A Language state object, containing all the meta keys for the NetWitness Core service from which this data
   * was fetched. Used for looking up information about the keys (such as flags indicating which keys are indexed,
   * which keys are singletons, etc).
   * @type {object}
   * @public
   */
  language: undefined,

  /**
   * An aliases state object, containing a hash of lookup tables for meta key values.
   * Used for rendering meta values as user-friendly text rather than raw values.
   * @type {object}
   * @public
   */
  aliases: undefined,

  /**
   * An array of state objects, one for each meta key in `language`. These objects represent the data streams
   * that fetch the meta values for the meta keys in `language`.
   * @see protected/investigate/state/meta-key
   * @type {object[]}
   * @public
   */
  metaKeyStates: undefined,

  actions: {
    // Used for invoking actions from template that may be undefined (without throwing an error).
    safeCallback
  }
});
