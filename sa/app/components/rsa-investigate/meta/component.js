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


  actions: {
    // Used for invoking actions from template that may be undefined (without throwing an error).
    safeCallback
  }
});
