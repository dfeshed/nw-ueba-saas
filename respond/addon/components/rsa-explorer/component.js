import Component from '@ember/component';
import { isArray } from '@ember/array';
import { assert } from '@ember/debug';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { gt, alias, empty } from 'ember-computed-decorators';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';
import Confirmable from 'respond/mixins/confirmable';
import Notifications from 'respond/mixins/notifications';
import { get } from '@ember/object';

/**
 * The Explorer component's redux state will always use the same base set of properties (e.g., items, itemsSelected,
 * focusedItem, etc), independent of the domain for which it's used (e.g., incidents, alerts, remediation tasks, etc).
 *
 * Rather than require all parent components to declare their own stateToComputed, which will always be the same for
 * each Explorer implementation, the stateToComputed is baked into the Explorer component itself and utilizes a
 * declared 'reduxSpace' to dynamically find the area in redux state that has all of the required properties.
 *
 * If, for example, the Explorer is declared with the reduxSpace 'respond.incidents', the stateToComputed will expect
 * to find all Explorer properties in redux state at 'respond.incidents.
 *
 * This automatic resolution of state properties therefore means we can define stateToComputed once as part of Explorer,
 * and not at a higher level consumer component.
 * @param state
 * @private
 */
const stateToComputed = function(state) {
  const stateSpace = get(state, this.get('reduxSpace')) || {};
  const itemsFilters = stateSpace.itemsFilters || {};

  return {
    items: stateSpace.items,
    itemsStatus: stateSpace.itemsStatus,
    itemsTotal: stateSpace.itemsTotal,
    itemsSelected: stateSpace.itemsSelected,
    isFilterPanelOpen: stateSpace.isFilterPanelOpen,
    focusedItem: stateSpace.focusedItem,
    isTransactionUnderway: stateSpace.isTransactionUnderway,
    hasCustomDate: stateSpace.hasCustomDateRestriction,
    timeframeFilter: itemsFilters[stateSpace.defaultDateFilterField],
    isSelectAll: stateSpace.isSelectAll,
    sortField: stateSpace.sortField,
    isSortDescending: stateSpace.isSortDescending,
    defaultDateFilterField: stateSpace.defaultDateFilterField
  };
};

/**
 * The Explorer has an interface of required methods/actions that are used to define and implement all of the Explorer
 * behaviors (e.g., toggling the filter panel, selecting items, updating filters, sorting, etc.
 *
 * The dispatchToActions function uses the creators attribute (an object that implements the explorer action-creator
 * interface) to dispatch all of the expected actions that the explorer component supports.
 * @param dispatch
 * @private
 */
const dispatchToActions = function(dispatch) {
  const creators = this.get('creators');
  return {
    getItems: () => dispatch(creators.getItems()),
    updateItem: (entityId, fieldName, value, revert = () => {}) => dispatch(creators.updateItem(entityId, fieldName, value, {
      onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess')),
      onFailure: () => {
        revert();
        this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.updateFailure');
      }
    })),
    deleteItem: (entityId) => dispatch(creators.deleteItem(entityId, {
      onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess')),
      onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.updateFailure'))
    })),
    toggleFilterPanel: () => dispatch(creators.toggleFilterPanel()),
    updateFilter: (change) => dispatch(creators.updateFilter(change)),
    resetFilters: () => dispatch(creators.resetFilters()),
    toggleCustomDate: () => dispatch(creators.toggleCustomDateRestriction()),
    select: (item) => dispatch(creators.toggleItemSelected(item.id)),
    focus: (item) => dispatch(creators.toggleFocusItem(item)),
    clearFocusItem: () => dispatch(creators.clearFocusItem()),
    toggleSelectAll: () => dispatch(creators.toggleSelectAll()),
    sortBy: (sortField, isSortDescending) => dispatch(creators.sortBy(sortField, isSortDescending))
  };
};

/**
 * The Explorer is a high-level component that consists of a layout containing the following child components:
 *
 *  -- Toolbar
 *  -- Table
 *  -- Filters
 *  -- Inspector
 *
 * The Explorer bundles the aforementioned components so that they can be reused across multiple pages (e.g., Incidents,
 * Remediation Tasks, and Alerts), all of which follow the same basic pattern of providing a set of items in a table,
 * a way to filter those items, a way to select and execute actions on multiple items (i.e., bulk actions), and a way
 * to inspect the details of any one specific item.
 * @class Explorer
 * @public
 */
const Explorer = Component.extend(Notifications, Confirmable, {
  tagName: 'vbox',
  classNames: ['rsa-respond-explorer', 'flexi-fit'],
  classNameBindings: ['isFilterPanelOpen:show-filters', 'focusedItem:show-inspector', 'isTransactionUnderway:transaction-in-progress'],
  redux: service(),

  /**
   * Each instance of an explorer can use a different reduxSpace, which helps resolve the redux app state that holds
   * all of related properties
   * @property reduxSpace
   * @type {string}
   * @public
   */
  reduxSpace: '',

  creators: null,

  onInit: function() {
    const columns = this.get('columns');
    const creators = this.get('creators');
    assert('A "columns" attribute referencing an array must be passed to the Explorer to define the columns in the ' +
      'Explorer list', columns && isArray(columns));
    assert('A "creators" attribute must be provided that contains all of the explorer interface functions', creators);
  }.on('init'),

  /**
   * The number of (filtered) items delivered to the explorer view. This should be distinguished from "itemsTotal" which
   * represents the total item count that meets the filter criteria but not all of which is shown in the page.
   * @property itemsCount
   * @public
   */
  @alias('items.length')
  itemsCount: null,

  /**
   * True if there is one or more items in the data set
   * @property hasResults
   * @public
   */
  @gt('itemsTotal', 0) hasResults: false,

  /**
   * Boolean true when there are no selected items
   * @property hasNoSelections
   * @public
   */
  @empty('itemsSelected') hasNoSelections: true,

  /**
   * The number of currently selected items
   * @public
   * @property selectionCount
   */
  @alias('itemsSelected.length')
  selectionCount: null
});

export default connect(stateToComputed, dispatchToActions)(Explorer);
