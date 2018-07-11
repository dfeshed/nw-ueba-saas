import Component from '@ember/component';
import { next, scheduleOnce } from '@ember/runloop';
import computed from 'ember-computed-decorators';

import * as MESSAGE_TYPES from '../message-types';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import { isArrowLeft, isArrowRight, isBackspace, isEnter, isEscape } from 'investigate-events/util/keys';

const { log } = console;// eslint-disable-line no-unused-vars

const leadingSpaces = /^[\s\uFEFF\xA0]+/;

export default Component.extend({
  classNameBindings: ['isExpanded', 'isPopulated', ':pill-operator'],

  /**
   * Does this component currently have focus?
   * @type {boolean}
   * @public
   */
  isActive: false,
  /**
   * Does this component consume the full width of its parent, or is it sized to
   * match its contents?
   * @type {boolean}
   * @public
   */
  isExpanded: true,

  isFocusAtBeginning: false,

  /**
   * A meta object. Used to determin which operators to display.
   * @type {Object}
   * @public
   */
  meta: null,
  /**
   * The option that is currently selected
   * @type {Object}
   * @public
   */
  selection: null,
  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  // Indicates if something is being rendered by this template
  // and that it isn't empty. Controls whether padding/spacing is
  // required
  @computed('selection', 'isActive')
  isPopulated(selection, isActive) {
    return !!selection || isActive;
  },

  @computed('meta')
  options(meta) {
    return relevantOperators(meta);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    if (this.get('isActive')) {
      // We schedule this after render to give time for the power-select to
      // be rendered before trying to focus on it.
      scheduleOnce('afterRender', this, '_focusOnPowerSelectTrigger');
    }
  },

  click() {
    // If this component is not active and the user clicks on it, dispatch an
    // action so that the parent can coordinate the activation of this component.
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.OPERATOR_CLICKED);
    }
  },

  actions: {
    onChange(selection /* powerSelectAPI, event */) {
      this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, selection);
    },
    onFocus(powerSelectAPI /* event */) {
      const selection = this.get('selection');
      if (powerSelectAPI.lastSearchedText && !selection) {
        // If we gain focus and `lastSearchText` exists, power-select will use
        // that to down-select the list of options. This can happen if the user
        // enters some text, focuses away, then comes back. What they previously
        // typed will effect the list of options.
        powerSelectAPI.actions.search('');
      } else if (selection) {
        // Check to see if the selected option is valid for the power-select
        // options and select it if it is; otherwise clear it out.
        const option = this.get('options').find((d) => d.displayName === selection.displayName);
        if (option) {
          powerSelectAPI.actions.search(option.displayName);
        } else {
          this.set('selection', null);
        }
      }
      powerSelectAPI.actions.open();
    },
    /**
     * This function is called on every `input` event from the power-select's
     * trigger element. It's looking for an input string that ends with a space.
     * If it finds one and the power-select has been down-selected to one
     * result, then broadcast a `select` event. Ultimately, this triggers the
     * `onChange` action above.
     * If the input string is empty, it resets the `selection`. We do this to
     * prevent the previously highlighted item from staying highlighted.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      const isSpace = input.slice(-1) === ' ';
      const { options, results } = powerSelectAPI;
      if (isSpace && results.length === 1) {
        this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, results[0]);
      } else if (input.length === 0) {
        this.set('selection', null);
        // Set the power-select highlight on the next runloop so that the
        // power-select has time to render the full list of options.
        next(this, () => powerSelectAPI.actions.highlight(options[0]));
      }
    },
    /**
     * This function is called every time a key is pressed, and is invoked
     * before power-select reacts to the key that was pressed. This is here to
     * handle one specific case. If the user presses ENTER, selecting an
     * operator that was already selected. In that case, power-select does
     * nothing, but we want the focus to move onto the pill value.
     * As a side note, we cannot combine `onInput`'s functionality here because
     * this code runs before any down-selection of options happens.
     * @private
     */
    onKeyDown(powerSelectAPI, event) {
      if (isEscape(event)) {
        this._broadcast(MESSAGE_TYPES.OPERATOR_ESCAPE_KEY);
      } else if (isEnter(event)) {
        const { selected } = powerSelectAPI;
        const selection = this.get('selection');
        if (selection && selected && selection === selected) {
          this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, selection);
        }
      } else if (isBackspace(event) && event.target.value === '') {
        next(this, () => this._broadcast(MESSAGE_TYPES.OPERATOR_BACKSPACE_KEY));
      } else if (isArrowLeft(event) && event.target.selectionStart === 0) {
        next(this, () => this._broadcast(MESSAGE_TYPES.OPERATOR_ARROW_LEFT_KEY));
      } else if (isArrowRight(event)) {
        const { selected } = powerSelectAPI;
        if (selected && event.target.selectionStart === selected.displayName.length) {
          next(this, () => this._broadcast(MESSAGE_TYPES.OPERATOR_ARROW_RIGHT_KEY));
        }
      }
    },

    onOptionMouseDown(e) {
      const { textContent } = e.target;
      const trimmedText = textContent ? textContent.trim() : '';
      const selection = this.get('selection');
      const displayName = selection ? selection.displayName : null;
      if (trimmedText === displayName) {
        // Re-broadcast selected value if the user clicks on a previously
        // selected option because power-select will not call the `onchange`
        // method.
        this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, selection);
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

  _focusOnPowerSelectTrigger() {
    const trigger = this.element.querySelector('.pill-operator input');
    if (trigger) {
      trigger.focus();
      if (this.get('isFocusAtBeginning')) {
        // Set the cursor position on the next run loop.
        next(this, () => {
          trigger.setSelectionRange(0, 0);
          this.set('isFocusAtBeginning', false);
        });
      }
    }
  },

  /**
   * Function that power-select uses to make an autosuggest match. This function
   * looks at the operators's `displayName` property for a match. This matched
   * from the beginning of the operator string. So `ex` will match `exists`,
   * but not `!exists`.
   * @param {Object} operator An operator object
   * @param {string} input The search string
   * @return {number} The index of the string match. Either `-1` or `0`.
   * @private
   */
  _matcher: (operator, input) => {
    const _input = input.toLowerCase().replace(leadingSpaces, '');
    const _displayName = operator.displayName.toLowerCase();
    return _displayName.indexOf(_input) === 0 ? 0 : -1;
  }
});