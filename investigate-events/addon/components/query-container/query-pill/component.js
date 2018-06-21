import { next } from '@ember/runloop';
import Component from '@ember/component';
import computed, { and, empty } from 'ember-computed-decorators';
import _ from 'lodash';

import * as MESSAGE_TYPES from '../message-types';

const { log } = console;

const RESET_PROPS = {
  isActive: true,
  selectedMeta: null,
  selectedOperator: null,
  valueString: null,
  isMetaActive: true,
  isOperatorActive: false,
  isValueActive: false
};

export default Component.extend({
  classNameBindings: ['isActive', ':query-pill'],

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
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  isMetaActive: false,
  isMetaAutoFocused: true,
  isOperatorActive: false,
  isOperatorCursorLeft: false,
  isValueActive: false,
  selectedMeta: null,
  selectedOperator: null,
  valueString: null,

  // Whether or not a focusOut event should be processed
  shouldFocusOut: false,

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
   * The meta control can expand to take all the space if there is no operator
   * selected and no value set.
   * @private
   */
  @computed('selectedOperator', 'valueString')
  canMetaExpand: (op, vs) => !op && !vs,

  /**
   * Should the meta field take up 100% of the available pill space?
   * @public
   */
  @and('canMetaExpand', 'isMetaActive')
  shouldMetaExpand: false,

  /**
   * The operator can expand to take all the space if there is no value set
   * @private
   */
  @empty('valueString')
  canOperatorExpand: true,

  /**
   * Should the operator field take up 100% of the available pill space?
   * @public
   */
  @and('canOperatorExpand', 'isOperatorActive')
  shouldOperatorExpand: false,

  init() {
    this._super(arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.META_ARROW_LEFT_KEY]: () => this._metaArrowLeft(),
      [MESSAGE_TYPES.META_ARROW_RIGHT_KEY]: () => this._metaArrowRight(),
      [MESSAGE_TYPES.META_CLICKED]: () => this._metaClicked(),
      [MESSAGE_TYPES.META_ESCAPE_KEY]: () => this._cancelPillCreation(),
      [MESSAGE_TYPES.META_SELECTED]: (data) => this._metaSelected(data),
      [MESSAGE_TYPES.OPERATOR_ARROW_LEFT_KEY]: () => this._operatorArrowLeft(),
      [MESSAGE_TYPES.OPERATOR_ARROW_RIGHT_KEY]: () => this._operatorArrowRight(),
      [MESSAGE_TYPES.OPERATOR_BACKSPACE_KEY]: () => this._operatorBackspace(),
      [MESSAGE_TYPES.OPERATOR_CLICKED]: () => this._operatorClicked(),
      [MESSAGE_TYPES.OPERATOR_ESCAPE_KEY]: () => this._cancelPillCreation(),
      [MESSAGE_TYPES.OPERATOR_SELECTED]: (data) => this._operatorSelected(data),
      [MESSAGE_TYPES.PILL_DELETED]: (data) => this._deletePill(data),
      [MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY]: (data) => this._valueArrowLeft(data),
      [MESSAGE_TYPES.VALUE_ARROW_RIGHT_KEY]: (data) => this._rightArrowKeyPressed(data),
      [MESSAGE_TYPES.VALUE_BACKSPACE_KEY]: () => this._valueBackspace(),
      [MESSAGE_TYPES.VALUE_ENTER_KEY]: (data) => {
        if (this.get('isExistingPill')) {
          this._editPill(data);
        } else {
          this._createPill(data);
        }
      },
      [MESSAGE_TYPES.VALUE_ESCAPE_KEY]: () => this._cancelPillCreation(),
      [MESSAGE_TYPES.VALUE_SET]: (data) => this._valueSet(data)
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
    // When we create this instance, if it's active, set meta as active
    if (this.get('isActive')) {
      this.set('isMetaActive', true);
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

  focusOut() {
    this.set('shouldFocusOut', true);

    // Use next here because as user moves
    // from meta to operator to value, this
    // component loses focus for a split second.
    // But it regains it. Before next function
    // is called the focusIn above resets
    // flag indicating focus has been regained
    // and no focusOut side effects should take
    // place
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
        this._pillLostFocus();
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
    this.setProperties(RESET_PROPS);
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

    // If pill has id, then this is an edit, send proper message
    if (pillData.id) {
      this._broadcast(MESSAGE_TYPES.PILL_ENTERED_FOR_EDIT, pillData);
    } else {
      this._broadcast(MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW, pillData);
    }
  },

  /**
   * Checks to see if anything should be done when the pill loses focus.
   * If we have no meta/operator/value then treat this like a pill cancel.
   *
   * @private
   */
  _pillLostFocus() {
    const {
      selectedMeta,
      selectedOperator,
      valueString
    } = this.getProperties('selectedMeta', 'selectedOperator', 'valueString');
    if (!selectedMeta && !selectedOperator && !valueString) {
      // treat this like an ESC was keyed
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
    // TODO - Move control to the pill to the left.
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
      isOperatorCursorLeft: true,
      isValueActive: false
    });
  },

  /**
   * Handles meta being clicked.
   * @private
   */
  _metaClicked() {
    this.setProperties({
      isMetaActive: true,
      isOperatorActive: false,
      isValueActive: false,
      isActive: true
    });
  },

  /**
   * Handles selected pill meta.
   * @param {Object} selectedMeta The selected meta value
   * @private
   */
  _metaSelected(selectedMeta) {
    this.setProperties({
      selectedMeta,
      isMetaActive: false,
      isMetaAutoFocused: true,
      isOperatorActive: true,
      isValueActive: false
    });
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
      isValueActive: true
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
      isValueActive: false
    });
  },

  /**
   * Handles operator being clicked.
   * @private
   */
  _operatorClicked() {
    // save operator and move focus to value if the operator accepts values
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: true,
      isValueActive: false,
      isActive: true
    });
  },

  /**
   * Handles selected pill operator.
   * @param {Object} selectedOperator The selected operator value
   * @private
   */
  _operatorSelected(selectedOperator) {
    // save operator and move focus to value if the operator accepts values
    this.setProperties({
      selectedOperator,
      isMetaActive: false,
      isOperatorActive: false,
      isValueActive: selectedOperator.hasValue,
      isActive: selectedOperator.hasValue
    });
    if (!selectedOperator.hasValue) {
      // an operator that does not accept a value was selected,
      // so create the pill
      this._createPill();
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
      valueString: data,
      isMetaActive: false,
      isOperatorActive: true,
      isOperatorCursorLeft: false,
      isValueActive: false
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
      valueString: null,
      isMetaActive: false,
      isOperatorActive: true,
      isOperatorCursorLeft: false,
      isValueActive: false
    });
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

  // ************************ PILL FUNCTIONALITY **************************** //
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
    this._broadcast(MESSAGE_TYPES.PILL_CANCELLED, pD);
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
    const meta = this.get('selectedMeta.metaName');
    const operator = this.get('selectedOperator.displayName');
    const pillData = {
      meta,
      operator
    };

    // If is an existing pill, add id to object
    if (this.get('isExistingPill')) {
      pillData.id = this.get('pillData.id');
      pillData.value = this.get('pillData.value');
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
    this._broadcast(MESSAGE_TYPES.PILL_DELETED, this._createPillData());
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
    // Enivitably this pill is going to be replaced with a new one
    // because an edited pill is a replacement of the previous pill.
    // We are just making everything inactive in case that takes a
    // few millis.
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: false,
      isValueActive: false,
      isActive: false
    });
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
