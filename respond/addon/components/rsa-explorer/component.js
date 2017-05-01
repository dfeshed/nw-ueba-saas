import Component from 'ember-component';
import { isEmberArray } from 'ember-array/utils';
import { assert } from 'ember-metal/utils';
import connect from 'ember-redux/components/connect';
import actionBroker from 'respond/actions/action-creator-broker';
import service from 'ember-service/inject';
import { camelize } from 'ember-string';
import { isPresent } from 'ember-utils';
import { gt, alias, empty } from 'ember-computed-decorators';

/**
 * The Explorer component's redux state will always use the same base set of properties (e.g., items, itemsSelected,
 * focusedItem, etc), independent of the domain for which it's used (e.g., incidents, alerts, remediation tasks, etc).
 *
 * Rather than require all parent components to declare their own stateToComputed, which will always be the same for
 * each Explorer implementation, the stateToComputed is baked into the Explorer component itself and utilizes a
 * declared 'namespace' to dynamically find the space in the application state that has all of the required properties.
 *
 * If, for example, the Explorer is declared with the namespace 'remediation-tasks', the stateToComputed will expect
 * to find all Explorer properties at respond.remediationTasks. If the namespace is 'alerts', the stateToComputed will
 * expect to find all Explorer properties at respond.alerts.
 *
 * This automatic resolution of state properties therefore means we can define stateToComputed once as part of Explorer,
 * and not at a higher level consumer component.
 * @param state
 * @private
 */
const stateToComputed = function(state) {
  const { respond } = state;
  const namespace = this.get('namespace');
  const stateSpace = respond[camelize(namespace)] || {};

  return {
    items: stateSpace.items,
    itemsStatus: stateSpace.itemsStatus,
    itemsTotal: stateSpace.itemsTotal,
    itemsSelected: stateSpace.itemsSelected,
    isFilterPanelOpen: stateSpace.isFilterPanelOpen,
    focusedItem: stateSpace.focusedItem,
    isTransactionUnderway: stateSpace.isTransactionUnderway,
    hasCustomDate: stateSpace.hasCustomDateRestriction,
    timeframeFilter: stateSpace.itemsFilters.created,
    isSelectAll: stateSpace.isSelectAll,
    sortField: stateSpace.sortField,
    isSortDescending: stateSpace.isSortDescending
  };
};

/**
 * The Explorer has an interface of required methods/actions that are used to define and implement all of the Explorer
 * behaviors (e.g., toggling the filter panel, selecting items, updating filters, sorting, etc.
 *
 * The dispatchToActions function uses the Explorer's namespace to automatically find and execute the
 * redux action creator functions. A simple function called the actionBroker (which is aware of all known creator
 * functions), auto-resolves the proper function to use by doing a lookup with the namespace.
 *
 * Like stateToComputed, this automatic resolution of action creator functions means we can define the dispatchToActions
 * once as part of Explorer and not at a higher level for each and every consumer of the Exploer component.
 * @param dispatch
 * @private
 */
const dispatchToActions = function(dispatch) {
  const namespace = this.get('namespace');
  return {
    initializeItems: () => actionBroker(dispatch, namespace, 'getItems'),
    toggleFilterPanel: () => actionBroker(dispatch, namespace, 'toggleFilterPanel'),
    updateFilter: (change) => actionBroker(dispatch, namespace, 'updateFilter', change),
    resetFilters: () => actionBroker(dispatch, namespace, 'resetFilters'),
    toggleCustomDate: () => actionBroker(dispatch, namespace, 'toggleCustomDateRestriction'),
    select: (item) => actionBroker(dispatch, namespace, 'toggleItemSelected', item.id),
    focus: (item) => actionBroker(dispatch, namespace, 'toggleFocusItem', item),
    clearFocusItem: () => actionBroker(dispatch, namespace, 'clearFocusItem'),
    toggleSelectAll: () => actionBroker(dispatch, namespace, 'toggleSelectAll'),
    sortBy: (sortField, isSortDescending) => actionBroker(dispatch, namespace, 'sortBy', sortField, isSortDescending)
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
const Explorer = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-respond-explorer', 'flexi-fit'],
  classNameBindings: ['isFilterPanelOpen:show-filters', 'isTransactionUnderway:transaction-in-progress'],
  redux: service(),
  namespace: '',

  onInit: function() {
    const columns = this.get('columns');
    assert('A "columns" attribute referencing an array must be passed to the Explorer to define the columns in the ' +
      'Explorer list', columns && isEmberArray(columns));
    assert('A namespace attribute must be provied', isPresent(this.get('namespace')));
    this.sendAction('bootstrap');
    this.send('initializeItems');

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
