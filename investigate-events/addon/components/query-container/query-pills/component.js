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
import { getContextItems } from './context-menu-util';
import {
  canQueryGuided,
  deselectedPills,
  enrichedPillsData,
  hasInvalidSelectedPill,
  hasTextPill,
  pillsInsideParens,
  isPillValidationInProgress,
  selectedPills,
  shouldUseStashedPills
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
  editGuidedPill,
  focusAndToggleLogicalOperator,
  openGuidedPillForEdit,
  removePillFocus,
  replaceLogicalOperator,
  resetGuidedPill,
  wrapWithParens
} from 'investigate-events/actions/pill-creators';
import {
  deselectAllGuidedPills,
  deselectGuidedPills,
  selectGuidedPills,
  selectAllPillsTowardsDirection
} from 'investigate-events/actions/pill-selection-creators';

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
import {
  findPositionAfterEmptyParensDeleted,
  isLogicalOperator,
  isValidToWrapWithParens,
  doPillsContainTextPill,
  selectedPillIndexes,
  getAdjacentDeletableLogicalOperatorAt
} from 'investigate-events/actions/pill-utils';
const { log } = console;// eslint-disable-line no-unused-vars

const stateToComputed = (state, attrs = {}) => ({
  canQueryGuided: canQueryGuided(state),
  deselectedPills: deselectedPills(state),
  hasInvalidSelectedPill: hasInvalidSelectedPill(state),
  hasTextPill: hasTextPill(state),
  pillsInsideParens: pillsInsideParens(state),
  isPillValidationInProgress: isPillValidationInProgress(state),
  metaOptions: metaKeySuggestionsForQueryBuilder(state),
  languageAndAliasesForParser: languageAndAliasesForParser(state),
  pillsData: shouldUseStashedPills(state) && attrs.isPrimary ? enrichedPillsData(state).originalPills : enrichedPillsData(state).pillsData,
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
  valueSuggestions,
  wrapWithParens
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

const isEventFromContextMenus = (event) => {
  const { target: clickedElement } = event;
  if (typeof clickedElement.className === 'string') {
    // TODO context menu component should have string type className, but in case it doesn't,
    // we should assert from classList ?
    return clickedElement.className.includes('context-menu__item');
  } else {
    return clickedElement.classList.contains('context-menu__item') || clickedElement.classList.contains('context-menu__item__label');
  }
};

const _hasCloseParenToRight = (pd, i) => pd[i] && pd[i].type === CLOSE_PAREN;


const _hasOperatorToTheLeft = (pd, i) => {
  // get pill to the left
  const pill = pd[i - 1];
  return isLogicalOperator(pill);
};

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
   * Is this the component that resides inside query-bar?
   * In order to re-use this component, we stash pillsData as originalPills
   * and toggle between them when required.
   */
  isPrimary: undefined,

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

    // Need right click actions only when this is the primary component
    // and something is selected
    if ((currentClass || parentClass) && this.get('isPrimary')) {
      const isParen = target.classList.contains('open-paren') ||
        target.classList.contains('close-paren') ||
        target.parentElement.classList.contains('open-paren') ||
        target.parentElement.classList.contains('close-paren');
      if (isParen) {
        this.set('contextItems', this.get('contextOptions').parens);
      } else {
        const pills = this.get('pillsData');
        const { startIndex, endIndex } = selectedPillIndexes(pills);

        // Special case: If there is a text filter, we want to compute
        // a label at run time. Thus, we pass the label to wrapInParens fn.
        let label;
        if (doPillsContainTextPill(pills, startIndex, endIndex)) {
          label = this.get('i18n').t('queryBuilder.wrapInParensNotAllowed');
        } else {
          label = this.get('i18n').t('queryBuilder.wrapParens');
        }
        this.set('contextItems', this.get('contextOptions').pills(label));
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

  init() {
    this._super(...arguments);
    this.set('_messageHandlerMap', {
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
      [MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT]: (data, position) => this._openNewPillTriggerLeft(position),
      [MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT]: (data, position) => this._openNewPillTriggerRight(position),
      [MESSAGE_TYPES.PILL_INTENT_TO_QUERY]: () => this._submitQuery(),
      [MESSAGE_TYPES.PILL_OPEN_FOR_EDIT]: (pillData) => this._pillOpenForEdit(pillData),
      [MESSAGE_TYPES.PILL_PASTE]: (data, position) => this._pillPaste(data, position),
      [MESSAGE_TYPES.PILL_SELECTED]: (data) => this._pillsSelected([data]),
      [MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT]: (data, position) => this._addFocusToLeftPill(position),
      [MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT]: (data, position) => this._addFocusToRightPill(position),
      [MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT]: (data, position) => this._pillsSelectAllToRight(position),
      [MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT]: (data, position) => this._pillsSelectAllToLeft(position),
      [MESSAGE_TYPES.CREATE_FREE_FORM_PILL]: (data, position) => this._createPill(COMPLEX_FILTER, data, position),
      [MESSAGE_TYPES.CREATE_TEXT_PILL]: (data, position) => this._createPill(TEXT_FILTER, data, position),
      [MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT]: (data) => this._fetchRecentQueries(data),
      [MESSAGE_TYPES.RECENT_QUERY_PILL_CREATED]: (data, position) => this._recentQueryPillCreated(data, position),
      [MESSAGE_TYPES.PILL_OPEN_PAREN]: (data, position) => this._insertParens(position),
      [MESSAGE_TYPES.PILL_CLOSE_PAREN]: (data, position) => this._handleCloseParentheses(position),
      [MESSAGE_TYPES.PILL_LOGICAL_OPERATOR]: (data, position) => this._insertLogicalOperator(data, position),
      [MESSAGE_TYPES.PILL_LOGICAL_OPERATOR_TOGGLED]: (data, position) => this._logicalOperatorClicked(data, position),
      [MESSAGE_TYPES.PILL_HOME_PRESSED]: (data) => this._openNewPillAtBeginning(data),
      [MESSAGE_TYPES.PILL_END_PRESSED]: (data) => this._openNewPillAtEnd(data),
      [MESSAGE_TYPES.WRAP_SELECTED_PILLS_WITH_PARENS]: () => this._wrapSelectedPillsWithParens(),
      [MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED]: (data, position) => this._deleteOrBackspacePressed(data, position)
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
    // Close dropdowns
    this._pillsExited();
    if (position === 0) {
      this.send(messageName, {
        pillData,
        position
      });
      position++;
      // Are there any existing pills other than the one you just created above?
      // If so, add an AND operator afterwards and forward the cursor to the NPT
      // to the right of the new operator
      if (this.pillsData.length > 1 && !_isLogicalOperator(this.pillsData[1])) {
        this.send('addLogicalOperator', {
          pillData: createOperator(OPERATOR_AND),
          position
        });
        position++;
      }
    } else {
      const previousPill = this.pillsData[position - 1];
      if (_shouldAddLogicalOperator(previousPill)) {
        this.send('addLogicalOperator', {
          pillData: createOperator(OPERATOR_AND),
          position
        });
        position++;
      }
      this.send(messageName, {
        pillData,
        position
      });
      position++;
      const nextPill = this.pillsData[position];
      if (nextPill && !_isLogicalOperator(nextPill) && nextPill.type !== CLOSE_PAREN) {
        this.send('addLogicalOperator', {
          pillData: createOperator(OPERATOR_AND),
          position
        });
        position++;
      }
    }
    // Set cursor to the right of the newly created pill
    this.set('cursorPosition', position);
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
   * This function will look to each "side" of the insertion point to determine
   * if it needs to add a logical operator.
   * @param {Object[]} newPills - List of new pills to add to `pillsData`
   * @param {number} position - Index within `pillsData` to insert `newPills`.
   */
  _addOperatorIfNeeded(newPills, position) {
    const previousPill = this.pillsData[position - 1];
    const nextPill = this.pillsData[position];
    let newPillsWithOperator;
    if (previousPill && !_isLogicalOperator(previousPill) && previousPill.type !== OPEN_PAREN) {
      newPillsWithOperator = [
        createOperator(OPERATOR_AND),
        ...newPills
      ];
    } else if (nextPill && !_isLogicalOperator(nextPill) && nextPill.type !== CLOSE_PAREN) {
      newPillsWithOperator = [
        ...newPills,
        createOperator(OPERATOR_AND)
      ];
    }
    return newPillsWithOperator || newPills;
  },

  /**
   * Called when text is pasted into the query bar. Parses the text and inserts
   * pills into state.
   * @param {String} data - The query text to parse
   * @param {Number} position - The position of the first pill to paste
   * @private
   */
  _pillPaste(data, position) {
    const { language, aliases } = this.get('languageAndAliasesForParser');
    let pills = transformTextToPillData(data, { language, aliases, returnMany: true });
    this._pillsExited();
    if (this.pillsData.length > 0) {
      pills = this._addOperatorIfNeeded(pills, position);
    }
    this.send('batchAddPills', { pillsData: pills, initialPosition: position });
    this.set('cursorPosition', position + pills.length);
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
    const { type } = pillData;
    const allowPillDeletion = !(type === OPERATOR_AND || type === OPERATOR_OR);
    if (allowPillDeletion) {
      // delete the pill
      this.send('deleteSelectedGuidedPills', pillData, true);
      // if the pill is focused, then execute the logic to assign focus to the adjacent pills.
      if (!pillData.isSelected && pillData.isFocused) {
        this._moveFocusToRight(pillData, position);
      }
    } else {
      // the position to be passed to _addFocusToRightPill is the next pill position.
      // Since pill is not deleted for the next pill position we have to increment position by 1.
      this._addFocusToRightPill(position + 1);
    }
  },

  _moveFocusToRight(pillData, position) {
    const pillsData = this.get('pillsData');
    // position could be greater than number of remaining pills, as logical operator is deleted along with the pill.
    if (position >= pillsData.length || pillsData.length === 0) {
      // if last pill open new pill trigger to the end
      this._openNewPillTriggerRight(pillsData.lastIndex);
    } else {
      const type = pillData?.type;
      // if pill or open-paren is deleted, the position is assigned to the next pill.
      // if the close-paren is deleted, open-paren is deleted too so the position has
      // to be re-caliberated.
      if (type === CLOSE_PAREN) {
        position = position - 1;
      }
      // if not the last pill, then shift focus to the next pill on right
      this._addFocusToRightPill(position);
    }
  },

  /**
   * Handles backspace action on a focused pill by deleting the pill and moving
   * the focus to the pill on the left. If there are no pills on the left, the
   * the focus to shift to the empty pill on the left.
   * @private
   */
  _backspacePressedOnFocusedPill(pillData, position) {
    const { type } = pillData;
    const allowPillDeletion = !(type === OPERATOR_AND || type === OPERATOR_OR);

    if (allowPillDeletion) {
      // delete the pill
      this.send('deleteSelectedGuidedPills', pillData, true);
      // if the pill is focused, then execute the logic to assign focus to the adjacent pills.
      if (!pillData.isSelected && pillData.isFocused) {
        this._moveFocusToLeft(pillData, position);
      }
    } else {
      this._addFocusToLeftPill(position);
    }
  },

  _moveFocusToLeft(pillData, position) {
    const pillsData = this.get('pillsData');
    if (pillsData.length === 0) {
      // when the last existing pill is removed and there are no pills we do not have new pill
      // triggers.we have new pill template which needs to be focused.
      this._openNewPillTriggerRight(0);
    } else if (position === 0) {
      // when the first pill is removed using backspace but one or more pills are present,
      // focus shifts to the first new pill trigger.
      this._openNewPillTriggerLeft(0);
    } else {
      const type = pillData?.type;
      // if pill or open-paren is deleted, the position is assigned to the next pill.
      // if the close-paren is deleted, open-paren is deleted too so the position has
      // to be re-caliberated.
      if (type === CLOSE_PAREN) {
        position = position - 1;
      }
      const isFocusedParens = (type === CLOSE_PAREN || type === OPEN_PAREN) && !!pillData && pillData.isFocused && !pillData.isSelected;
      const prevPill = pillsData[position - 1];
      const isEmptyPillWithoutPrevOperator = !type && !!prevPill && !_isLogicalOperator(prevPill);
      const isLogicalOperatorDeleted = pillsData.length > 0 && position > 0 && !isFocusedParens && !isEmptyPillWithoutPrevOperator;
      position = isLogicalOperatorDeleted ? position - 1 : position;
      // if not the first pill, then shift focus to the next pill on left
      this._addFocusToLeftPill(position);
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

  /**
   * Handles moving focus from a new pill trigger to a pill on the left.
   * @param {number} position - The index of the NPT that we're moving away
   * from. The index for a NPT always matches the index of the pill to its right.
   */
  _addFocusToLeftPill(position) {
    if (position !== 0) {
      this._pillsExited();
      const nextPill = this.pillsData[position];
      const prevPill = this.pillsData[position - 1];
      const isCloseParenToRight = nextPill && nextPill.type === CLOSE_PAREN;
      const isOperatorToLeft = _isLogicalOperator(prevPill);
      if (isCloseParenToRight && isOperatorToLeft) {
        // Someone typed "( P && _ )", they're at the "_" and pressed the left
        // arrow. We need to remove the operator and focus on the "P" to the
        // left. We run the addPillFocus in the next runloop so that it happens
        // after some logic that removes all pill focus caused by the NPT losing
        // focus. We also send "position - 2" because we need to skip over the
        // operator we just deleted.
        this.send('deleteGuidedPill', { pillData: [this.pillsData[position - 1]] });
        next(this, this.send, 'addPillFocus', position - 2);
      } else {
        // Normal left movement
        this.send('addPillFocus', position - 1);
      }
    }
  },

  /**
   * Handles moving focus from a new pill trigger to a pill on the right.
   * @param {number} position - The index of the NPT that we're moving away
   * from. The index for a NPT always matches the index of the pill to its right.
   */
  _addFocusToRightPill(position) {
    if (!this.isEditing) {
      const focusPosition = findPositionAfterEmptyParensDeleted(this.pillsData, position);
      const deleteLogicalOperator = getAdjacentDeletableLogicalOperatorAt(this.pillsData, position);
      this._pillAddCancelled(position);
      position = focusPosition;
      position = deleteLogicalOperator ? position - 1 : position;
    }
    if (position < this.pillsData.length) {
      this._pillsExited();
      const nextPill = this.pillsData[position];
      const prevPill = this.pillsData[position - 1];
      const isCloseParenToRight = nextPill && nextPill.type === CLOSE_PAREN;
      const isOperatorToLeft = _isLogicalOperator(prevPill);
      if (isCloseParenToRight && isOperatorToLeft) {
        // Someone typed "( P && _ )", they're at the "_" and pressed the right
        // arrow. We need to remove the operator and focus on the ")" to the
        // right. We run the addPillFocus in the next runloop so that it happens
        // after some logic that removes all pill focus caused by the NPT losing
        // focus. We also send "position - 1" because we removed the operator,
        // so the ")" moves back one space.
        this.send('deleteGuidedPill', { pillData: [this.pillsData[position - 1]] });
        next(this, this.send, 'addPillFocus', position - 1);
      } else {
        // Normal right movement
        this.send('addPillFocus', position);
      }
    } else {
      this._openNewPillTriggerRight(this.pillsData.lastIndex);
    }
  },

  /**
   * Handles events from pressing delete or backspace on empty pill or a focused pill.
   */
  _deleteOrBackspacePressed(data, position) {
    const { isFocusedPill, isDeleteEvent, isBackspaceEvent } = data;
    const pillData = this.pillsData[position];
    if (isFocusedPill && isDeleteEvent) {
      this._deletePressedOnFocusedPill(pillData, position);
    } else if (isFocusedPill && isBackspaceEvent) {
      this._backspacePressedOnFocusedPill(pillData, position);
    } else if (isDeleteEvent || isBackspaceEvent) {
      // This delete or backspace event is generated from pill meta.
      const previousPillData = this.pillsData[position - 1];
      let calibratePosition = false;
      let focusPosition = position;
      if (!!pillData && !!previousPillData && pillData.type === CLOSE_PAREN && previousPillData.type === OPEN_PAREN) {
        // An empty paren or nested empty parens are added and then delete or backspace is pressed. Along
        // with the empty parens the added logical operator is also removed.
        calibratePosition = true;
        focusPosition = findPositionAfterEmptyParensDeleted(this.pillsData, position);
      } else if (!pillData && !!previousPillData && _isLogicalOperator(previousPillData)) {
        calibratePosition = true;
      }
      this._pillAddCancelled(position);
      if (isDeleteEvent) {
        // deleting an empty pill would shift the focus to the pill on the right.
        if (calibratePosition) {
          this._moveFocusToRight(null, focusPosition);
        } else {
          this._moveFocusToRight(null, position);
        }
      }
      if (isBackspaceEvent) {
        if (calibratePosition) {
          this._moveFocusToLeft(null, focusPosition);
        } else {
          this._moveFocusToLeft(null, position);
        }
      }
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
    const { language, aliases } = this.get('languageAndAliasesForParser');
    let newPillsData = [ pillData ];
    const pillsData = this.get('pillsData');
    // Attempt to parse the edited text if the user edited a complex pill. That way,
    // if they corrected an error it will turn into a guided pill.
    if (pillData.type === COMPLEX_FILTER) {
      newPillsData = transformTextToPillData(pillData.complexFilterText, { language, aliases, returnMany: true });
      newPillsData[0].id = pillData.id;
    }
    const position = pillsData.map((pD) => pD.id).indexOf(pillData.id);
    this._pillsExited();
    if (newPillsData.length === 1) {
      this.send('editGuidedPill', { pillData: newPillsData[0], position });
    } else {
      // As long as pill deletion is done on pill id, it is okay to send the newly modified pill data
      // because here it still has the old id
      this.send('deleteGuidedPill', { pillData: [ pillData ] });
      this.send('batchAddPills', { pillsData: newPillsData, initialPosition: position });
    }
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
    // Events coming from context menus are technically originating from query pill
    if (!isEventFiredFromQueryPill(e) && !isEventFromContextMenus(e)) {
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
    let pills = transformTextToPillData(data, { language, aliases, returnMany: true });
    this._pillsExited();
    if (this.pillsData.length > 0) {
      pills = this._addOperatorIfNeeded(pills, position);
    }
    this.send('batchAddPills', { pillsData: pills, initialPosition: position });
    this.set('cursorPosition', position + pills.length);
  },

  _insertParens(position) {
    let cursorPosition = position + 1;
    let positionModifier = 0;
    const pillsData = this.get('pillsData');
    if (position > 0) {
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
    if (position <= pillsData.length) {
      const nextPill = pillsData[position];
      // Add logical AND operator if one's missing to the right
      if (!!nextPill && !_isLogicalOperator(nextPill) && nextPill.type !== CLOSE_PAREN) {
        const operatorPosition = position + positionModifier;
        const op = createOperator(OPERATOR_AND);
        this.send('addLogicalOperator', { pillData: op, position: operatorPosition });
      }
    }
    this.set('cursorPosition', cursorPosition);
    this._pillsExited(false);
    this.send('addParens', { position: position + positionModifier });
  },

  /**
   * Makes decisions on how to handle a close parentheses.
   * 1) Is there a close paren immediately to the right? Move focus to the right of it.
   * 2) Is there an operator immediately to the left? Do nothing.
   * 3) Do we have more open parens than close parens? Insert ") AND (".
   * @paren {number} position The cuttent position.
   * @private
   */
  _handleCloseParentheses(position) {
    const pillsData = this.get('pillsData');
    if (_hasCloseParenToRight(pillsData, position)) {
      this._moveToRightFrom(pillsData, position);
    } else if (_hasOperatorToTheLeft(pillsData, position)) {
      // Do nothing as this is invalid syntax. Perform this check before the
      // _hasMoreOpenThanCloseParens below as that could pass, causing us to
      // insert intra-parens where we shouldn't.
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
    const previousPill = this.pillsData[position - 1];
    if (_isLogicalOperator(previousPill)) {
      this.send('replaceLogicalOperator', { pillData: operator, position });
    } else if (this._canInsertLogicalOperator(this.pillsData, position) || isEditing) {
      this.set('cursorPosition', position + 1);
      this._pillsExited(false);
      this.send('addLogicalOperator', { pillData: operator, position });
      this._openNewPillTriggerRight(position);
    }
  },

  _canInsertLogicalOperator: (pillsData, cursorPosition) => {
    let ret = true;
    if (pillsData.length > 0 && cursorPosition) {
      // Return false if the preceding pill is an open paren
      ret = !(pillsData[cursorPosition - 1].type === OPEN_PAREN);
    }
    return ret;
  },

  /**
   * Handles dispatching to state when logical operators are clicked
   */
  _logicalOperatorClicked(pillData, position) {
    this.send('focusAndToggleLogicalOperator', { pillData, position });
  },

  _wrapSelectedPillsWithParens() {
    const pills = this.get('pillsData');
    const { startIndex, endIndex } = selectedPillIndexes(pills);
    if (
      isValidToWrapWithParens(pills, startIndex, endIndex) &&
      !this.get('hasInvalidSelectedPill') &&
      !doPillsContainTextPill(pills, startIndex, endIndex)
    ) {
      this.send('wrapWithParens', { startIndex, endIndex });
      this.send('deselectAllGuidedPills');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryPills);
