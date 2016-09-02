import Ember from 'ember';
import computed from 'ember-computed-decorators';
import safeCallback from 'sa/utils/safe-callback';

const { Component } = Ember;

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
  size: 'default',

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
   * A sequence of user's selections. Initially empty.
   * This array grows as the user clicks inside the component's contents, making selections and "navigating"
   * from one display to another.  And it shrinks as the user navigates back using a "Back" button.
   * When either of those happen, the array is not mutated; instead it is reset by reference. This is done
   * so that the `{{#liquid-bind}}` in this component's template will observe the change.
   * @type {object[]}
   * @default undefined
   * @public
   */
  path: undefined,

  /**
   * Configurable callback to be invoked whenever `fwd` action is triggered.
   * @type {function}
   * @public
   */
  onFwd: undefined,

  /**
   * Configurable callback to be invoked whenever `back` action is triggered.
   * @type {function}
   * @public
   */
  onBack: undefined,

  /**
   * Configurable callback to be invoked whenever a meta key is opened/closed.
   * @type {function}
   * @public
   */
  toggleAction: undefined,

  /**
   * List of available meta groups for user to browse.
   * Gets passed down to the `groups-panel` child component for display.
   * @type {object[]}
   * @public
   */
  groups: undefined,

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
    safeCallback,

    /**
     * Removes the last object in `path` (if any), thus effectively navigating back one step.
     * Note that this method does not mutate the existing path object; it clones it first then operates on the clone.
     * @public
     */
    back() {
      let path = this.get('path');
      if (!path || !path.length) {
        return;
      }
      path = [].concat(path);
      path.popObject();
      // @workaround liquid-bind breaks with an empty array for an unknown reason; use undefined instead
      if (!path.length) {
        path = undefined;
      }
      // end @workaround

      this.set('path', path);
      safeCallback(this.get('onBack'));
    },

    /**
     * Adds the given object to `path`, thus effectively navigating forward one step.
     * Note that this method does not mutate the existing path object; it clones it first then operates on the clone.
     * @param {object} obj The data object to be added to `path`.
     * @public
     */
    fwd(obj) {
      if (!obj) {
        return;
      }
      let path = this.get('path') || [];
      path = [].concat(path);
      path.pushObject(obj);

      this.set('path', path);
      safeCallback(this.get('onFwd'), obj);
    }
  }
});
