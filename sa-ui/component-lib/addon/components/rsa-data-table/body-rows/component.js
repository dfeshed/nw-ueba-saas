import HasTableParent from '../mixins/has-table-parent';
import layout from './template';
import computed from 'ember-computed-decorators';
import { htmlSafe } from '@ember/string';
import Component from '@ember/component';
import { set } from '@ember/object';

export default Component.extend(HasTableParent, {
  layout,
  tagName: 'section',
  classNames: 'rsa-data-table-body-rows',

  attributeBindings: ['style'],

  @computed('minHeight')
  style(minHeight) {
    return htmlSafe(`min-height: ${minHeight}px;`);
  },

  /**
   * The minimum height of this component, in pixels.
   * Typically this is set by the parent component, which is responsible for computing the height of all the rows
   * and setting it here. This ensures that the scrollable area is at least as tall as all the rows, so that a
   * scrollbar will appear even if we are doing lazy rendering and not rendering all the rows at once.
   * @type {number}
   * @public
   */
  minHeight: 0,

  /**
   * Stores a reference to this component in the `body.rows` attribute of the parent `rsa-data-table` component.
   * This allows other components within the `rsa-data-table` hierarchy to access this component's properties.
   * For example, the `rsa-data-table/load-more` needs to access this component's `element` so that it insert markup.
   * @private
   */
  init() {
    this._super(...arguments);
    set(this.get('table.body'), 'rows', this);
  }
});
