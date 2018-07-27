import * as MESSAGE_TYPES from '../message-types';
import { warn } from '@ember/debug';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import RsaContextMenu from 'rsa-context-menu/components/rsa-context-menu/component';

import { enrichedPillsData, hasInvalidSelectedPill } from 'investigate-events/reducers/investigate/query-node/selectors';
import {
  addGuidedPill,
  deleteGuidedPill,
  deleteSelectedGuidedPills,
  deselectAllGuidedPills,
  deselectGuidedPills,
  editGuidedPill,
  openGuidedPillForEdit,
  resetGuidedPill,
  selectGuidedPills
} from 'investigate-events/actions/guided-creators';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';

const { log } = console;// eslint-disable-line no-unused-vars

const stateToComputed = (state) => ({
  pillsData: enrichedPillsData(state),
  hasInvalidSelectedPill: hasInvalidSelectedPill(state),
  metaOptions: metaKeySuggestionsForQueryBuilder(state)
});

const dispatchToActions = {
  addGuidedPill,
  deleteGuidedPill,
  deleteSelectedGuidedPills,
  editGuidedPill,
  selectGuidedPills,
  deselectGuidedPills,
  deselectAllGuidedPills,
  openGuidedPillForEdit,
  resetGuidedPill
};

const QueryPills = RsaContextMenu.extend({
  classNames: ['query-pills'],

  classNameBindings: [
    'isPillOpen:pill-open',
    'isPillOpenForEdit:pill-open-for-edit',
    'isPillTriggerOpenForAdd:pill-trigger-open-for-add'
  ],

  contextMenu({ target }) {
    const currentClass = target.classList.contains('is-selected');
    const parentClass = target.parentElement.classList.contains('is-selected');
    if (currentClass || parentClass) {
      this.setProperties({
        contextItems: this.get('contextItems')
      });
      this._super(...arguments);
    } else {
      if (this.get('contextMenuService').deactivate) {
        this.get('contextMenuService').deactivate();
      }
    } // do not call super so that the browser right-click event is preserved
  },

  // Used to hold onto new pill triggers that should be open
  // but have been re-rendered because id of closest pill has
  // been updated
  startTriggeredPosition: undefined,

  // Is a pill rendered by this component open for any reason?
  isPillOpen: false,

  // Is a pill rendered by this component open for edit?
  isPillOpenForEdit: false,

  // Is a pill trigger open for add?
  isPillTriggerOpenForAdd: false,

  @computed('pillsData')
  newPillPosition: (pillsData) => pillsData.length,

  @computed
  contextItems() {
    const _this = this;
    return [{
      label: 'Execute Query in same tab',
      disabled() {
        return _this.get('hasInvalidSelectedPill');
      },
      action() {
        // executeQuery in same tab
      }
    },
    {
      label: 'Execute Query in a new tab',
      disabled() {
        return _this.get('hasInvalidSelectedPill');
      },
      action() {
        // executeQuery in a new tab
      }
    },
    {
      label: 'Delete Selected Pills',
      action() {
        _this.send('deleteSelectedGuidedPills');
      }
    }];
  },

  init() {
    this._super(arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.PILL_ADD_CANCELLED]: (data, position) => this._pillAddCancelled(data, position),
      [MESSAGE_TYPES.PILL_CREATED]: (data, position) => this._pillCreated(data, position),
      [MESSAGE_TYPES.PILL_DELETED]: (data) => this._pillDeleted(data),
      [MESSAGE_TYPES.DELETE_PRESSED_ON_SELECTED_PILL]: () => this._deletePressedOnSelectedPill(),
      [MESSAGE_TYPES.PILL_EDIT_CANCELLED]: (data) => this._pillEditCancelled(data),
      [MESSAGE_TYPES.PILL_EDITED]: (data) => this._pillEdited(data),
      [MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW]: () => this._pillEnteredForAppend(),
      [MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW]: (pillData, position) => this._pillEnteredForInsert(position),
      [MESSAGE_TYPES.PILL_OPEN_FOR_EDIT]: (pillData) => this._pillOpenForEdit(pillData),
      [MESSAGE_TYPES.PILL_SELECTED]: (data) => this._pillsSelected([data]),
      [MESSAGE_TYPES.PILL_DESELECTED]: (data) => this._pillsDeselected([data])
    });
  },

  actions: {
    /**
     * Handler for all messages coming from pills.
     * @param {string} type The event type from `message-types`
     * @param {Object} data The event data
     * @param {Object} position The position of the pill being messaged
     * @public
     */
    handleMessage(type, data, position) {
      const messageHandlerFn = this.get('_messageHandlerMap')[type];
      if (messageHandlerFn) {
        messageHandlerFn(data, position);
      } else {
        // The buck stops here
        warn(
          `An unhandled query pill message of type "${type}" has occured.`,
          null,
          { id: 'query-pills' }
        );
      }
    }
  },

  // ************************************************************************ //
  //                          PRIVATE FUNCTIONS                               //
  // ************************************************************************ //

  _pillEnteredForAppend() {
    this.setProperties({
      isPillOpen: true,
      isPillOpenForEdit: false,
      isPillTriggerOpenForAdd: false
    });
    this._pillEntered();
  },

  _pillEnteredForEdit() {
    this.setProperties({
      isPillOpen: true,
      isPillOpenForEdit: true,
      isPillTriggerOpenForAdd: false
    });
    this._pillEntered();
  },

  _pillEnteredForInsert(position) {
    this.set('startTriggeredPosition', position);
    this.setProperties({
      isPillOpen: true,
      isPillOpenForEdit: false,
      isPillTriggerOpenForAdd: true
    });
    this._pillEntered();
  },

  _pillAddCancelled() {
    this._pillsExited();
  },

  /**
   * Adjusts flags to indicate that no pills are currently open/focused
   * for edit/add
   * @private
   */
  _pillsExited() {
    this.set('startTriggeredPosition', undefined);
    this.set('isPillOpen', false);
    this.set('isPillOpenForEdit', false);
    this.set('isPillTriggerOpenForAdd', false);
  },

  /**
   * Manages side effects to entering a pill for edit/add
   * @private
   */
  _pillEntered() {
    this.send('deselectAllGuidedPills');
  },

  /**
   * Adds pill to state
   * @param {*} pillData The data for the pill
   * @param {*} position The position of the pill in the array
   * @private
   */
  _pillCreated(pillData, position) {
    this._pillsExited();
    this.send('addGuidedPill', { pillData, position });
  },

  /**
   * Delete pill from state
   * @param {*} pillData The data for the pill
   * @private
   */
  _pillDeleted(pillData) {
    // If pills are open for any reason, treat delete as a no-op
    // as we do not allow pills to be interactive (including delete)
    // while pills are open.
    // Goal here is to keep the query-pill component stupid. It just
    // says "delete me" and the smart component (this one) says "no".
    if (this.get('isPillOpen')) {
      return;
    }

    this.send('deleteGuidedPill', { pillData });
  },

  _deletePressedOnSelectedPill() {
    this.send('deleteSelectedGuidedPills');
  },

  /**
   * Updates pill in state so it can be opened for editing
   * @param {*} pillData The data for the pill
   * @private
   */
  _pillOpenForEdit(pillData) {
    // If pills are open for any reason, treat attempt to edit
    // as a no-op as we do not allow pills to be interactive
    // while pills are open.
    if (this.get('isPillOpen')) {
      return;
    }

    this.send('openGuidedPillForEdit', { pillData });
    this._pillEnteredForEdit();
  },

  /**
   *
   * TODO: WHEN EDIT IS INTRODUCED, ALL OF THIS FUNCTION
   * NEEDS TESTING. ALSO NEED TO TEST THAT ENTERING A PILL
   * FOR EDIT SENDS AN ENTERED EVENT UP TO THIS COMPONENT
   * AND TRIGGERS ADDITION OF THE RIGHT CLASSES
   *
   * Edit pill in state
   * @param {*} pillData The data for the pill
   * @private
   */
  _pillEdited(pillData) {
    const pillsData = this.get('pillsData');
    const position = pillsData.map((pD) => pD.id).indexOf(pillData.id);
    this._pillsExited();
    this.send('editGuidedPill', { pillData, position });
  },

  _pillsSelected(pillData) {
    this.send('selectGuidedPills', { pillData });
  },

  _pillsDeselected(pillData) {
    this.send('deselectGuidedPills', { pillData });
  },

  _pillEditCancelled(data) {
    this._pillsExited();
    this.send('resetGuidedPill', data);
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryPills);