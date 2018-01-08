import Component from 'ember-component';
import layout from './template';
import HasTableParent from 'component-lib/components/rsa-data-table/mixins/has-table-parent';
import { alias } from 'ember-computed-decorators';

export default Component.extend(HasTableParent, {
  layout,
  tagName: 'div',
  classNames: ['rsa-data-table-header-row', 'rsa-data-table-header-row-empty'],
  /**
   * Alias for the `visibleColumns` data array of the parent `rsa-data-table`.
   * @type {object[]}
   * @public
   */
  @alias('table.visibleColumns')
  visibleColumns: null
});
