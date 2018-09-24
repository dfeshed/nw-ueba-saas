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
  addGuidedPillFocus,
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
  addGuidedPillFocus,
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

const isEventFiredFromQueryPill = (event) => {
  const { target: clickedElement } = event;
  const { parentElement } = clickedElement;
  let includesQueryPillClass = true;
  if (parentElement) {
    const parentClickedClass = parentElement.className;
    const classNameIsString = (typeof parentClickedClass === 'string');
    includesQueryPillClass = parentClickedClass && classNameIsString && parentClickedClass.includes('query-pill');
  }
  return includesQueryPillClass;
};

const QueryPills = RsaContextMenu.extend({
  tagName: null,

  classNames: ['query-pills'],

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

  // Was a pill escaped in the middle of an edit?
  isPillEditCancelled: false,

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
        // Delete all deselected pills first
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
        // deselect all the pills and remove focus. Can't trigger this first, as
        // route action picks up selected pills from state to executeQ
        _this.send('removeGuidedPillFocus');
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
      [MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL]: (data) => this._deletePressedOnFocusedPill(data),
      [MESSAGE_TYPES.ENTER_PRESSED_ON_FOCUSED_PILL]: (pillData) => this._enterPressedOnFocusedPill(pillData),
      [MESSAGE_TYPES.PILL_ADD_CANCELLED]: (data, position) => this._pillAddCancelled(data, position),
      [MESSAGE_TYPES.PILL_CREATED]: (data, position) => this._pillCreated(data, position),
      [MESSAGE_TYPES.PILL_DELETED]: (data) => this._pillDeleted(data),
      [MESSAGE_TYPES.PILL_DESELECTED]: (data) => this._pillsDeselected([data]),
      [MESSAGE_TYPES.PILL_EDIT_CANCELLED]: (data) => this._pillEditCancelled(data),
      [MESSAGE_TYPES.PILL_EDITED]: (data) => this._pillEdited(data),
      [MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW]: () => this._pillEnteredForAppend(),
      [MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW]: (pillData, position) => this._pillEnteredForInsert(position),
      [MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT]: (position) => this._openNewPillTriggerLeft(position),
      [MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT]: (position) => this._openNewPillTriggerRight(position),
      [MESSAGE_TYPES.PILL_INTENT_TO_QUERY]: () => this._submitQuery(),
      [MESSAGE_TYPES.PILL_OPEN_FOR_EDIT]: (pillData) => this._pillOpenForEdit(pillData),
      [MESSAGE_TYPES.PILL_SELECTED]: (data) => this._pillsSelected([data]),
      [MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT]: (position) => this._addFocusToLeftPill(position),
      [MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT]: (position) => this._addFocusToRightPill(position),
      [MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT]: (position) => this._pillsSelectAllToRight(position),
      [MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT]: (position) => this._pillsSelectAllToLeft(position)
    });
  },

  didInsertElement() {
    this._super(...arguments);

    // escape
    const _boundEscapeListener = this._escapeListener.bind(this);
    // saving the event handler fn so that it can be removed later
    this.set('_boundEscapeListener', _boundEscapeListener);
    window.addEventListener('keydown', _boundEscapeListener);

    // click
    const _boundClickListener = this._clickListener.bind(this);
    this.set('_boundClickListener', _boundClickListener);
    window.addEventListener('click', _boundClickListener);

    // right-click
    const _boundRightClickListener = this._rightClickListener.bind(this);
    this.set('_boundRightClickListener', _boundRightClickListener);
    window.addEventListener('contextmenu', _boundRightClickListener);
  },

  willDestroyElement() {
    this._super(...arguments);
    window.removeEventListener('keydown', this.get('_boundEscapeListener'));
    window.removeEventListener('click', this.get('_boundClickListener'));
    window.removeEventListener('contextmenu', this.get('_boundRightClickListener'));
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
   * Will need to open a new pill trigger on the right of position that's
   * being passed in. This event is triggered when you press ARROW_RIGHT
   * from a focused pill.
   * In the case of the pill trigger on the left side, it gets re-rendered
   * because of focus changes in state, so setting 'startTriggeredPosition'
   * for it should just work without any click().
   * @private
   */
  _openNewPillTriggerLeft(position) {
    this._pillEnteredForInsert(position);
  },

   /**
   * Will need to open a new pill trigger on the right of position that's
   * being passed in. This event is triggered when you press ARROW_RIGHT
   * from a focused pill.
   * In the case of the pill trigger on the right side, it doesn't get
   * re-rendered (because the triggers are rendered on the left side within the loop).
   * In that case the click() will work and startTriggeredPosition wouldn't do anything
   * because it only effects on init and that component should not get re-rendered.
   * @private
   */
  _openNewPillTriggerRight(position) {
    const pillsData = this.get('pillsData');
    if (pillsData.length === position + 1) {
      // if this is the last pill in the list, no need for click().
      // Can just set takeFocus which opens the meta dropdown for new-pill-template
      this.set('takeFocus', true);
    } else {
      // otherwise, open the trigger on it's right
      const newPillTriggers = document.querySelectorAll('.new-pill-trigger');
      const triggerToOpen = newPillTriggers[position + 1];
      triggerToOpen.click();
    }
  },

  /**
   * Sends out delete action through a focused pill
   * @private
   */
  _deletePressedOnFocusedPill(pillData) {
    this.send('deleteSelectedGuidedPills', pillData);
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
  _enterPressedOnFocusedPill(pillData) {
    this._pillOpenForEdit(pillData);
  },

  _addFocusToLeftPill(position) {
    if (position !== 0) {
      this.send('addGuidedPillFocus', position - 1);
      this._pillAddCancelled();
    }
  },

  _addFocusToRightPill(position) {
    const pillsData = this.get('pillsData');
    if (position < pillsData.length) {
      this.send('addGuidedPillFocus', position);
      this._pillAddCancelled();
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

    // Set this flag to true as we'd want the global
    // window escape listener to ignore this event
    // and still retain focus
    this.set('isPillEditCancelled', true);
    this.send('resetGuidedPill', data);
  },

  // If escape is pressed, deselect all pills
  // and remove focus from any pill
  _escapeListener(e) {
    if (isEscape(e)) {
      this.send('deselectAllGuidedPills');
      if (!this.get('isPillEditCancelled')) {
        this.send('removeGuidedPillFocus');
      } else {
        // Toggle this flag again so the consecutive escape can
        // remove focus
        this.set('isPillEditCancelled', false);
      }
    }
  },

  _clickListener(e) {
    if (!isEventFiredFromQueryPill(e)) {
      this.send('removeGuidedPillFocus');
    }
  },

  // Right clicking anywhere on the window should remove
  // focus from a pill
  _rightClickListener(e) {
    if (!isEventFiredFromQueryPill(e)) {
      this.send('removeGuidedPillFocus');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryPills);