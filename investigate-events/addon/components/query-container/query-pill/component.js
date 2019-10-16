import Component from '@ember/component';
import { later, next, throttle } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { isEmpty } from '@ember/utils';
import computed, { alias, and, equal } from 'ember-computed-decorators';
import _ from 'lodash';
import * as MESSAGE_TYPES from '../message-types';
import {
  convertTextToPillData,
  createFilter,
  createOperator,
  valueList
} from 'investigate-events/util/query-parsing';
import {
  determineNewComponentPropsFromPillData,
  isSubmitClicked,
  resultsCount
} from './query-pill-util';
import {
  COMPLEX_FILTER,
  QUERY_FILTER,
  TEXT_FILTER,
  AFTER_OPTION_TAB_META,
  AFTER_OPTION_TAB_RECENT_QUERIES,
  PILL_META_DATA_SOURCE,
  PILL_OPERATOR_DATA_SOURCE,
  PILL_VALUE_DATA_SOURCE,
  PILL_RECENT_QUERY_DATA_SOURCE
} from 'investigate-events/constants/pill';

const { log } = console; // eslint-disable-line no-unused-vars

const RESET_PROPS = {
  isActive: true,
  isMetaActive: true,
  isOperatorActive: false,
  isValueActive: false,
  selectedMeta: null,
  selectedOperator: null,
  valueString: null
};

export default Component.extend({
  classNames: ['pill', 'query-pill'],
  classNameBindings: ['isActive', 'isEditing', 'isInvalid', 'isSelected', 'isExpensive', 'isFocused', 'activeTab'],
  attributeBindings: ['title', 'position'],
  i18n: service(),

  queryCounter: service(),

  /**
   * After options active tab
   */
  activePillTab: AFTER_OPTION_TAB_META,

  /**
   * Are all Core Services at a revision that allows Text searching to be
   * performed? Passed to subcomponents, not used directly.
   * @type {boolean}
   * @public
   */
  canPerformTextSearch: true,

  /**
   * Clean up input trailing text from meta, operator and value.
   * @type {Object}
   */
  cleanupInputFields: undefined,

  /**
   * Inform it's children that trailing text needs to be cleared out.
   * @type {boolean}
   */
  shouldCleanInputFields: false,

  /**
   * The position of this pill relative to other pills.
   * Used when messaging up to parent.
   * @type {Number}
   * @public
   */
  position: null,

  /**
   * Pre-populated Pill Data.
   * @type {Object}
   * @public
   */
  pillData: null,

  /**
   * Does this component currently have focus?
   * @type {boolean}
   * @public
   */
  isActive: true,

  /**
   * Whether or not we have a text pill across all pills
   * @type {Object}
   * @public
   */
  hasTextPill: null,

  /**
   * List of meta provided from above and simply
   * passed through to meta component
   * @type {Object}
   * @public
   */
  metaOptions: null,

  /**
   * Object with keys `language` and `aliases`
   */
  languageAndAliasesForParser: null,

  /**
   * Placeholder text for query bar
   */
  pillPlaceholder: null,

  /**
   * Possible suggestions for pill-value
   */
  valueSuggestions: [],

  /**
   * Is value suggestions API call in progress
   */
  isValueSuggestionsCallInProgress: false,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  isMetaActive: false,
  isMetaAutoFocused: true,
  isOperatorActive: false,
  isOperatorFocusedAtBeginning: false,
  isValueActive: false,
  isValueFocusedAtBeginning: false,
  prepopulatedMetaText: undefined,
  prepopulatedOperatorText: undefined,
  prepopulatedRecentQueryText: undefined,
  selectedMeta: null,
  selectedOperator: null,
  valueString: null,
  isLastPill: false,

  // Whether or not a focusOut event should be processed
  shouldFocusOut: false,

  // Tracks whether a double click has fired to single click
  // events can be stopped
  doubleClickFired: false,

  /**
   * Is this pill currently being edited
   * @type {boolean}
   * @public
   */
  @and('pillData.isEditing', 'isActive')
  isEditing: false,

  /**
   *
   * Is the pill with an expensive operator?
   * @type {boolean}
   * @public
   */
  @computed('pillData', 'isActive')
  isExpensive: (pillData, isActive) => {
    if (pillData && pillData.operator && !isActive) {
      return pillData.operator.isExpensive;
    }
  },

  /**
   *
   * Does the pill have focus?
   * @public
   */
  @alias('pillData.isFocused')
  isFocused: false,

  @computed('activePillTab')
  activeTab: (activePillTab) => {
    if (activePillTab === AFTER_OPTION_TAB_RECENT_QUERIES) {
      return 'recent-queries-tab';
    }
    return 'meta-tab';
  },


  /**
   * Update the component once validation completes. A pill is valid if both
   * client and server side validation passes.
   * @type {boolean}
   * @public
   */
  @alias('pillData.isInvalid')
  isInvalid: false,

  /**
   * Is meta-tab active?
   */
  @equal('activePillTab', AFTER_OPTION_TAB_META)
  isMetaTabActive: false,

  /**
   * Is recentQuery-tab active?
   */
  @equal('activePillTab', AFTER_OPTION_TAB_RECENT_QUERIES)
  isRecentQueriesTabActive: false,

  /**
   * Whether or not this pill is selected
   * @type {boolean}
   * @public
   */
  @alias('pillData.isSelected')
  isSelected: false,

  /**
   * Is this component being used to create a new pill
   * or is it an already existing pill
   * @type {boolean}
   * @public
   */
  @computed('pillData')
  isExistingPill: (pillData) => !!pillData && !!pillData.id,

  /**
   * Is this pill able to be deleted?
   * @type {boolean}
   * @public
   */
  @computed('isExistingPill', 'isActive')
  isDeletable: (isExistingPill, isActive) => isExistingPill && !isActive,

  /**
   * Update the component title with error message once validation returns
   * If a valid pill, return the concatenated string
   * @public
   */
  @computed('pillData', 'stringifiedPill', 'isActive')
  title: (pillData, stringifiedPill, isActive) => {
    if (!isActive && pillData) {
      if (pillData.isInvalid) {
        const { value } = pillData;
        const validationError = pillData.validationError.string || pillData.validationError.message;
        // i18n converts chars like `=` to encoded strings, so can't use that here.
        const errorMessage = `You entered '${value}'. ${validationError}`;
        return errorMessage;
      } else {
        return stringifiedPill;
      }
    }
  },

  @computed('pillData')
  stringifiedPill: (pillData) => {
    if (pillData) {
      const { meta, operator, value } = pillData;
      const metaName = meta ? meta.metaName : null;
      const displayName = operator ? operator.displayName : null;
      return `${metaName || ''} ${displayName || ''} ${value || ''}`.trim();
    }
  },

  /**
   * Should the meta field take up 100% of the available pill space? The meta
   * control can expand if there is no operator set, no value set, and is
   * active.
   * @public
   */
  @computed('selectedOperator', 'valueString', 'isMetaActive', 'isMetaTabActive')
  shouldMetaExpand: (selectedOperator, valueString, isMetaActive, isMetaTabActive) => {
    return !selectedOperator && isEmpty(valueString) && isMetaActive && isMetaTabActive;
  },

  /**
   * Should the operator field take up 100% of the available pill space? The
   * operator control can expand if there is no value set and is active.
   * @public
   */
  @computed('valueString', 'isOperatorActive', 'isMetaTabActive')
  shouldOperatorExpand: (valueString, isOperatorActive, isMetaTabActive) => {
    return isEmpty(valueString) && isOperatorActive && isMetaTabActive;
  },

  init() {
    this._super(arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.META_ARROW_LEFT_KEY]: () => this._metaArrowLeft(),
      [MESSAGE_TYPES.META_ARROW_RIGHT_KEY]: () => this._metaArrowRight(),
      [MESSAGE_TYPES.META_ARROW_RIGHT_KEY_WITH_NO_SELECTION]: () => this._metaArrowRightWithNoSelection(),
      [MESSAGE_TYPES.META_CLICKED]: () => this._metaClicked(),
      [MESSAGE_TYPES.META_ENTER_KEY]: () => this._checkToSubmitQuery(),
      [MESSAGE_TYPES.META_ESCAPE_KEY]: () => this._cancelPill(),
      [MESSAGE_TYPES.META_PASTE]: (value) => this._metaPaste(value),
      [MESSAGE_TYPES.META_SELECTED]: (data) => this._metaSelected(data),
      [MESSAGE_TYPES.OPERATOR_ARROW_LEFT_KEY]: () => this._operatorArrowLeft(),
      [MESSAGE_TYPES.OPERATOR_ARROW_RIGHT_KEY]: () => this._operatorArrowRight(),
      [MESSAGE_TYPES.OPERATOR_BACKSPACE_KEY]: () => this._operatorBackspace(),
      [MESSAGE_TYPES.OPERATOR_CLICKED]: () => this._operatorClicked(),
      [MESSAGE_TYPES.OPERATOR_ESCAPE_KEY]: () => this._cancelPill(),
      [MESSAGE_TYPES.OPERATOR_PASTE]: (value) => this._operatorPaste(value),
      [MESSAGE_TYPES.OPERATOR_SELECTED]: (data) => this._operatorSelected(data),
      [MESSAGE_TYPES.DELETE_CLICKED]: () => this._deletePill(),
      [MESSAGE_TYPES.FOCUSED_PILL_ENTER_PRESSED]: () => this._focusedEnterPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED]: () => this._focusedLeftArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED]: () => this._focusedRightArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_SHIFT_RIGHT_ARROW_PRESSED]: () => this._focusedShiftRightArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_SHIFT_LEFT_ARROW_PRESSED]: () => this._focusedShiftLeftArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_OPEN_PAREN_PRESSED]: () => this._focusedPillOpenParenPressed(),
      [MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY]: (data) => this._valueArrowLeft(data),
      [MESSAGE_TYPES.VALUE_ARROW_RIGHT_KEY]: (data) => this._rightArrowKeyPressed(data),
      [MESSAGE_TYPES.VALUE_BACKSPACE_KEY]: () => this._valueBackspace(),
      [MESSAGE_TYPES.VALUE_CLICKED]: () => this._valueClicked(),
      [MESSAGE_TYPES.VALUE_ENTER_KEY]: (data) => {
        if (this.get('isExistingPill')) {
          this._editPill(data);
        } else {
          this._createPill(data);
        }
      },
      [MESSAGE_TYPES.VALUE_ESCAPE_KEY]: () => this._cancelPill(),
      [MESSAGE_TYPES.VALUE_PASTE]: (value) => this._valuePaste(value),
      [MESSAGE_TYPES.VALUE_SET]: (data) => this._valueSet(data),
      [MESSAGE_TYPES.CREATE_FREE_FORM_PILL]: ([data, dataSource]) => this._createFreeFormPill(data, dataSource),
      [MESSAGE_TYPES.CREATE_TEXT_PILL]: ([data, dataSource]) => this._createTextPill(data, dataSource),
      [MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED]: ({ data, dataSource }) => this._toggleActiveTab(data, dataSource),
      [MESSAGE_TYPES.RECENT_QUERIES_TEXT_TYPED]: ({ data, dataSource }) => this._recentQueryTextEntered(data, dataSource),
      [MESSAGE_TYPES.RECENT_QUERIES_ESCAPE_KEY]: () => this._cancelPill(),
      [MESSAGE_TYPES.RECENT_QUERY_SELECTED]: (data) => this._recentQuerySelected(data),
      [MESSAGE_TYPES.PILL_OPEN_PAREN]: () => this._openParen(),
      [MESSAGE_TYPES.PILL_CLOSE_PAREN]: () => this._closeParen(),
      [MESSAGE_TYPES.PILL_LOGICAL_OPERATOR]: (data) => this._logicalOperator(data),
      [MESSAGE_TYPES.PILL_HOME_PRESSED]: (data) => this._homeButtonPressed(data),
      [MESSAGE_TYPES.PILL_END_PRESSED]: () => this._endButtonPressed(),
      [MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED]: (data) => this._deleteOrBackspacePressed(data)
    });

    if (this.get('isExistingPill')) {
      const { meta, operator, value } = this.get('pillData');
      this.setProperties({
        selectedMeta: meta,
        selectedOperator: operator,
        valueString: value
      });
    }
  },

  didInsertElement() {
    this._super(...arguments);
    // When we create this instance, if it's active, set something active
    if (this.get('isActive')) {
      const pD = this.get('pillData');
      if (pD && pD.operator && pD.operator.hasValue) {
        // operator should have a value, so focus on value
        this.set('isValueActive', true);
      } else if (pD && pD.operator && !pD.operator.hasValue) {
        // operator is valueless (like "exists"), so focus on operator
        this.set('isOperatorActive', true);
      } else {
        // default to focusing on meta
        this.set('isMetaActive', true);
      }
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);

    const cleanupProperty = this.get('cleanupInputFields');
    // Cleanup all trailing text
    if (typeof cleanupProperty === 'undefined') {
      this.set('shouldCleanInputFields', false);
    } else {
      // This can be triggered via hitting search button or if a NPT is opened.
      // If NPT is opened up, do not mess with QP props, just clean up any trailing text.
      // The reason behind this is that a NPT can only be opened up if no meta/op/value
      // has been selected, which just leaves us to cleanup trailin text.
      // And if search is clicked, reset pill props.
      const { fromPillTrigger } = cleanupProperty;
      if (!fromPillTrigger) {
        this._reset();
      }
      this.set('shouldCleanInputFields', true);
    }
  },

  keyUp() {
    // Stop propogation of event so that things like the events
    // data table don't react to arrow keys being pressed.
    return false;
  },

  focusIn() {
    // Stop any focus out events because focus has returned
    this.set('shouldFocusOut', false);
    this._pillEntered();
  },

  click() {
    // If not active, and clicking pill, then process
    // this as a selection event
    if (!this.get('isActive')) {
      this._throttledPillSelected();
    }
  },

  doubleClick() {
    // set flag to prevent previous single-clicks from executing
    this.set('doubleClickFired', true);

    // If not active, and double clicking pill, then process
    // this as a desire to open for edit
    if (!this.get('isActive')) {
      this._pillOpenForEdit();
    }
  },

  focusOut(e) {
    this.set('shouldFocusOut', true);
    // Use next here because as user moves
    // from meta to operator to value, this
    // component loses focus for a split second.
    // But it regains it. Before next callback
    // is called the focusIn above resets
    // flag indicating focus has been regained
    // and no focusOut side effects should take
    // place.
    next(() => {

      // NOTE: this will not send a focus out
      // event when a pill is created using
      // the pill template because the pill
      // template will have been cleared out
      // and re-entered with the meta trigger
      // open by the time next is called
      const {
        shouldFocusOut,
        isDestroyed,
        isDestroying
      } = this.getProperties('shouldFocusOut', 'isDestroyed', 'isDestroying');
      if (shouldFocusOut && !isDestroyed && !isDestroying) {
        this._pillLostFocus(e);
      }
    });
  },

  actions: {
    /**
     * Handler for all messages coming from pill components
     * (meta/operator/value).
     * @param {string} type The event type from `event-types`
     * @param {Object} data The event data
     * @public
     */
    handleMessage(type, data) {
      const messageHandlerFn = this.get('_messageHandlerMap')[type];
      if (messageHandlerFn) {
        messageHandlerFn(data);
      } else {
        // Any messages that do not match expected message types get send up
        // to the query-pills component.
        this._broadcast(type, data);
      }
    }
  },

  // ************************************************************************ //
  //                          PRIVATE FUNCTIONS                               //
  // ************************************************************************ //
  /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type, data) {
    this.get('sendMessage')(type, data, this.get('position'));
  },

  _reset() {
    // If NPT, no need to reset the properties
    if (!this.isDestroyed && !this.isDestroying) {
      this.setProperties({
        ...RESET_PROPS,
        activePillTab: AFTER_OPTION_TAB_META
      });
      this._resetTabCounts();
    }
  },

  _resetAfterPaste() {
    this.setProperties({
      selectedMeta: null,
      selectedOperator: null,
      isMetaActive: false,
      isOperatorActive: false,
      isOperatorFocusedAtBeginning: false,
      isValueActive: false
    });
    this._resetTabCounts();
  },

  /**
   * Checks current internal state and compares it to the starting state
   * to determine if we are in a "started over" situation. Helps
   * avoid needless prop updates.
   * @private
   */
  _hasBeenReset() {
    const props = this.getProperties(Object.keys(RESET_PROPS));
    return _.isEqual(props, RESET_PROPS);
  },

  _pillEntered() {
    const pillData = this._createPillData();

    // If pill doesn't have id, then this is the new pill template
    // So send proper message
    if (!pillData.id) {
      this._broadcast(MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW, pillData);
    }
  },

  // Pill selected events multiply because of event bubbling.
  // Rather than inspect events for targets, just throttle the
  // pill selection so multiple messages are not sent
  _throttledPillSelected() {
    // Using 500 since it's the default timing for double-click.
    throttle(this, this._pillSelected, 500);
  },

  _pillSelected() {
    // Waiting 300 milliseconds in order to give a double click a chance to
    // occur. If we do not delay execution here, double clicks will execute
    // click processing twice.
    later(() => {
      if (!this.get('doubleClickFired')) {
        const pillData = this._createPillData(this.get('valueString'));
        if (pillData.isSelected) {
          this._broadcast(MESSAGE_TYPES.PILL_DESELECTED, pillData);
        } else {
          this._broadcast(MESSAGE_TYPES.PILL_SELECTED, pillData);
        }
      } else {
        // if double click had been fired, reset flag, however
        // decent chance component no longer exists, so make
        // that check
        if (!this.isDestroyed || !this.isDestroying) {
          this.set('doubleClickFired', false);
        }
      }
    }, 300);
  },

  _pillOpenForEdit() {
    const oldPillData = this.get('pillData');
    const newPillData = this._createPillData(this.get('valueString'));
    const pillData = { ...oldPillData, ...newPillData };
    this._broadcast(MESSAGE_TYPES.PILL_OPEN_FOR_EDIT, pillData);
  },

  /**
   * Checks to see if anything should be done when the pill loses focus.
   * If we have no meta/operator/value then treat this like a pill cancel.
   * @param {Object} focusEvent - A FocusEvent so that we can inspect the
   * `relatedTarget` property to determine if we've clicked on the Query events
   * button.
   * @private
   */
  _pillLostFocus(focusEvent) {
    const {
      valueString,
      isExistingPill
    } = this.getProperties('valueString', 'isExistingPill');
    let isSubmit;
    const el = focusEvent.relatedTarget;
    if (el) {
      const clickedOnContent = el.textContent.trim();
      isSubmit = isSubmitClicked(focusEvent.relatedTarget);
      if (
        clickedOnContent.toLowerCase() === AFTER_OPTION_TAB_META ||
        clickedOnContent.toLowerCase() === AFTER_OPTION_TAB_RECENT_QUERIES
      ) {
        return;
      }
    }
    if (this._isPillDataEmpty()) {
      // Treat this like an ESC was keyed
      this._cancelPillCreation();
    } else if (isSubmit && !isExistingPill && !(this._isPillDataEmpty())) {
      const { selectedMeta, selectedOperator } = this.getProperties('selectedMeta', 'selectedOperator');

      // If meta, operator and value are already in place
      if (!!selectedMeta && !!selectedOperator && valueString) {
        this._createPill(valueString);
      } else {
        // Exit out of pill creation so that the post-pill-creation dropdown is
        // removed
        this._cancelPillCreation();
      }
    }
  },

  // ************************ META FUNCTIONALITY **************************** //
  /**
   * Hanldes when the ARROW_LEFT key is pressed at the left edge of the
   * meta.
   * @private
   */
  _metaArrowLeft() {
    if (this._isPillDataEmpty()) {
      this._broadcast(MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT);
    }
  },

  /**
   * Handles when ARROW_RIGHT is pressed when no meta/operator/value is
   * selected. Shall be used to move focus to adjacent pill.
   * @private
   */

  _metaArrowRightWithNoSelection() {
    if (this._isPillDataEmpty()) {
      this._broadcast(MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT);
    }

  },

  /**
   * Hanldes when the ARROW_RIGHT key is pressed at the right edge of the
   * meta.
   * @private
   */
  _metaArrowRight() {
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: true,
      isOperatorFocusedAtBeginning: true,
      isValueActive: false
    });
  },

  /**
   * Handles meta being clicked. If the pill is active,
   * then treat as activation of meta. If the pill isn't
   * active, then treat is a pill selection.
   *
   * @private
   */
  _metaClicked() {
    if (this.get('isActive')) {
      this.setProperties({
        isMetaActive: true,
        isOperatorActive: false,
        isValueActive: false
      });
    }
  },

  /**
   * Handles a query being pasted into pill-meta. Parses the query and inserts
   * the pills into state.
   * @param {String} value The pasted string
   * @private
   */
  _metaPaste(value) {
    this._broadcast(MESSAGE_TYPES.PILL_PASTE, value);
    // Reset everything and stop editing
    this._resetAfterPaste();
  },

  /**
   * Handles selected pill meta. If no selected meta is supplied, reset meta
   * properties to an appropriate state.
   * @param {Object} selectedMeta The selected meta value
   * @private
   */
  _metaSelected(selectedMeta) {
    if (selectedMeta) {
      this.setProperties({
        selectedMeta,
        isMetaActive: false,
        isMetaAutoFocused: true,
        isOperatorActive: true,
        isValueActive: false
      });
      this.queryCounter.setMetaTabCount(1);
      this._recentQueryTextEntered(selectedMeta.metaName, PILL_META_DATA_SOURCE);
      this._requestValueSuggestions(selectedMeta.metaName);
    } else {
      this.setProperties({
        selectedMeta: null,
        isMetaAutoFocused: false
      });

      // Adding a delay here for the cases where user long presses backspace.
      // We want to reset once all the debounce actions from pill-meta are sent out.
      // This sets the `isExpectingResponse` flag to false and whenever that earlier
      // api request is completed, it will be dropped to the floor.
      later(this, () => {
        this._resetTabCounts();
      }, 100);
    }
  },

  // ************************ OPERATOR FUNCTIONALITY ************************ //
  /**
   * Hanldes when the ARROW_LEFT key is pressed at the left edge of the
   * operator.
   * @private
   */
  _operatorArrowLeft() {
    this.setProperties({
      isMetaActive: true,
      isOperatorActive: false,
      isOperatorFocusedAtBeginning: false,
      isValueActive: false
    });
  },

  /**
   * Hanldes when the ARROW_RIGHT key is pressed at the right edge of the
   * operator.
   * @private
   */
  _operatorArrowRight() {
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: false,
      isOperatorFocusedAtBeginning: false,
      isValueActive: true,
      isValueFocusedAtBeginning: true
    });
  },

  /**
   * Hanldes when the BACKSPACE key is pressed and there is no value in the
   * power-select's input field. i.e. A user has deleted all characters.
   * This will clear out the operator's selected value and move control to the
   * meta control.
   * @private
   */
  _operatorBackspace() {
    this.setProperties({
      selectedOperator: null,
      isMetaActive: true,
      isOperatorActive: false,
      isOperatorFocusedAtBeginning: false,
      isValueActive: false
    });
  },

  /**
   * Handles operator being clicked. If the pill is active,
   * then treat as activation of operator. If the pill isn't
   * active, then treat is a pill selection.
   *
   * @private
   */
  _operatorClicked() {
    if (this.get('isActive')) {
      // save operator and move focus to value if the operator accepts values
      this.setProperties({
        isMetaActive: false,
        isOperatorActive: true,
        isValueActive: false
      });
    }
  },

  /**
   * Handles a query being pasted into pill-operator. Parses the query and inserts
   * the pills into state. Prepends anything in the meta w/ a space to the query
   * and resets the meta.
   * @param {String} value The pasted string
   * @private
   */
  _operatorPaste(value) {
    value = `${this.get('selectedMeta').metaName} ${value}`;
    // Reset the selected meta and stop editing
    this._resetAfterPaste();
    this._broadcast(MESSAGE_TYPES.PILL_PASTE, value);
  },

  /**
   * Handles selected pill operator.
   * @param {Object} selectedOperator The selected operator value
   * @private
   */
  _operatorSelected(selectedOperator) {
    if (selectedOperator) {
      // save operator and move focus to value if the operator accepts values
      this.setProperties({
        selectedOperator,
        isMetaActive: false,
        isOperatorActive: false,
        isValueActive: selectedOperator && selectedOperator.hasValue
      });
      if (selectedOperator && !selectedOperator.hasValue) {
        // An operator that doesn't accept a value was selected,
        // so either create or edit the pill
        if (this.get('isExistingPill')) {
          this._editPill();
        } else {
          this._createPill();
        }
      } else {
        // If there is a operator that does not accept values, no need to deal with recent queries.
        // But if there is one that accepts values, send out the request.
        this._recentQueryTextEntered(selectedOperator.displayName, PILL_OPERATOR_DATA_SOURCE);
      }
    } else {
      this.set('selectedOperator', null);
    }
  },

  // ************************ VALUE FUNCTIONALITY *************************** //
  /**
   * Hanldes when the ARROW_LEFT key is pressed at the left edge of the
   * value.
   * @private
   */
  _valueArrowLeft(data) {
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: true,
      isValueActive: false,
      isValueFocusedAtBeginning: false,
      valueString: data
    });
  },

  /**
   * Hanldes when the BACKSPACE key is pressed and there is no value in the
   * input field. i.e. A user has deleted all characters. This will move control
   * to the operator control.
   * @private
   */
  _valueBackspace() {
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: true,
      isValueActive: false,
      isValueFocusedAtBeginning: false,
      valueString: null
    });
  },

  _valueClicked() {
    if (this.get('isActive')) {
      this.setProperties({
        isMetaActive: false,
        isOperatorActive: false,
        isValueActive: true
      });
    }
  },

  /**
   * Handles a query being pasted into pill-value. Parses the query and inserts
   * the pills into state. Prepends anything in the meta & operator w/ a space to
   * the query and resets the meta & operator. If the type of meta is text, and
   * the operator is not unary, adds quotes around the pasted text.
   * @param {String} value The pasted string
   * @private
   */
  _valuePaste(value) {
    // Auto-quote only if the format is text...
    const shouldAutoQuote = this.get('selectedMeta').format === 'Text' &&
      // ...and the operator is not unary, and...
      this.get('selectedOperator').hasValue &&
      // ...the value does not already have quotes.
      value.length > 1 &&
      !((value[0] === "'" && value[value.length - 1] === "'") ||
      (value[0] === '"' && value[value.length - 1] === '"'));

    if (shouldAutoQuote) {
      // When we add quotes, make sure to escape any existing quotes or backslashes
      value = valueList(value).map((segment) => {
        segment = segment.value
          // Looks like it does nothing because the first string is also escaped
          // for RegExp. Replaces backslashes with double backslashes.
          .replace(new RegExp('\\\\', 'g'), '\\\\')
          .replace(new RegExp('\'', 'g'), '\\\'');
        return `'${segment}'`;
      }).join(',');
    }
    value = `${this.get('selectedMeta').metaName} ${this.get('selectedOperator').displayName} ${value}`;
    // Reset the selected meta & selected operator and stop editing
    this._resetAfterPaste();
    this._broadcast(MESSAGE_TYPES.PILL_PASTE, value);
  },

  /**
   * Handles input of the pill value.
   * @param {Object} data The full string value
   * @private
   */
  _valueSet(data) {
    // if the pill has already moved on to starting over
    // because of a value being set (which causes a pill
    // create/edit), then do not bother setting the value
    // as it'll be cleared out.
    //
    // If mid-creation, we will cause a backtracking-render
    // Ember error if we update the value here because we
    // clear it out as a part of submitting the pill
    if (!this._hasBeenReset() && data !== undefined) {
      this.set('valueString', data);
    }
  },

  // Figures out whether or not we are cancelling an
  // edit or an add
  _cancelPill() {
    if (this.get('isExistingPill')) {
      this._cancelPillEdit();
    } else {
      this._cancelPillCreation();
    }
    this._resetTabCounts();
  },

  // ************************ PILL FUNCTIONALITY **************************** //
  /**
   * Are there no meta/operator/value selected?
   * @private
   *
   */
  _isPillDataEmpty() {
    const {
      selectedMeta,
      selectedOperator,
      valueString
    } = this.getProperties('selectedMeta', 'selectedOperator', 'valueString');
    return !selectedMeta && !selectedOperator && !valueString;
  },

  /**
   * Cancel pill creation. If this pill has `pillData`, then we were editing an
   * existing pill. We don't want to reset all the data back to defaults in this
   * case.
   * @private
   */
  _cancelPillCreation() {
    const pD = this.get('pillData');
    if (!pD) {
      // Reset data and prevent auto focus. The default behavior of the
      // pill-meta component is to show a dropdown when set to active. We need
      // to override this behavior, so we set `isMetaAutoFocused` to `false` to
      // prevent this from happening. This auto-focus behavior is turned back
      // on once a meta selection is made.
      this.setProperties({
        ...RESET_PROPS,
        isMetaAutoFocused: false
      });
    }
    // Inform container that this pill component is cancelling out of creation
    this._broadcast(MESSAGE_TYPES.PILL_ADD_CANCELLED);
  },

  _cancelPillEdit() {
    const pD = this.get('pillData');
    // Inform container that this pill component is cancelling out of editing
    this._broadcast(MESSAGE_TYPES.PILL_EDIT_CANCELLED, pD);
  },

  /**
   * This checks to see if the user pressed ENTER when no meta, operator, and
   * value have been set yet. This is to support submitting a query. It doesn't
   * actually submit the query. It informs the parent that it wants to query.
   * The parent will make the final decision if all conditions are met to allow
   * a query to be performed.
   * @private
   */
  _checkToSubmitQuery() {
    if (this._isPillDataEmpty()) {
      this._broadcast(MESSAGE_TYPES.PILL_INTENT_TO_QUERY);
    }
  },

  /**
   * Handles creating a new pill.
   * @param {string} data Value of pill
   * @private
   */
  _createPill(data) {
    const pillData = this._createPillData(data);

    this._broadcast(MESSAGE_TYPES.PILL_CREATED, pillData);
    // Because this is a "new pill template" pill, when we
    // create a new pill we need to clean this up so another one
    // can be added using the same empty pill. We are effecively
    // starting over and making it so a user can just keep
    // typing and creating pills.
    //
    // Worth noting, it is expected the `position` property
    // would be incremented/updated by the parent.
    this._reset();
  },

  /**
   * Creates a pillData object
   *
   * @param {*} values The pill value. Does not have to be specified if
   * operator is a type that does not have a value.
   * @return {Object} The pill data
   * @private
   */
  _createPillData(values) {
    if (!values) {
      values = [];
    } else if (typeof values === 'string') {
      values = valueList(values);
    }
    values = values.map((v) => v.value);
    const { selectedMeta, selectedOperator } = this.getProperties('selectedMeta', 'selectedOperator');
    const meta = selectedMeta ? selectedMeta.metaName : null;
    const operator = this.get('selectedOperator.displayName');

    const pillData = createFilter(QUERY_FILTER, meta, operator, values);

    // If is an existing pill, add id to object
    if (this.get('isExistingPill')) {
      pillData.id = this.get('pillData.id');
      pillData.isSelected = this.get('pillData.isSelected');
      pillData.isFocused = this.get('pillData.isFocused');
    }
    // Check what type of meta this is. If it's a string value, add quotes unless
    // the operator is length (which has a numeric value). Also add quotes even
    // if the type is not text but if an alias was entered.
    const languageAndAliasesForParser = this.get('languageAndAliasesForParser');
    const aliases = languageAndAliasesForParser ? languageAndAliasesForParser.aliases : {};
    const stringOfValues = values.map((value) => {
      const valueAlias = value && aliases[meta] && Object.values(aliases[meta]).find((alias) => alias.toLowerCase() === value.toLowerCase());
      if (selectedMeta && selectedMeta.format === 'Text' &&
        selectedOperator && selectedOperator.displayName !== 'length') {
        return `'${value}'`;
      } else if (valueAlias) {
        return `'${valueAlias}'`;
      }
      return value;
    }).join(',');
    pillData.value = stringOfValues || null;

    return pillData;
  },

  /**
   * Handles messaging around deleting this pill
   * @private
   */
  _deletePill() {
    this._broadcast(MESSAGE_TYPES.PILL_DELETED, this._createPillData(this.get('valueString')));
  },

  /**
   * Handles editing an existing pill
   * @param {string} data Value of pill
   * @private
   */
  _editPill(data) {
    const pillData = this._createPillData(data);

    this._broadcast(MESSAGE_TYPES.PILL_EDITED, pillData);
    // shutting this down, but not concerned with setting
    // data as the data should be refreshed back down through state.
    // Inevitably this pill is going to be replaced with a new one
    // because an edited pill is a replacement of the previous pill.
    // We are just making everything inactive in case that takes a
    // few millis.
    if (!this.get('isDestroyed') && !this.get('isDestroying')) {
      this.setProperties({
        isMetaActive: false,
        isOperatorActive: false,
        isValueActive: false
      });
    }
  },

  /**
   * Handles events propagating from focus-holder
   * This will be called only when a pill is selected and
   * user presses enter for editing
   * @private
   */
  _focusedEnterPressed() {
    if (!this.get('isActive')) {
      const pillData = this._createPillData(this.get('valueString'));
      this._broadcast(MESSAGE_TYPES.ENTER_PRESSED_ON_FOCUSED_PILL, pillData);
    }
  },

  _focusedLeftArrowPressed() {
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT);
    }
  },

  _focusedRightArrowPressed() {
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT);
    }
  },

  _focusedShiftRightArrowPressed() {
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT);
    }
  },

  _focusedShiftLeftArrowPressed() {
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT);
    }
  },

  _focusedPillOpenParenPressed() {
    if (!this.get('isActive') && this.get('isSelected')) {
      this._broadcast(MESSAGE_TYPES.WRAP_SELECTED_PILLS_WITH_PARENS);
    }
  },

  /**
   * Handles events propagating from focus-holder and pill-meta
   * when the user clicks on Home button and relays the message.
   * When editing the pill, the event is not relayed unless the
   * pill data is empty.
   * @param data - data object passed from recent queries component.
   *               Will be null in other cases.
   */
  _homeButtonPressed(data) {
    const isFromRecentQuery = data?.isFromRecentQuery;
    // If home is pressed from recent query, close that component by resetting query pill.
    if (isFromRecentQuery) {
      this._resetToMoveOutOfRecentQuery();
    }
    this._broadcast(MESSAGE_TYPES.PILL_HOME_PRESSED, this.get('pillData'));
  },

  /**
   * Handles events propagating from focus-holder and pill-meta
   * when the user clicks on End button and relays the message.
   * When editing the pill, the event is not relayed unless the
   * pill data is empty.
   */
  _endButtonPressed() {
    this._broadcast(MESSAGE_TYPES.PILL_END_PRESSED, this.get('pillData'));
  },

  /**
   * Handles events propagating from focus-holder and pill-meta
   * when the user clicks on delete button and relays the message.
   * When editing the pill, the event is not relayed.
   */
  _deleteOrBackspacePressed(data) {
    const isFromRecentQuery = data?.isFromRecentQuery;
    // If delete or backspace is pressed from recent query, close that component by resetting query pill.
    if (isFromRecentQuery) {
      this._resetToMoveOutOfRecentQuery();
    }
    this._broadcast(MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, data);
  },


  _createFreeFormPill(data, dataSource) {
    const textString = this._getStringifiedPill(data, dataSource);
    const pillData = createFilter(COMPLEX_FILTER, textString);
    this._broadcast(MESSAGE_TYPES.CREATE_FREE_FORM_PILL, pillData);
    this._reset();
  },

  _createTextPill(data, dataSource) {
    const searchTerm = this._getStringifiedPill(data, dataSource);
    const pillData = createFilter(TEXT_FILTER, searchTerm);
    this._broadcast(MESSAGE_TYPES.CREATE_TEXT_PILL, pillData);
    this._reset();
  },

  /**
   * Constructs a string to be used for Free-Form and Text filters
   * @param {string} data The data.
   * @param {string} dataSource Where the `data` came from. Acceptable values
   * are "meta", "operator", or "value".
   * @return A String value.
   * @private
   */
  _getStringifiedPill(data, dataSource) {
    const metaStr = this.get('selectedMeta.metaName');
    const operatorStr = this.get('selectedOperator.displayName');
    return this._getStringFromSource({
      'pill-meta': data,
      'pill-operator': `${metaStr} ${data}`,
      'pill-value': `${metaStr} ${operatorStr} ${data}`,
      'recent-query': data
    })(dataSource);
  },

  /**
   * Creates a function that will return a formatted string given a key that
   * matches the supplied `source` map.
   * @param {Object} sources A map of source to string interpolations.
   * @return A function that will return the desired string given a key that
   * matches a source from the `sources` hash.
   * @private
   */
  _getStringFromSource(sources) {
    return (key) => sources.hasOwnProperty(key) ? sources[key] : null;
  },

  /**
   * Broadcast that an open paren was pressed.
   */
  _openParen() {
    this.set('shouldFocusOut', false);
    this._broadcast(MESSAGE_TYPES.PILL_OPEN_PAREN);
  },

  /**
   * Broadcast that a close paren was pressed.
   */
  _closeParen() {
    this._broadcast(MESSAGE_TYPES.PILL_CLOSE_PAREN);
  },

  /**
   * Broadcast message to fetch value suggestions for text typed in.
   */
  _requestValueSuggestions(metaName, filter) {
    this._broadcast(MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS, { metaName, filter });
  },

  /**
   * Broadcast message to create a logical operator
   * @param {string} type - Type of operator to create (AND/OR).
   */
  _logicalOperator(type) {
    const operator = createOperator(type);
    const pillData = this.get('pillData');
    this._broadcast(MESSAGE_TYPES.PILL_LOGICAL_OPERATOR, { operator, pillData });
  },

  // ************************ EPS TAB FUNCTIONALITY *************************  //

  /**
   * Set all meta-tab triggers to false
   */
  _deactivateMetaTab() {
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: false,
      isValueActive: false
    });
  },

  /**
   * Resets all the tab counts in the service.
   */
  _resetTabCounts() {
    this.queryCounter.resetAllTabCounts();
  },

  /**
   * This function is being called each time some text is typed in any of the query-pill's components.
   * We use this text to update - recent queries, tab counts, value suggestions.
   *
   * For Tabs -
   * Regardless of where entered query text is coming from, we'll need a query count.
   * If source is pill-meta or recent-query, we'd also need a meta count. Pill-operator
   * and pill-value will always maintain a meta count 1.
   *
   * Recent queries and Tab counts will not be updated in edit mode as we do not display any of the
   * afterOptions. But Value suggestions should, even if pill is in edit mode.
   */
  _recentQueryTextEntered(data, dataSource) {
    if (this.get('isEditing') && dataSource === PILL_VALUE_DATA_SOURCE) {
      this._requestValueSuggestions(this.get('selectedMeta')?.metaName, data);
      return;
    }
    const stringifiedPill = this._getStringifiedPill(data, dataSource);
    if (stringifiedPill && stringifiedPill.length > 0) {
      this.queryCounter.setResponseFlag(true);
      this._broadcast(MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT, stringifiedPill);

      if (dataSource === PILL_META_DATA_SOURCE ||
          dataSource === PILL_RECENT_QUERY_DATA_SOURCE
      ) {
        const metaCount = this._retrieveCountForMetaText(stringifiedPill, dataSource);
        this.queryCounter.setMetaTabCount(metaCount);
      } else if (dataSource === PILL_VALUE_DATA_SOURCE) {
        this._requestValueSuggestions(this.get('selectedMeta')?.metaName, data);
      }
    }
  },

  _retrieveCountForMetaText(searchText, dataSource) {
    if (dataSource === PILL_RECENT_QUERY_DATA_SOURCE) {
      const pillData = convertTextToPillData({ queryText: searchText, availableMeta: this.get('metaOptions') });
      const props = determineNewComponentPropsFromPillData(pillData);
      // If we are able to construct a proper meta object with whatever has been typed in, we
      // will no longer update the metaTabCount.
      // Otherwise, we run the text against a matcher function.
      if (props.selectedMeta !== null) {
        return 1;
      }
    }
    const metaCount = resultsCount(this.get('metaOptions'), searchText);
    return metaCount;
  },

  _recentQuerySelected(data) {
    this._broadcast(MESSAGE_TYPES.RECENT_QUERY_PILL_CREATED, data);
    this._reset();

  },

  _toggleActiveTab(data, dataSource) {
    const activeTab = this.get('activePillTab');
    switch (activeTab) {
      case AFTER_OPTION_TAB_META: {
        // First thing we do is to deactivate meta-tab's drop-downs
        this._deactivateMetaTab();

        const stringifiedPill = this._getStringifiedPill(data, dataSource);
        // Now we can set recent-query's input with the text coming in from meta-tab
        this.setProperties({
          activePillTab: AFTER_OPTION_TAB_RECENT_QUERIES,
          prepopulatedRecentQueryText: stringifiedPill
        });
        this._resetPrePopulatedProperties();
        break;
      }
      case AFTER_OPTION_TAB_RECENT_QUERIES: {
        let props;
        if (!isEmpty(data)) {
          const meta = this.get('metaOptions');
          const pillData = convertTextToPillData({ queryText: data, availableMeta: meta });
          props = determineNewComponentPropsFromPillData(pillData);

          // If there is meta selected, send out value suggestions call that will
          // be used to display options in pill-value.
          if (props.selectedMeta !== null) {
            this._requestValueSuggestions(props.selectedMeta.metaName, props.valueString ? props.valueString : '');
          }
        } else {
          // If there is no data, activate pill-meta component
          props = {
            isMetaAutoFocused: true,
            isMetaActive: true,
            selectedMeta: null
          };
        }
        const newProps = {
          ...props,
          activePillTab: AFTER_OPTION_TAB_META
        };
        this.setProperties(newProps);
        this._resetPrePopulatedProperties();
        break;
      }
    }
  },

  /**
   * Will clear out prepopulated text (provided) in the next
   * event loop.
   * We do this because we do not want prepopulated text
   * to meddle with the workings of respective components.
   * Should prepopulate only when explicity asked, and clear away once set.
   */
  _resetPrePopulatedProperties() {
    next(() => {
      this.set('prepopulatedMetaText', undefined);
      this.set('prepopulatedOperatorText', undefined);
      this.set('prepopulatedRecentQueryText', undefined);
    });
  },
  /**
   * This is needed in order to prevent query pills from getting confused about which exact
   *  location to open a new component when moving away from recent queries
   */
  _resetToMoveOutOfRecentQuery() {
    this.setProperties({
      activePillTab: AFTER_OPTION_TAB_META,
      isActive: false,
      isMetaActive: false,
      isOperatorActive: false,
      isValueActive: false,
      selectedMeta: null,
      selectedOperator: null,
      valueString: null
    });
    this._resetTabCounts();
  },
  // ************************ TODO FUNCTIONALITY **************************** //
  /**
   * Handles the right arrow key.
   * @param {Object} data The full string value
   * @private
   */
  _rightArrowKeyPressed(data) {
    // TODO
    log('_rightArrowKeyPressed() called', data);
  }
});
