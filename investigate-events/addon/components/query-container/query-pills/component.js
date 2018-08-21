import { warn } from '@ember/debug';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import RsaContextMenu from 'rsa-context-menu/components/rsa-context-menu/component';
import * as MESSAGE_TYPES from '../message-types';
import { isEscape } from 'investigate-events/util/keys';

import {
  canQueryGuided,
  enrichedPillsData,
  hasInvalidSelectedPill,
  selectedPills,
  deselectedPills
} from 'investigate-events/reducers/investigate/query-node/selectors';
import {
  addGuidedPill,
  deleteGuidedPill,
  deleteSelectedGuidedPills,
  deselectAllGuidedPills,
  deselectGuidedPills,
  editGuidedPill,
  openGuidedPillForEdit,
  removeGuidedPillFocus,
  resetGuidedPill,
  selectGuidedPills,
  selectAllPillsTowardsDirection
} from 'investigate-events/actions/guided-creators';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';

const { log } = console;// eslint-disable-line no-unused-vars

const stateToComputed = (state) => ({
  canQueryGuided: canQueryGuided(state),
  deselectedPills: deselectedPills(state),
  pillsData: enrichedPillsData(state),
  hasInvalidSelectedPill: hasInvalidSelectedPill(state),
  metaOptions: metaKeySuggestionsForQueryBuilder(state),
  selectedPills: selectedPills(state)
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
  removeGuidedPillFocus,
  resetGuidedPill,
  selectAllPillsTowardsDirection
};

const QueryPills = RsaContextMenu.extend({
  tagName: null,

  classNameBindings: [
    'isPillOpen:pill-open',
    'isPillOpenForEdit:pill-open-for-edit',
    'isPillTriggerOpenForAdd:pill-trigger-open-for-add'
  ],

  i18n: service(),

  // whether or not this component's children should take
  // focus if they are so inclined.
  takeFocus: true,

  // Action to execute when submitting a query
  executeQuery: () => {},

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

  @computed('pillsData')
  newPillPosition: (pillsData) => pillsData.length,

  @computed
  contextItems() {
    const _this = this;
    const i18n = this.get('i18n');
    return [{
      label: i18n.t('queryBuilder.querySelected'),
      disabled() {
        return _this.get('hasInvalidSelectedPill');
      },
      action() {
        // Delete all selected pills first
        // submit query with remaining selected pills
        _this.send('deleteGuidedPill', { pillData: _this.get('deselectedPills') });
        _this._submitQuery();
      }
    },
    {
      label: i18n.t('queryBuilder.querySelectedNewTab'),
      disabled() {
        return _this.get('hasInvalidSelectedPill');
      },
      action() {
        // Do not want to check canQueryGuided because user might
        // want to execute the same query in new tab
        _this.get('executeQuery')(true);
        // deselect all the pills. Can't trigger this first, as
        // route action picks up selected pills from state to executeQ
        _this.send('deselectAllGuidedPills');
      }
    },
    {
      label: i18n.t('queryBuilder.delete'),
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
      [MESSAGE_TYPES.ENTER_PRESSED_ON_SELECTED_PILL]: (pillData) => this._enterPressedOnSelectedPill(pillData),
      [MESSAGE_TYPES.PILL_EDIT_CANCELLED]: (data) => this._pillEditCancelled(data),
      [MESSAGE_TYPES.PILL_EDITED]: (data) => this._pillEdited(data),
      [MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW]: () => this._pillEnteredForAppend(),
      [MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW]: (pillData, position) => this._pillEnteredForInsert(position),
      [MESSAGE_TYPES.PILL_INTENT_TO_QUERY]: () => this._submitQuery(),
      [MESSAGE_TYPES.PILL_OPEN_FOR_EDIT]: (pillData) => this._pillOpenForEdit(pillData),
      [MESSAGE_TYPES.PILL_SELECTED]: (data) => this._pillsSelected([data]),
      [MESSAGE_TYPES.PILL_DESELECTED]: (data) => this._pillsDeselected([data]),
      [MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT]: (position) => this._pillsSelectAllToRight(position),
      [MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT]: (position) => this._pillsSelectAllToLeft(position)
    });
  },

  didInsertElement() {
    this._super(...arguments);
    const _boundEscapeListener = this._escapeListener.bind(this);
    // saving the event handler fn so that it can be removed later
    this.set('_boundEscapeListener', _boundEscapeListener);
    window.addEventListener('keydown', _boundEscapeListener);
  },

  willDestroyElement() {
    this._super(...arguments);
    window.removeEventListener('keydown', this.get('_boundEscapeListener'));
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
    },

    clickOutside() {
      this.send('removeGuidedPillFocus');
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
   * If entering a pill for edit or add, focus should be removed from whatever pill has it
   * And no pill should be selected
   * @private
   */
  _pillEntered() {
    this.send('removeGuidedPillFocus');
    this.send('deselectAllGuidedPills');
  },

  /**
   * Adds pill to state
   * @param {*} pillData The data for the pill
   * @param {*} position The position of the pill in the array
   * @private
   */
  _pillCreated(pillData, position) {
    let shouldAddFocusToNewPill = false;
    // if true, it means a pill is being created in the middle of pills
    if (this.get('isPillTriggerOpenForAdd')) {
      shouldAddFocusToNewPill = true;
    }
    this._pillsExited();
    this.send('addGuidedPill', { pillData, position, shouldAddFocusToNewPill });
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

    this.send('deleteGuidedPill', { pillData: [pillData] });
  },

  /**
   * Deletes all selected pills on keypress
   * @private
   */
  _deletePressedOnSelectedPill() {
    this.send('deleteSelectedGuidedPills');
  },

  _pillsSelectAllToRight(position) {
    this.send('selectAllPillsTowardsDirection', position, 'right');
  },

  _pillsSelectAllToLeft(position) {
    this.send('selectAllPillsTowardsDirection', position, 'left');
  },
  /**
   * Opens up the pill for edit on keypress
   * @private
   * Selecting & hitting enter can also trigger this
   * action. So we need to make sure there is just
   * one pill selected
   */
  _enterPressedOnSelectedPill(pillData) {
    const sP = this.get('selectedPills');
    if (sP.length === 1) {
      this._pillOpenForEdit(pillData);
    }
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

  _submitQuery() {
    if (this.get('canQueryGuided')) {
      this.get('executeQuery')();
    }
  },

  /**
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
    // If pills are open for any reason, do not allow
    // a pill to be selected
    if (this.get('isPillOpen')) {
      return;
    }

    this.send('selectGuidedPills', { pillData });
  },

  _pillsDeselected(pillData) {
    // If pills are open for any reason, do not allow
    // a pill to be deselected
    if (this.get('isPillOpen')) {
      return;
    }

    this.send('deselectGuidedPills', { pillData });
  },

  _pillEditCancelled(data) {
    this._pillsExited();
    this.send('resetGuidedPill', data);
  },

  _escapeListener(e) {
    if (isEscape(e) && this.get('selectedPills').length > 0) {
      this.send('deselectAllGuidedPills');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryPills);