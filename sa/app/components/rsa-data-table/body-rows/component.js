import Ember from 'ember';
import CspStyleMixin from 'ember-cli-csp-style/mixins/csp-style';

const { Component } = Ember;

export default Component.extend(CspStyleMixin, {
  tagName: 'section',
  classNames: 'rsa-data-table-body-rows',

  // Applies `minHeight` to the component's `element`.
  // @see ember-cli-csp-style/mixins/csp-style
  styleBindings: ['minHeight:min-height[px]'],

  /**
   * The minimum height of this component, in pixels.
   * Typically this is set by the parent component, which is responsible for computing the height of all the rows
   * and setting it here. This ensures that the scrollable area is at least as tall as all the rows, so that a
   * scrollbar will appear even if we are doing lazy rendering and not rendering all the rows at once.
   * @type {number}
   * @public
   */
  minHeight: 0
});
