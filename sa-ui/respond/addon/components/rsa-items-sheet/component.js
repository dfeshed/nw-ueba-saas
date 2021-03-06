import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { gt } from '@ember/object/computed';

/**
 * @class Items Sheet component
 * A Component used for browsing the detailed properties of a given list of items.
 *
 * It contains 2 child components: a Table view for displaying a list of items, and an Item Details view
 * for displaying the details of a single item. When this Items Sheet component is given an `items` array containing
 * only a single item, it automatically displays the Item Details view for that item.
 *
 * Otherwise, if `items` contains multiple items, then this Component displays those items in a data table so that
 * the end-user can choose which item should be shown in the Item Details view.
 *
 * @public
 */
export default Component.extend({
  layout,
  classNames: ['rsa-items-sheet'],

  /**
   * Total number of expected `items`.
   * The total count of items is used to determine the default view (Item Details should be the default only when
   * total count is exactly one; otherwise the Table view is the default).
   * The total count is surfaced as a separate input param (rather than simply measuring `items.length`) because in
   * the Respond UI scenario, `items` may be streamed in and therefore `items.length` is variable and unreliable.
   * Also in Respond UI, incidents and alerts already have cached their total event count, so we don't need to wait
   * for the events to finish streaming in order to determine their total count. Rather, we can prepare the UI
   * right away, even before the events have started arriving.
   * @type {Number}
   * @public
   */
  totalCount: 0,

  /**
   * The object from `items` which is to be displayed in the Item Details view.
   * @type {Object}
   * @public
   */
  selectedItem: null,

  detailsHeaderComponentClass: 'respond-common/stub',

  detailsBodyComponentClass: 'rsa-property-tree',

  tableHeaderComponentClass: 'respond-common/stub',

  tableBodyComponentClass: 'rsa-data-table',

  /**
   * The columns configuration for the data table child view.
   * @see component-lib/components/rsa-data-table#columnsConfig
   * @type {Object[]}
   * @public
   */
  columnsConfig: null,

  init() {
    this._super(arguments);
    /**
     * The list of POJOs to be displayed, possibly empty.
     * @type {Object[]}
     * @public
     */
    this.items = this.items || [];
  },

  // Clear `itemSelected` every time `items` is reset.
  // Without this, the UI won't return back to the Table view if `items` gets reset after the user
  // has clicked on an item in Table view.
  didReceiveAttrs() {
    const items = this.get('items');
    if (items !== this._lastItems) {
      const selectedItem = this.get('selectedItem');
      if (selectedItem) {
        const found = items && items.includes(selectedItem);
        if (!found) {
          this.set('selectedItem', null);
        }
      }
      this._lastItems = items;
    }
  },

  // Computes which item should be currently shown in the Item Details view
  resolvedSelectedItem: computed('items.[]', 'selectedItem', 'totalCount', function() {
    if (this.selectedItem) {
      return this.selectedItem;
    } else {
      const canAutoSelect = (this.totalCount === 1);
      if (canAutoSelect) {
        return this.items && this.items[0];
      }
      return null;
    }
  }),

  // Computes the index of `resolvedSelectedItem` relative to the entire `items` array.
  selectedIndex: computed('items.[]', 'resolvedSelectedItem', function() {
    if (!this.resolvedSelectedItem || !this.items) {
      return -1;
    }
    return this.items.indexOf(this.resolvedSelectedItem);
  }),

  // Computes the 1-based number that corresponds to the 0-based selectedIndex.
  selectedOrdinal: computed('selectedIndex', function() {
    return this.selectedIndex + 1;
  }),

  // Computes the item in `items` that is immediately before `resolvedSelectedItem` (if any).
  previousItem: computed('items.[]', 'selectedIndex', function() {
    return (this.selectedIndex > 0) ? this.items[this.selectedIndex - 1] : null;
  }),

  // Computes the item in `items` that is immediately after `resolvedSelectedItem` (if any).
  nextItem: computed('items.[]', 'selectedIndex', function() {
    return (this.selectedIndex > -1) ? this.items[this.selectedIndex + 1] : null;
  }),

  // True if the Item Details view should be displayed; otherwise, Table view should be displayed.
  shouldShowDetails: computed('resolvedSelectedItem', 'totalCount', function() {
    return !!this.resolvedSelectedItem || (this.totalCount === 1);
  }),

  // True if the Previous & Next navigation controls should be displayed
  // i.e., if there are multiple objects in `items`.
  isNavEnabled: gt('items.length', 1),
  isPreviousEnabled: gt('selectedIndex', 0),

  isNextEnabled: computed('selectedOrdinal', 'items.length', function() {
    return this.selectedOrdinal < this.items?.length;
  }),

  actions: {
    /**
     * Configurable action to be invoked when user clicks on a row in the data table.
     * @param {Object} item The data object corresponding to the clicked row.
     * @public
     */
    onTableRowClick(item) {
      this.set('selectedItem', item);
    },

    /**
     * Action to be invoked when user clicks on Back To Table btn in Item Details.
     * @private
     */
    backToTable() {
      this.set('selectedItem', null);
    },

    /**
     * Action to be invoked when user clicks on Previous Item btn in Item Details.
     * @private
     */
    previous() {
      this.send('onTableRowClick', this.get('previousItem'));
    },

    /**
     * Action to be invoked when user clicks on Next Item btn in Item Details.
     * @private
     */
    next() {
      this.send('onTableRowClick', this.get('nextItem'));
    }
  }
});
