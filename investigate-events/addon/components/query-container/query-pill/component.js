import Component from '@ember/component';
import computed, { and, empty } from 'ember-computed-decorators';
import * as MESSAGE_TYPES from '../message-types';

const { log } = console;

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
  isOperatorActive: false,
  isValueActive: false,
  selectedMeta: null,
  selectedOperator: null,
  valueString: null,

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
      [MESSAGE_TYPES.META_CLICKED]: () => this._metaClicked(),
      [MESSAGE_TYPES.META_SELECTED]: (data) => this._metaSelected(data),
      [MESSAGE_TYPES.OPERATOR_CLICKED]: () => this._operatorClicked(),
      [MESSAGE_TYPES.OPERATOR_SELECTED]: (data) => this._operatorSelected(data),
      [MESSAGE_TYPES.PILL_DELETED]: (data) => this._deletePill(data),
      [MESSAGE_TYPES.VALUE_SET]: (data) => this._valueSet(data),
      [MESSAGE_TYPES.VALUE_ENTER_KEY]: (data) => this._createPill(data),
      [MESSAGE_TYPES.VALUE_ESCAPE_KEY]: () => this._cancelPillCreation(),
      [MESSAGE_TYPES.VALUE_BACKSPACE_KEY]: (data) => this._backspaceKeyPressed(data),
      [MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY]: (data) => this._leftArrowKeyPressed(data),
      [MESSAGE_TYPES.VALUE_ARROW_RIGHT_KEY]: (data) => this._rightArrowKeyPressed(data)
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
    this.get('sendMessage')(type, data, this.get('position'));
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
      this._broadcast(MESSAGE_TYPES.PILL_CREATED, this._createPillData());
    }
  },

  /**
   * Handles messaging around deleting this pill
   * @private
   */
  _deletePill() {
    this._broadcast(MESSAGE_TYPES.PILL_DELETED, this._createPillData());
  },

  /**
   * Handles creating a new pill.
   * @param {string} data Value of pill
   * @private
   */
  _createPill(data) {
    const valueString = data;
    const pillData = this._createPillData(valueString);

    // TODO
    //
    // Eventually we will not want to turn "new" pills
    // into real pills, instead clearing them out and letting
    // them remain blank new pill templates. Real pills
    // would be added via state and state interation in the
    // pills template. This would mean that the changes we
    // make here would be different based on new vs edit
    //
    this.setProperties({
      isMetaActive: false,
      isOperatorActive: false,
      isValueActive: false,
      isActive: false,
      valueString
    });

    if (this.get('isExistingPill')) {
      this._broadcast(MESSAGE_TYPES.PILL_EDITED, pillData);
    } else {
      this._broadcast(MESSAGE_TYPES.PILL_CREATED, pillData);
    }
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
