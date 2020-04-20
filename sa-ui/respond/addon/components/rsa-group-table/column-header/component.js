import Component from '@ember/component';
import layout from './template';

/**
 * @class GroupTableColumnHeader Component
 * Renders a generic customizable column header for a Group Table component.
 * @public
 */
export default Component.extend({
  layout,
  classNames: ['rsa-group-table-column-header'],

  /**
   * The object representing the corresponding table column for this component. Typically passed down from parent.
   * @type {Ember.Object}
   * @public
   */
  column: null,

  /**
   * The 0-based index of `column` among the visible columns of the table. Typically passed down from parent.
   * @type {Ember.Object}
   * @public
   */
  index: null
});
