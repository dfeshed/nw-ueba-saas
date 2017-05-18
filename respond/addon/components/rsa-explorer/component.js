import Component from 'ember-component';
import { isEmberArray } from 'ember-array/utils';
import { assert } from 'ember-metal/utils';
import connect from 'ember-redux/components/connect';
import actionBroker from 'respond/actions/action-creator-broker';
import service from 'ember-service/inject';
import { camelize } from 'ember-string';
import { isPresent } from 'ember-utils';
import { gt, alias, empty } from 'ember-computed-decorators';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';
const NOOP = () => ({});

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
    timeframeFilter: itemsFilters.created,
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
    getItems: () => actionBroker(dispatch, namespace, 'getItems'),
    updateItem: (entityId, fieldName, value) => actionBroker(dispatch, namespace, 'updateItem', entityId, fieldName, value, {
      onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess')),
      onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.updateFailure'))
    }),
    deleteItem: (entityId) => actionBroker(dispatch, namespace, 'deleteItem', entityId, {
      onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess')),
      onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.updateFailure'))
    }),
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
  classNameBindings: ['isFilterPanelOpen:show-filters', 'focusedItem:show-inspector', 'isTransactionUnderway:transaction-in-progress'],
  redux: service(),
  flashMessages: service(),
  eventBus: service(),

  /**
   * Each instance of an explorer can use a different namespace, which helps the component resolve the associated
   * action creators via the action-creator-broker, as well as the place in app state that holds all of related
   * properties
   * @property namespace
   * @type {string}
   * @public
   */
  namespace: '',

  onInit: function() {
    const columns = this.get('columns');
    assert('A "columns" attribute referencing an array must be passed to the Explorer to define the columns in the ' +
      'Explorer list', columns && isEmberArray(columns));
    assert('A namespace attribute must be provied', isPresent(this.get('namespace')));
    this.sendAction('bootstrap');
    this.send('getItems');

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
  selectionCount: null,

  actions: {
    /**
     * Convenience method for showing a flash success/error message to the user on update or failure
     * @method showFlashMessage
     * @public
     * @param type
     * @param i18nKey
     * @param context
     */
    showFlashMessage(type, i18nKey, context) {
      const { i18n, flashMessages } = this.getProperties('i18n', 'flashMessages');
      flashMessages[type.name](i18n.t(i18nKey, context), { iconName: type.icon });
    },

    /**
     * Displays specified confirmation modal dialog
     * @method showConfirmationDialog
     * @public
     */
    showConfirmationDialog(confirmationDialogId, confirmationData = {}, confirmCallback = NOOP, cancelCallback = NOOP) {
      this.setProperties({ showConfirmationDialog: true, confirmationDialogId, confirmationData, confirmCallback, cancelCallback });
      this.get('eventBus').trigger(`rsa-application-modal-open-${confirmationDialogId}`);
    },

    /**
     * Closes/Cancels a specified confirmation 'dialog'
     * @method closeConfirmationDialog
     * @public
     */
    closeConfirmationDialog(confirmationDialogId) {
      this.setProperties({ showConfirmationDialog: false, confirmationDialogId: null, confirmationData: null, confirmCallback: NOOP, cancelCallback: NOOP });
      this.get('eventBus').trigger(`rsa-application-modal-close-${confirmationDialogId}`);
    },

    confirm() {
      const callback = this.get('confirmCallback');
      callback();
      this.send('closeConfirmationDialog', this.get('confirmationDialogId'));
    },

    cancel() {
      this.get('cancelCallback')();
      this.send('closeConfirmationDialog', this.get('confirmationDialogId'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Explorer);
