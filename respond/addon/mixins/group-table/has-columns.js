import Column from 'respond/utils/group-table/column';
import Mixin from '@ember/object/mixin';
import computed, { filterBy } from 'ember-computed-decorators';

/**
 * @class HasColumns Mixin
 * Enables a Component to manage the state of a set of columns.
 *
 * Supports a `columnsConfig` attr, which is used to define a set of columns that can be
 * displayed in the group table. Additionally equips the Component with attrs `columns` & `visibleColumns`
 * which are derived from the `columnsConfig`.
 *
 * @public
 */
export default Mixin.create({
  /**
   * Array of column configuration objects.
   * Each array member is a POJO that represents a column. The following properties are supported:
   *
   * (i) `field`: Required. The name of the JSON field from which to read the display value.
   *
   * (ii) `title`: Optional. String to display as the column's title. Defaults to `field`.
   * (Titles will only be shown if if `{{rsa-group-table/header}}` is included in the `{{#rsa-group-table}}` block).
   *
   * (iii) `width`: Optional. Width for this column. If missing, a default width will be applied.
   *
   * (iv) `visible`: Optional. If `false`, the column is not rendered in DOM. Defaults to `true`.
   *
   * (v) `componentClass`: Optional. Name of the Ember Component to be used to render this column's values. Defaults
   * to `rsa-group-table/body-item-cell`. At render time, the component for the column will be assigned 2 attributes:
   * - `item`: the data record from `table.groups[i].items` to be rendered; and
   * - `column`: this column configuration object which corresponds to the column being rendered.
   *
   * @example
   * ```js
   * columnsConfig: [{
   *   field: 'name'
   * }, {
   *   field: 'created',
   *   title: 'Created Date',
   *   componentClass: 'path/to/my-component',
   *   width: 150
   * }, {
   *   field: 'desc',
   *   title: 'Description'
   * }]
   * ```
   *
   * @type {Object[]}
   * @public
   */
  columnsConfig: null,

  /**
   * Computes an array of Ember.Objects from the POJOs in `columnsConfig`.
   * @type {Ember.Object[]}
   * @public
   */
  @computed('columnsConfig.[]')
  columns(columnsConfig) {
    return (columnsConfig || [])
      .map((config) => Column.create(config));
  },

  /**
   * Computes the subset of `columns` whose `selected` is `true`.
   * @type {Object[]}
   * @public
   */
  @filterBy('columns', 'visible', true)
  visibleColumns: null
});
