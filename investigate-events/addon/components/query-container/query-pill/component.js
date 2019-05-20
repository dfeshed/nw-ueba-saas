import Component from '@ember/component';
import { later, next, throttle } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { isEmpty } from '@ember/utils';
import computed, { alias, and } from 'ember-computed-decorators';
import _ from 'lodash';

import * as MESSAGE_TYPES from '../message-types';
import quote from 'investigate-events/util/quote';
import { createFilter, convertTextToPillData } from 'investigate-events/util/query-parsing';
import { determineNewComponentPropsFromPillData } from './query-pill-util';
import {
  COMPLEX_FILTER,
  QUERY_FILTER,
  TEXT_FILTER,
  AFTER_OPTION_TAB_META,
  AFTER_OPTION_TAB_RECENT_QUERIES,
  PILL_META_DATA_SOURCE,
  PILL_OPERATOR_DATA_SOURCE
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
  classNameBindings: ['isActive', 'isEditing', 'isInvalid', 'isSelected', 'isExpensive', 'isFocused'],
  attributeBindings: ['title'],
  i18n: service(),

  /**
   * After options active tab
   */
  activePillTab: AFTER_OPTION_TAB_META,

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
   * Whether or not we have a text pill across all pills,
   * passed along to rendered components, not used
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
   * List of recent queries
   */
  recentQueries: null,

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
  prepopulatedMetaText: null,
  prepopulatedOperatorText: null,
  selectedMeta: null,
  selectedOperator: null,
  valueString: null,

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

  /**
   * Update the component once validation completes. A pill is valid if both
   * client and server side validation passes.
   * @type {boolean}
   * @public
   */
  @alias('pillData.isInvalid')
  isInvalid: false,

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
  @computed('selectedOperator', 'valueString', 'isMetaActive')
  shouldMetaExpand: (selectedOperator, valueString, isMetaActive) => {
    return !selectedOperator && isEmpty(valueString) && isMetaActive;
  },

  /**
   * Should the operator field take up 100% of the available pill space? The
   * operator control can expand if there is no value set and is active.
   * @public
   */
  @computed('valueString', 'isOperatorActive')
  shouldOperatorExpand: (valueString, isOperatorActive) => {
    // log('shouldOperatorExpand called', valueString, isOperatorActive);
    return isEmpty(valueString) && isOperatorActive;
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
      [MESSAGE_TYPES.META_SELECTED]: (data) => this._metaSelected(data),
      [MESSAGE_TYPES.OPERATOR_ARROW_LEFT_KEY]: () => this._operatorArrowLeft(),
      [MESSAGE_TYPES.OPERATOR_ARROW_RIGHT_KEY]: () => this._operatorArrowRight(),
      [MESSAGE_TYPES.OPERATOR_BACKSPACE_KEY]: () => this._operatorBackspace(),
      [MESSAGE_TYPES.OPERATOR_CLICKED]: () => this._operatorClicked(),
      [MESSAGE_TYPES.OPERATOR_ESCAPE_KEY]: () => this._cancelPill(),
      [MESSAGE_TYPES.OPERATOR_SELECTED]: (data) => this._operatorSelected(data),
      [MESSAGE_TYPES.DELETE_CLICKED]: () => this._deletePill(),
      [MESSAGE_TYPES.FOCUSED_PILL_DELETE_PRESSED]: () => this._focusedDeletePressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_ENTER_PRESSED]: () => this._focusedEnterPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED]: () => this._focusedLeftArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED]: () => this._focusedRightArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_SHIFT_DOWN_RIGHT_ARROW_PRESSED]: () => this._focusedShiftDownRightArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_SHIFT_UP_LEFT_ARROW_PRESSED]: () => this._focusedShiftUpLeftArrowPressed(),
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
      [MESSAGE_TYPES.VALUE_SET]: (data) => this._valueSet(data),
      [MESSAGE_TYPES.CREATE_FREE_FORM_PILL]: ([data, dataSource]) => this._createFreeFormPill(data, dataSource),
      [MESSAGE_TYPES.CREATE_TEXT_PILL]: ([data, dataSource]) => this._createTextPill(data, dataSource),
      [MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED]: ({ data, dataSource }) => this._toggleActiveTab(data, dataSource)
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
    this.setProperties({
      ...RESET_PROPS,
      activePillTab: AFTER_OPTION_TAB_META
    });
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
      const queryEvents = this.get('i18n').t('queryBuilder.queryEvents').string;
      isSubmit = clickedOnContent === queryEvents;
      // if tabs are clicked on, do nothing here.
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
      // If it's not an existing pill, create new pill
      this._createPill(valueString);
      // Exit out of pill creation so that the post-pill-creation dropdown is
      // removed
      this._cancelPillCreation();
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
      this.get('sendMessage')(MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT, this.get('position'));
    }
  },

  /**
   * Handles when ARROW_RIGHT is pressed when no meta/operator/value is
   * selected. Shall be used to move focus to adjacent pill.
   * @private
   */

  _metaArrowRightWithNoSelection() {
    if (this._isPillDataEmpty()) {
      this.get('sendMessage')(MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT, this.get('position'));
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
    } else {
      this.setProperties({
        selectedMeta: null,
        isMetaAutoFocused: false
      });
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
    this._broadcast(MESSAGE_TYPES.PILL_ADD_CANCELLED, pD);
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
   * @param {*} value The pill value. Does not have to be specified if
   * operator is a type that does not have a value.
   * @return {Object} The pill data
   * @private
   */
  _createPillData(value = null) {
    const selectedMeta = this.get('selectedMeta');
    const meta = selectedMeta ? selectedMeta.metaName : null;
    const operator = this.get('selectedOperator.displayName');

    const pillData = createFilter(QUERY_FILTER, meta, operator, value);

    // If is an existing pill, add id to object
    if (this.get('isExistingPill')) {
      pillData.id = this.get('pillData.id');
      pillData.isSelected = this.get('pillData.isSelected');
      pillData.isFocused = this.get('pillData.isFocused');
    }
    // Check what type of meta this is. If it's a string value, add quotes
    if (selectedMeta && selectedMeta.format === 'Text' && value) {
      pillData.value = quote(value);
    } else {
      pillData.value = value;
    }

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
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: false,
      isValueActive: false
    });
  },

  /**
   * Handles events propagating from focus-holder
   * This will be called only when a pill is focused and
   * user either presses delete or backspace
   * @private
   */
  _focusedDeletePressed() {
    this._broadcast(MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL, this._createPillData(this.get('valueString')));
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
      this.get('sendMessage')(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT, this.get('position'));
    }
  },

  _focusedRightArrowPressed() {
    if (!this.get('isActive')) {
      this.get('sendMessage')(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT, this.get('position'));
    }
  },

  _focusedShiftDownRightArrowPressed() {
    if (!this.get('isActive')) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT, this.get('position'));
    }
  },

  _focusedShiftUpLeftArrowPressed() {
    if (!this.get('isActive')) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT, this.get('position'));
    }
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
      'pill-value': `${metaStr} ${operatorStr} ${data}`
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

  // ************************ EPS TAB FUNCTIONALITY *************************  //

  _toggleActiveTab(data, dataSource) {
    const activeTab = this.get('activePillTab');
    switch (activeTab) {
      case AFTER_OPTION_TAB_META: this.set('activePillTab', AFTER_OPTION_TAB_RECENT_QUERIES);
        break;
      case AFTER_OPTION_TAB_RECENT_QUERIES: {
        // If the current tab is recent queries, before we toggle,
        // we parse the text typed in and set meta, operator and value (if available)
        // and place focus on the correct component.

        if (!isEmpty(data)) {
          let pillData;
          const meta = this.get('metaOptions');
          if (dataSource === PILL_META_DATA_SOURCE) {
            pillData = convertTextToPillData({
              queryText: data,
              dataSource,
              availableMeta: meta,
              selectedMeta: null
            });
          } else if (dataSource === PILL_OPERATOR_DATA_SOURCE) {
            pillData = convertTextToPillData({
              queryText: data,
              dataSource,
              availableMeta: meta,
              selectedMeta: this.get('selectedMeta')
            });
          }

          const props = determineNewComponentPropsFromPillData(pillData);
          this.setProperties(props);
          this._runNext(props);
        }
        this.set('activePillTab', AFTER_OPTION_TAB_META);
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
  _runNext(properties) {
    if (properties.prepopulatedMetaText || properties.prepopulatedOperatorText) {
      next(() => {
        this.set('prepopulatedMetaText', undefined);
        this.set('prepopulatedOperatorText', undefined);
      });
    }
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
