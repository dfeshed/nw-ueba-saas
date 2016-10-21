import Ember from 'ember';
import HasTableParent from 'component-lib/components/rsa-data-table/mixins/has-table-parent';
import layout from './template';

const {
  observer,
  Component,
  $
} = Ember;

export default Component.extend(HasTableParent, {
  layout,
  tagName: 'header',
  classNames: 'rsa-data-table-header',
  /**
   * CSS selector for the (optional) DOM nodes that can be used to drag-move header cells.
   * If not specified, or if matching node cannot be found, then moving will be disabled.
   * @type {string}
   * @public
   */
  moveHandleSelector: '.js-move-handle', // Selector for the drag-move handle.

  /**
   * Whether or not to translate header titles from locale
   * @public
   */
  translateTitles: false,

  /**
   * @name enableColumnSelector
   * @description Whether or not to display the column selector icon
   * @public
   */
  enableColumnSelector: false,

  /**
   * Moves this component horizontally so that the children header cells will horizontally align with the
   * rsa-data-table/body's cells as the user scrolls horizontally.
   * @private
   */
  _tableBodyScrollLeftDidChange: observer('table.body.scrollLeft', function() {
    if (this._$row) {
      let left = this.get('table.body.scrollLeft');
      if ($.isNumeric(left)) {
        this._$row.css('transform', `translate(-${left}px,0)`);
      }
    }
  }),

  /**
   * Initialize this element's horizontal translation when it first renders.
   * This ensures its aligns horizontally with the table body's scrollLeft initially, even if this component finishes r
   * endering AFTER the table.body, which is not typical but nevertheless possible.
   * @private
   */
  didInsertElement() {
    this._super(...arguments);
    this._$row = this.$('.js-header-row');
    this._tableBodyScrollLeftDidChange();
  },

  willDestroyElement() {
    this._$row = null;
    this._super(...arguments);
  }
});
