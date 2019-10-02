import { warn } from '@ember/debug';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { next } from '@ember/runloop';
import RsaContextMenu from 'rsa-context-menu/components/rsa-context-menu/component';
import * as MESSAGE_TYPES from '../message-types';
import { isEscape } from 'investigate-events/util/keys';
import {
  createOperator,
  transformTextToPillData,
  valueList
} from 'investigate-events/util/query-parsing';
import { quoteComplexValues } from 'investigate-events/util/quote';
import { isSubmitClicked } from '../query-pill/query-pill-util';
import { getContextItems } from './right-click-util';
import {
  canQueryGuided,
  deselectedPills,
  enrichedPillsData,
  hasInvalidSelectedPill,
  hasTextPill,
  isPillValidationInProgress,
  selectedPills
} from 'investigate-events/reducers/investigate/query-node/selectors';
import {
  addFreeFormFilter,
  addGuidedPill,
  addIntraParens,
  addLogicalOperator,
  addPillFocus,
  addParens,
  addTextFilter,
  batchAddPills,
  cancelPillCreation,
  deleteGuidedPill,
  deleteSelectedGuidedPills,
  deleteSelectedParenContents,
  deselectAllGuidedPills,
  deselectGuidedPills,
  editGuidedPill,
  focusAndToggleLogicalOperator,
  openGuidedPillForEdit,
  removePillFocus,
  replaceLogicalOperator,
  resetGuidedPill,
  selectGuidedPills,
  selectAllPillsTowardsDirection
} from 'investigate-events/actions/guided-creators';
import { hasMinimumCoreServicesVersionForTextSearch } from 'investigate-events/reducers/investigate/services/selectors';
import { getRecentQueries, valueSuggestions } from 'investigate-events/actions/initialization-creators';
import { metaKeySuggestionsForQueryBuilder, languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import {
  CLOSE_PAREN,
  COMPLEX_FILTER,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR,
  TEXT_FILTER,
  QUERY_FILTER
} from 'investigate-events/constants/pill';

const { log } = console;// eslint-disable-line no-unused-vars

const stateToComputed = (state) => ({
  canQueryGuided: canQueryGuided(state),
  deselectedPills: deselectedPills(state),
  hasInvalidSelectedPill: hasInvalidSelectedPill(state),
  hasTextPill: hasTextPill(state),
  isPillValidationInProgress: isPillValidationInProgress(state),
  metaOptions: metaKeySuggestionsForQueryBuilder(state),
  languageAndAliasesForParser: languageAndAliasesForParser(state),
  pillsData: enrichedPillsData(state),
  selectedPills: selectedPills(state),
  canPerformTextSearch: hasMinimumCoreServicesVersionForTextSearch(state),
  valueSuggestions: state.investigate.queryNode.valueSuggestions || [],
  isValueSuggestionsCallInProgress: state.investigate.queryNode.isValueSuggestionsCallInProgress
});

const dispatchToActions = {
  addFreeFormFilter,
  addGuidedPill,
  addIntraParens,
  addLogicalOperator,
  addPillFocus,
  addParens,
  addTextFilter,
  batchAddPills,
  cancelPillCreation,
  deleteGuidedPill,
  deleteSelectedGuidedPills,
  deleteSelectedParenContents,
  deselectAllGuidedPills,
  deselectGuidedPills,
  editGuidedPill,
  focusAndToggleLogicalOperator,
  getRecentQueries,
  openGuidedPillForEdit,
  removePillFocus,
  replaceLogicalOperator,
  resetGuidedPill,
  selectAllPillsTowardsDirection,
  selectGuidedPills,
  valueSuggestions
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

const _hasCloseParenToRight = (pd, i) => pd[i] && pd[i].type === CLOSE_PAREN;

/**
 * Counts the number of open and close parentheses to the left of `position`.
 * @param {object[]} pd Array of filters
 * @param {number} position Index to stop looking for parentheses
 */
const _hasMoreOpenThanCloseParens = (pd, position) => {
  let i = 0;
  let count = 0;
  while (i < position && position < pd.length) {
    if (pd[i].type === OPEN_PAREN) {
      count++;
    } else if (pd[i].type === CLOSE_PAREN) {
      count--;
    }
    i++;
  }
  return count > 0;
};

const _isLogicalOperator = (filter = {}) => [OPERATOR_AND, OPERATOR_OR].includes(filter.type);

const _shouldAddLogicalOperator = (filter = {}) => !_isLogicalOperator(filter) && filter.type !== OPEN_PAREN;

const QueryPills = RsaContextMenu.extend({
  tagName: null,

  classNames: ['query-pills'],

  classNameBindings: [
    'isPillOpen:pill-open',
    'isPillOpenForEdit:pill-open-for-edit'
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
  cursorPosition: undefined,

  // Is a complex pill that was being edited, cancelled? This helps us to handle
  // the logic for cancelling the editing of a complex pill versus a pill that
  // is EPS based
  isComplexPillEditCancelled: false,

  // Is a pill rendered by this component open for any reason?
  isPillOpen: false,

  // Is a pill rendered by this component open for edit?
  isPillOpenForEdit: false,

  /**
   * List of all possible right click options for pills and parens
   */
  contextOptions: undefined,

  /**
   * Current list that's displayed on right-click
   */
  contextItems: undefined,

  rightClickTarget: undefined,

  /**
   * Clean up input trailing text current pill's meta, operator and value.
   * @type {Object}
   */
  cleanupInputFields: undefined,

  contextMenu({ target }) {
    const currentClass = target.classList.contains('is-selected');
    const parentClass = target.parentElement.classList.contains('is-selected');
    if (currentClass || parentClass) {
      const isParen = target.classList.contains('open-paren') ||
        target.classList.contains('close-paren') ||
        target.parentElement.classList.contains('open-paren') ||
        target.parentElement.classList.contains('close-paren');
      if (isParen) {
        this.set('contextItems', this.get('contextOptions').parens);
      } else {
        this.set('contextItems', this.get('contextOptions').pills);
      }
      this.set('rightClickTarget', currentClass ? target : target.parentElement);
      this._super(...arguments);
    } else {
      if (this.get('contextMenuService').deactivate) {
        this.get('contextMenuService').deactivate();
      }
    } // do not call super so that the browser right-click event is preserved
  },

  @computed('pillsData', 'i18n')
  pillPlaceholder: (pillsData, i18n) => pillsData.length > 0 ? '' : i18n.t('queryBuilder.placeholder'),

  @computed('pillsData')
  lastIndex: (pillsData) => pillsData.length,

  @computed('pillsData', 'cursorPosition')
  _canInsertLogicalOperator: (pillsData, cursorPosition) => {
    let ret = true;
    if (pillsData.length > 0 && cursorPosition) {
      // Return false if the preceding pill is an open paren
      ret = !(pillsData[cursorPosition - 1].type === OPEN_PAREN);
    }
    return ret;
  },

  init() {
    this._super(...arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL]: (data, position) => this._deletePressedOnFocusedPill(data, position),
      [MESSAGE_TYPES.ENTER_PRESSED_ON_FOCUSED_PILL]: (pillData) => this._enterPressedOnFocusedPill(pillData),
      [MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS]: (data) => this._fetchValueSuggestions(data),
      [MESSAGE_TYPES.PILL_ADD_CANCELLED]: (data, position) => this._pillAddCancelled(position),
      [MESSAGE_TYPES.PILL_CREATED]: (data, position) => this._createPill(QUERY_FILTER, data, position),
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
      [MESSAGE_TYPES.PILL_PASTE]: (text, position) => this._pillPaste(text, position),
      [MESSAGE_TYPES.PILL_SELECTED]: (data) => this._pillsSelected([data]),
      [MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT]: (position) => this._addFocusToLeftPill(position),
      [MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT]: (position) => this._addFocusToRightPill(position),
      [MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT]: (position) => this._pillsSelectAllToRight(position),
      [MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT]: (position) => this._pillsSelectAllToLeft(position),
      [MESSAGE_TYPES.CREATE_FREE_FORM_PILL]: (data, position) => this._createPill(COMPLEX_FILTER, data, position),
      [MESSAGE_TYPES.CREATE_TEXT_PILL]: (data, position) => this._createPill(TEXT_FILTER, data, position),
      [MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT]: (data) => this._fetchRecentQueries(data),
      [MESSAGE_TYPES.RECENT_QUERY_PILL_CREATED]: (data, position) => this._recentQueryPillCreated(data, position),
      [MESSAGE_TYPES.PILL_OPEN_PAREN]: (data, position) => this._insertParens(position),
      [MESSAGE_TYPES.PILL_CLOSE_PAREN]: (data, position) => this._moveCursorOrInsertParens(position),
      [MESSAGE_TYPES.PILL_LOGICAL_OPERATOR]: (data, position) => this._insertLogicalOperator(data, position),
      [MESSAGE_TYPES.PILL_LOGICAL_OPERATOR_CLICKED]: (data, position) => this._logicalOperatorClicked(data, position),
      [MESSAGE_TYPES.PILL_HOME_PRESSED]: (data) => this._openNewPillAtBeginning(data),
      [MESSAGE_TYPES.PILL_END_PRESSED]: (data) => this._openNewPillAtEnd(data),
      [MESSAGE_TYPES.META_DELETE_PRESSED]: (position) => this._metaDeletePressed(position)
    });
    this.setProperties({
      CLOSE_PAREN,
      COMPLEX_FILTER,
      OPEN_PAREN,
      TEXT_FILTER
    });
    this.set('contextOptions', getContextItems(this, this.get('i18n')));
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

  _fetchRecentQueries(data) {
    this.send('getRecentQueries', data);
  },

  _pillEnteredForAppend() {
    this.setProperties({
      isPillOpen: true,
      isPillOpenForEdit: false
    });
    this._pillEntered();
  },

  _pillEnteredForEdit() {
    this.setProperties({
      isPillOpen: true,
      isPillOpenForEdit: true
    });
    this._pillEntered();
  },

  _pillEnteredForInsert(position) {
    this.setProperties({
      cursorPosition: position,
      isPillOpen: true,
      isPillOpenForEdit: false
    });
    this._pillEntered();
    next(this, () => this._cleanupTrailingText(true));
  },

  _pillAddCancelled(position) {
    this.send('cancelPillCreation', position);
    this._pillsExited();
  },

  /**
   * Adjusts flags to indicate that no pills are currently open/focused
   * for edit/add
   * @param {boolean} shouldResetStartTrigger - You want to prevent the default
   * behavior of setting `cursorPosition` to `undefined` if it was set
   * to a desired value before calling this function.
   * @private
   */
  _pillsExited(shouldResetStartTrigger = true) {
    if (shouldResetStartTrigger) {
      this.set('cursorPosition', undefined);
    }
    this.setProperties({
      isPillOpen: false,
      isPillOpenForEdit: false
    });
  },

  /**
   * Manages side effects to entering a pill for edit/add
   * If entering a pill for edit or add, focus should be removed from whatever pill has it
   * And no pill should be selected
   * @private
   */
  _pillEntered() {
    this.send('removePillFocus');
    this.send('deselectAllGuidedPills');
  },

  /**
   * Adds a filter pill to state.
   * @param {string} type Type of filter to create
   * @param {*} pillData The data for the pill
   * @param {*} position The position of the pill in the array
   * @private
   */
  _createPill(type, pillData, position) {
    let messageName;
    if (type === QUERY_FILTER) {
      messageName = 'addGuidedPill';
    } else if (type === COMPLEX_FILTER) {
      messageName = 'addFreeFormFilter';
    } else if (type === TEXT_FILTER) {
      messageName = 'addTextFilter';
    } else {
      warn(`Unable to create filter, unknown type of "${type}"`, 'pillCreation.unknownType');
    }
    if (position === 0) {
      // adjust the cursorPosition to point to the new-pill-trigger
      // that's after the position where this pill will be inserted
      this.set('cursorPosition', position + 1);
      // Don't reset cursorPosition because it's pointing to where we want
      // to be after this pill is added
      this._pillsExited(false);
      this.send(messageName, { pillData, position });
    } else {
      const pillsData = this.get('pillsData');
      const previousPill = pillsData[position - 1];
      let cursorPosition = position + 1;
      let positionModifier = 0;
      // Add logical AND operator if previous pill is not some type of operator
      // AND is not an open paren
      if (_shouldAddLogicalOperator(previousPill)) {
        cursorPosition = position + 2;
        // Since we're adding the operator, we need to bump-right the pill
        positionModifier++;
        const op = createOperator(OPERATOR_AND);
        this.send('addLogicalOperator', { pillData: op, position });
      }
      this.set('cursorPosition', cursorPosition);
      this._pillsExited(false);
      this.send(messageName, { pillData, position: position + positionModifier });
    }
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
   * Called when text is pasted into the query bar. Parses the text and inserts
   * pills into state.
   * @param {String} text - The query text to parse
   * @param {Number} position - The position of the first pill to paste
   * @private
   */
  _pillPaste(text, position) {
    // Parse the string into an array of pills
    const { language, aliases } = this.get('languageAndAliasesForParser');
    const pills = transformTextToPillData(text, { language, aliases, returnMany: true });

    this.send('batchAddPills', { pillsData: pills, initialPosition: position });

    this._pillsExited();
  },

  /**
   * Will need to open a new pill trigger on the right of position that's
   * being passed in. This event is triggered when you press ARROW_RIGHT
   * from a focused pill.
   * In the case of the pill trigger on the left side, it gets re-rendered
   * because of focus changes in state, so setting 'cursorPosition'
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
   * In that case the click() will work and cursorPosition wouldn't do anything
   * because it only effects on init and that component should not get re-rendered.
   * @private
   */
  _openNewPillTriggerRight(position) {
    const pillsData = this.get('pillsData');
    if (pillsData.length === position + 1 || pillsData.length === 0) {
      // if this is the last pill in the list, no need for click().
      // Can just set takeFocus which opens the meta dropdown for new-pill-template
      this.set('takeFocus', true);
    } else {
      // otherwise, open the trigger on it's right
      this._pillEnteredForInsert(position + 1);
    }
  },

  /**
   * When hit home from an empty pill meta or focused pill data or editing a pill, it
   * focus is shifted tp the leftmost New Pill Template. If in the middle of editing a pill
   * the pill edit is cancelled before focusing the leftmost new pill template.
   */
  _openNewPillAtBeginning(data) {
    if (this.get('isPillOpenForEdit')) {
      this._pillEditCancelled(data);
    } else {
      this._pillsExited();
    }
    this._openNewPillTriggerLeft(0);
  },

  /**
  * When hit end from an empty pill meta or focused pill or editing a pill, it
  * focus is shifted tp the rightmost New Pill Template. If in the middle of editing a pill
  * the pill edit is cancelled before focusing the rightmost new pill template.
  */
  _openNewPillAtEnd(data) {
    if (this.get('isPillOpenForEdit')) {
      this._pillEditCancelled(data);
    } else {
      this._pillsExited();
    }
    const pillsData = this.get('pillsData');
    this._openNewPillTriggerRight(pillsData.lastIndex);
  },


  /**
   * Sends out delete action through a focused pill
   * @private
   */
  _deletePressedOnFocusedPill(pillData, position) {
    // delete the pill
    this.send('deleteSelectedGuidedPills', pillData, true);
    const { type } = pillData;
    // if pill or open-paren is deleted, the position is assigned to the next pill.
    // if the close-paren is deleted, open-paren is deleted too so the position has
    // to be re-caliberated.
    if (type === CLOSE_PAREN) {
      position = position - 1;
    }
    const pillsData = this.get('pillsData');
    if (position === pillsData.length || pillsData.length === 0) {
      // if last pill open new pill trigger to the end
      this._openNewPillTriggerRight(pillsData.lastIndex);
    } else {
      // if not the last pill, then shift focus to the next pill on right
      this._addFocusToRightPill(position);
    }
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
      this._pillsExited();
      this.send('addPillFocus', position - 1);
    }
  },

  _addFocusToRightPill(position) {
    const pillsData = this.get('pillsData');
    if (position < pillsData.length) {
      this._pillsExited();
      this.send('addPillFocus', position);
    }
  },
  /**
   * When hit delete from an empty pill meta, the focus is shifted to
   * the next right pill.
   */
  _metaDeletePressed(position) {
    this._addFocusToRightPill(position);
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
    // Fetch value suggestions to update array in state
    if (pillData.type === QUERY_FILTER) {
      // Remove quotes from the string, except around values that require them to stay the way they are.
      const filter = pillData.value ? quoteComplexValues(valueList(pillData.value).map((v) => v.value)).join(',') : '';
      this._fetchValueSuggestions({
        metaName: pillData.meta,
        filter
      });
    }
    this.send('openGuidedPillForEdit', { pillData });
    this._pillEnteredForEdit();
  },

  _submitQuery() {
    const {
      canQueryGuided,
      executeQuery,
      isPillValidationInProgress
    } = this.getProperties('canQueryGuided', 'executeQuery', 'isPillValidationInProgress');
    if (canQueryGuided && !isPillValidationInProgress) {
      executeQuery();
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
    if (data.complexFilterText) {
      this.set('isComplexPillEditCancelled', true);
    }
    this._pillsExited();
    this.send('resetGuidedPill', data);
  },

  // If escape is pressed, deselect all pills
  // and remove focus from any pill
  _escapeListener(e) {
    if (isEscape(e)) {
      const selectedPills = this.get('selectedPills');
      const cancelledComplexPill = this.get('isComplexPillEditCancelled');
      if (cancelledComplexPill || (selectedPills && selectedPills.length)) {
        this.send('deselectAllGuidedPills');
        this.set('isComplexPillEditCancelled', false);
      } else {
        this.send('removePillFocus');
      }
    }
  },

  _clickListener(e) {
    if (!isEventFiredFromQueryPill(e)) {
      this.send('removePillFocus');
    }
    if (isSubmitClicked(e.target)) {
      this._cleanupTrailingText();
    }
  },

  // Right clicking anywhere on the window should remove
  // focus from a pill
  _rightClickListener(e) {
    if (!isEventFiredFromQueryPill(e)) {
      this.send('removePillFocus');
    }
  },

  _cleanupTrailingText(fromPillTrigger = false) {
    this.set('cleanupInputFields', { fromPillTrigger });
    // reset the field
    next(this, () => {
      if (!this.isDestroyed || !this.isDestroying) {
        this.set('cleanupInputFields', undefined);
      }
    });
  },

  _recentQueryPillCreated(data, position) {
    const { language, aliases } = this.get('languageAndAliasesForParser');
    const pills = transformTextToPillData(data, { language, aliases, returnMany: true });
    this.send('batchAddPills', { pillsData: pills, initialPosition: position });
    this._pillsExited();
  },

  _insertParens(position) {
    let cursorPosition = position + 1;
    let positionModifier = 0;
    if (position > 0) {
      const pillsData = this.get('pillsData');
      const previousPill = pillsData[position - 1];
      // Add logical AND operator if one's missing to the left
      if (_shouldAddLogicalOperator(previousPill)) {
        cursorPosition = position + 2;
        // Since we're adding the operator, we need to bump-right the parens
        positionModifier++;
        const op = createOperator(OPERATOR_AND);
        this.send('addLogicalOperator', { pillData: op, position });
      }
    }
    this.set('cursorPosition', cursorPosition);
    this._pillsExited(false);
    this.send('addParens', { position: position + positionModifier });
  },

  /**
   * Do we have more open parens than close parens?
   * Yes, insert ") AND (".
   * No, move to the right.
   * @paren {number} position The position to move from.
   * @private
   */
  _moveCursorOrInsertParens(position) {
    const pillsData = this.get('pillsData');
    if (_hasCloseParenToRight(pillsData, position)) {
      this._moveToRightFrom(pillsData, position);
    } else if (_hasMoreOpenThanCloseParens(pillsData, position)) {
      this._pillsExited();
      this.send('addIntraParens', { position });
      next(this, this._addFocusToRightPill, position + 2);
    }
  },

  /**
   * Move cursor one position to the right.
   * @paren {number} position The position to move from.
   */
  _moveToRightFrom(pillsData, position) {
    const trigger = document.querySelectorAll('.new-pill-trigger-container')[position];
    const input = trigger.querySelector('input');
    // Escape out of current pill creation. This handles any cleanup.
    input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    // Allow time for the simulated ESC key to do it's thing before moving
    // to the new NPT
    next(this, this._openNewPillTriggerRight, position);
  },

  /**
   * Request value suggestions
   */
  _fetchValueSuggestions({ metaName, filter }) {
    this.send('valueSuggestions', metaName, filter);
  },

  /**
   * Handles requests for logical operators. This will either insert a new
   * logical operator or replace an existing operator.
   * @paren {Object} data
   * @paren {Object} data.operator - The new logical operator
   * @paren {Object} data.pillData - The pill data this message was spawned
   * from. Used for determining if an existing pill was edited with the intent
   * to insert a logical operator before it.
   * @paren {number} position - Position within pillsData to insert the operator
   */
  _insertLogicalOperator(data, position) {
    const { operator, pillData } = data;
    const isEditing = pillData ? pillData.isEditing : false;
    const { _canInsertLogicalOperator, pillsData } =
      this.getProperties('_canInsertLogicalOperator', 'pillsData');
    const previousPill = pillsData[position - 1];
    if (_isLogicalOperator(previousPill)) {
      this.send('replaceLogicalOperator', { pillData: operator, position });
    } else if (_canInsertLogicalOperator || isEditing) {
      this.set('cursorPosition', position + 1);
      this._pillsExited(false);
      this.send('addLogicalOperator', { pillData: operator, position });
      this._openNewPillTriggerRight(position);
    }
  },

  /**
   * Handles dispatching to state when logical operators are clicked
   */
  _logicalOperatorClicked(pillData, position) {
    this.send('focusAndToggleLogicalOperator', { pillData, position });
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryPills);