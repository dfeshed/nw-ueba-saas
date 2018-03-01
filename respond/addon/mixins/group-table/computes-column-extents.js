import Mixin from '@ember/object/mixin';
import HasColumns from './has-columns';
import computed from 'ember-computed-decorators';

/**
 * @class ComputesColumnSizes Mixin
 * Defines a `totalColumnsWidth` attr, which sums the width of columns in a `visibleColumns` attr.
 * @public
 */
export default Mixin.create(HasColumns, {

  /**
   * The sum of the widths of the columns currently in `visibleColumns`, including units, as a String (e.g., '125%', '50px').
   * Supports column widths that all share the same units (e.g., '%', 'px') or are all unitless (assumes 'px').
   * Columns whose widths are 'auto' or not defined are excluded from the sum.
   * If column widths have mixed units (e.g., a 'px' column + a '%' column) then returns empty string.
   * @type {String}
   * @public
   */
  @computed('visibleColumns.@each.width')
  totalColumnsWidth(visibleColumns) {
    const obj = (visibleColumns || [])
      .mapBy('parsedWidth')
      .reduce(
        ((total, w) => total ? total.add(w) : w),
        null
      );
    return obj ? obj.get('string') : '';
  }
});
