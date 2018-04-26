import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';

const { log } = console;

export default Component.extend({
  classNameBindings: ['isActive', ':query-pill'],

  filter: null,
  isActive: false,
  sendMessage: () => {},

  isMetaActive: false,
  isOperatorActive: false,
  isValueActive: false,
  selectedMeta: null,
  selectedOperator: null,
  valueString: null,

  init() {
    this._super(arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.META_SELECTED]: (data) => this._metaSelected(data),
      [MESSAGE_TYPES.OPERATOR_CLICKED]: () => this._operatorClicked(),
      [MESSAGE_TYPES.OPERATOR_SELECTED]: (data) => this._operatorSelected(data),
      [MESSAGE_TYPES.VALUE_SET]: (data) => this._valueSet(data),
      [MESSAGE_TYPES.VALUE_ENTER_KEY]: () => this._createPill(),
      [MESSAGE_TYPES.VALUE_ESCAPE_KEY]: () => this._cancelPillCreation(),
      [MESSAGE_TYPES.VALUE_BACKSPACE_KEY]: (data) => this._backspaceKeyPressed(data),
      [MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY]: (data) => this._leftArrowKeyPressed(data),
      [MESSAGE_TYPES.VALUE_ARROW_RIGHT_KEY]: (data) => this._rightArrowKeyPressed(data)
    });
  },

  didInsertElement() {
    this._super(...arguments);
    this._broadcast(MESSAGE_TYPES.PILL_INITIALIZED);
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
    this.get('sendMessage')(type, data);
  },

  /**
   * Handles selected pill meta.
   * @param {Object} selectedMeta The selected meta value
   * @private
   */
  _metaSelected(selectedMeta) {
    // save meta and move focus to the operator
    this.setProperties({
      selectedMeta,
      isMetaActive: false,
      isOperatorActive: true,
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
      this.set('valueString', null);
      this._broadcast(MESSAGE_TYPES.PILL_CREATED, this._createFilter());
    }
  },

  /**
   * Handles creating a new pill.
   * @private
   */
  _createPill() {
    // TODO - Sure more will happen here, just doing this for now to see that
    // something different happens when hitting the Enter key.
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: false,
      isValueActive: false,
      isActive: false
    });
    const value = this.get('valueString').trim();
    this._broadcast(MESSAGE_TYPES.PILL_CREATED, this._createFilter(value));
  },

  /**
   * Creates a filter to be used for metaFilters.
   * @param {*} value The value of the filter. Does not have to be specified if
   * operator is a type that does not have a value.
   * @return {Object} A filter
   * @private
   */
  _createFilter(value = null) {
    const meta = this.get('selectedMeta.metaName');
    const operator = this.get('selectedOperator.displayName');
    return { meta, operator, value };
  },

  /**
   * Handles input of the pill value.
   * @param {Object} data The full string value
   * @private
   */
  _valueSet(data) {
    if (data !== undefined) {
      this.set('valueString', data);
    }
  },

  /**
   * Cancel pill creation.
   * @private
   */
  _cancelPillCreation() {
    // TODO
    log('_cancelPillCreation() called');
  },

  /**
   * Handles the Backspace key.
   * @param {Object} data The full string value
   * @private
   */
  _backspaceKeyPressed(data) {
    // TODO
    log('_backspaceKeyPressed() called', data);
  },

  /**
   * Handles the left arrow key.
   * @param {Object} data The full string value
   * @private
   */
  _leftArrowKeyPressed(data) {
    // TODO
    log('_leftArrowKeyPressed() called', data);
  },

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
