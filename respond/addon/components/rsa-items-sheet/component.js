import Component from 'ember-component';
import layout from './template';
import computed, { bool, gt } from 'ember-computed-decorators';

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
   * The list of POJOs to be displayed, possibly empty.
   * @type {Object[]}
   * @public
   */
  items: [],

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

  // Clear `itemSelected` every time `items` is reset.
  // Without this, the UI won't return back to the Table view if `items` gets reset after the user
  // has clicked on an item in Table view.
  didReceiveAttrs() {
    const items = this.get('items');
    if (items !== this._lastItems) {
      if (this.get('selectedItem')) {
        this.set('selectedItem', null);
      }
      this._lastItems = items;
    }
  },

  // Computes which item should be currently shown in the Item Details view
  @computed('items.[]', 'selectedItem')
  resolvedSelectedItem(items, selectedItem) {
    return selectedItem ||
      ((items && items.length === 1) ? items[0] : null);
  },

  // Computes the index of `resolvedSelectedItem` relative to the entire `items` array.
  @computed('items.[]', 'resolvedSelectedItem')
  selectedIndex(items, selectedItem) {
    if (!selectedItem || !items) {
      return -1;
    }
    return items.indexOf(selectedItem);
  },

  // Computes the 1-based number that corresponds to the 0-based selectedIndex.
  @computed('selectedIndex')
  selectedOrdinal(selectedIndex) {
    return selectedIndex + 1;
  },

  // Computes the item in `items` that is immediately before `resolvedSelectedItem` (if any).
  @computed('items.[]', 'selectedIndex')
  previousItem(items, selectedIndex) {
    return (selectedIndex > 0) ? items[selectedIndex - 1] : null;
  },

  // Computes the item in `items` that is immediately after `resolvedSelectedItem` (if any).
  @computed('items.[]', 'selectedIndex')
  nextItem(items, selectedIndex) {
    return (selectedIndex > -1) ? items[selectedIndex + 1] : null;
  },

  // True if the Item Details view should be displayed; otherwise, Table view should be displayed.
  @bool('resolvedSelectedItem')
  shouldShowItemDetails: true,

  // Computes an object that is a composition of the other computes properties which describe this component's UI
  // state, plus the name of the liquid-fire transition that should be to render that new state.
  @computed('shouldShowItemDetails', 'items', 'resolvedSelectedItem', 'selectedIndex', 'isNavEnabled')
  shouldShow(itemDetails, items, selectedItem, selectedIndex, isNavEnabled) {
    const show = { itemDetails, items, selectedItem, selectedIndex, isNavEnabled };
    const lastShow = this._lastShow || {};
    let transition = 'crossFade';
    if (show.items === lastShow.items) {
      if (!lastShow.selectedItem || !show.selectedItem) {
        if (!lastShow.selectedItem && show.selectedItem) {
          transition = 'toLeft';
        } else if (lastShow.selectedItem && !show.selectedItem) {
          transition = 'toRight';
        }
      }
    }
    show.transition = transition;
    this._lastShow = show;
    return show;
  },

  // True if the Previous & Next navigation controls should be displayed; i.e., if there are multiple objects in `items`.
  @gt('items.length', 1)
  isNavEnabled: false,

  @gt('selectedIndex', 0)
  isPreviousEnabled: false,

  @computed('selectedOrdinal', 'items.length')
  isNextEnabled(ordinal, length) {
    return ordinal < length;
  },

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
