import Component from '@ember/component';
import layout from './template';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import HasSelections from 'respond/mixins/group-table/has-selections';

/**
 * @class Group Table Component
 * A configurable multi-column data-driven table-like layout that displays groups of data rows in collapsable sections.
 * For performance, utilizes lazy rendering i.e. only renders rows that are within the scrollable viewport area.
 *
 * This is an Ember "contextual component", i.e. it yields its child components so that its content can be customized.
 * @example
 * ```hbs
 * {{#rsa-group-table as |table|}}
 *  {{#table.header}}
 *    ...
 *  {{/table.header}}
 *  {{#table.body}}
 *    ...
 *  {{/table.body}}
 * {{/rsa-group-table}}
 * ```
 * @public
 */
export default Component.extend(ComputesRowViewport, ComputesColumnExtents, HasSelections, {
  tagName: 'article',
  layout,
  classNames: ['rsa-group-table'],

  /**
   * Configurable name of the Ember Component to be used for rendering the header content of this table.
   * This component will automatically receive a `table` attr which points back to this table Component.
   * @type {String}
   * @default 'rsa-group-table/column-headers'
   * @public
   */
  columnHeadersComponentClass: 'rsa-group-table/column-headers',

  /**
   * Configurable name of the Ember Component to be used for rendering the body content of this table.
   * This component will automatically receive a `table` attr which points back to this table Component.
   * @type {String}
   * @default 'rsa-group-table/body'
   * @public
   */
  bodyComponentClass: 'rsa-group-table/body',

  /**
   * Configurable handler for clicks on a group header row.
   * @param {Object} group The group data object that corresponds to the clicked row.
   * @param {Number} groupIndex The index of `group` relative to the entire `groups` array.
   * @public
   */
  groupClickAction() {},

  /**
   * Configurable handler for SHIFT+clicks on a group header row.
   * @param {Object} group The group data object that corresponds to the clicked row.
   * @param {Number} groupIndex The index of `group` relative to the entire `groups` array.
   * @public
   */
  groupShiftClickAction() {},

  /**
   * Configurable handler for CTRL+clicks on a group header row.
   * @param {Object} group The group data object that corresponds to the clicked row.
   * @param {Number} groupIndex The index of `group` relative to the entire `groups` array.
   * @public
   */
  groupCtrlClickAction() {},

  /**
   * Configurable handler for clicks on a group item row.
   * @param {Object} group The group data object which contains the clicked row.
   * @param {Number} groupIndex The index of `group` relative to the entire `groups` array.
   * @param {Object} item The data object which corresponds to the clicked row.
   * @param {Number} itemIndex The index of `item` relative to the `group.items` array.
   * @public
   */
  itemClickAction() {},

  /**
   * Configurable handler for SHIFT+clicks on a group item row.
   * @param {Object} group The group data object which contains the clicked row.
   * @param {Number} groupIndex The index of `group` relative to the entire `groups` array.
   * @param {Object} item The data object which corresponds to the clicked row.
   * @param {Number} itemIndex The index of `item` relative to the `group.items` array.
   * @public
   */
  itemShiftClickAction() {},

  /**
   * Configurable handler for CTRL+clicks on a group item row.
   * @param {Object} group The group data object which contains the clicked row.
   * @param {Number} groupIndex The index of `group` relative to the entire `groups` array.
   * @param {Object} item The data object which corresponds to the clicked row.
   * @param {Number} itemIndex The index of `item` relative to the `group.items` array.
   * @public
   */
  itemCtrlClickAction() {}
});
