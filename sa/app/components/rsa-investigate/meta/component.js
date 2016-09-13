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
   * Configurable callback to be invoked whenever a meta key is opened/closed.
   * @type {function}
   * @public
   */
  toggleAction: undefined,

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
