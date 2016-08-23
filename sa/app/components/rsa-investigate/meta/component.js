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
   * List of available meta groups for user to browse.
   * Gets passed down to the `groups-panel` child component for display.
   * @type {object[]}
   * @public
   */
  groups: undefined,

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
    }
  }
});
