import Component from '@ember/component';
import { cancel, later, next, scheduleOnce } from '@ember/runloop';
import computed from 'ember-computed-decorators';

import * as MESSAGE_TYPES from '../message-types';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL
} from 'investigate-events/constants/pill';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import {
  isArrowDown,
  isArrowLeft,
  isArrowRight,
  isArrowUp,
  isBackspace,
  isEnter,
  isEscape
} from 'investigate-events/util/keys';
import BoundedList from 'investigate-events/util/bounded-list';

const { log } = console;// eslint-disable-line no-unused-vars

const AFTER_OPTIONS_MENU = [
  { label: AFTER_OPTION_FREE_FORM_LABEL, disabled: false, highlighted: false },
  { label: AFTER_OPTION_TEXT_LABEL, disabled: false, highlighted: false }
];

const LEADING_SPACES = /^[\s\uFEFF\xA0]+/;

const AFTER_OPTIONS_COMPONENT = 'query-container/power-select-after-options';

export default Component.extend({
  classNameBindings: ['isExpanded', 'isPopulated', ':pill-operator'],

  /**
   * Does this component currently have focus?
   * @type {boolean}
   * @public
   */
  isActive: false,

  /**
   * Is the component being opened for edit?
   * @type {boolean}
   * @public
   */
  isEditing: false,

  /**
   * Does this component consume the full width of its parent, or is it sized to
   * match its contents?
   * @type {boolean}
   * @public
   */
  isExpanded: true,

  /**
   * Should we position the cursor at the beginning of the string when focusing
   * on this component?
   * @type {boolean}
   * @public
   */
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

  _afterOptionsMenu: BoundedList.create({ list: AFTER_OPTIONS_MENU }),

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

  /**
   * We take away the ability to create FF in edit mode.
   * Therefore, no Advanced Options while editing.
   */
  @computed('isEditing')
  optionsComponent(isEditing) {
    return isEditing ? undefined : AFTER_OPTIONS_COMPONENT;
  },

  init() {
    this._super(...arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.AFTER_OPTIONS_SELECTED]: (d) => this._createPillFromAdvancedOption(d),
      [MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT]: (index) => this._afterOptionsMenu.highlightIndex = index,
      [MESSAGE_TYPES.AFTER_OPTIONS_REMOVE_HIGHLIGHT]: () => this._afterOptionsMenu.clearHighlight()
    });
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
    /**
     * Handler for all messages coming from afterOptionsComponent.
     * @param {string} type The event type from `message-types`
     * @param {Object} data The event data
     * @public
     */
    handleMessage(type, data) {
      const messageHandlerFn = this.get('_messageHandlerMap')[type];
      if (messageHandlerFn) {
        messageHandlerFn(data);
      } else {
        // Any messages that do not match expected message types get send up
        // to the query-pill component.
        this._broadcast(type, data);
      }
    },

    onChange(selection /* powerSelectAPI, event */) {
      const timer = this.get('operationSelectedTimer');
      if (timer) {
        // When editing a pill and selecting a new operator, the `onKeyDown()`
        // function is called before this function, which causes the previously
        // selected operator to be re-selected. To stop this, we put the
        // OPERATOR_SELECTED dispatching into a `later` runloop and save off the
        // timer info. If the user actually changed the operator, we'll land
        // here, see if we have an outstanding timer, and cancel it if we do.
        // This will prevent the users' operator selection from being reversed.
        cancel(timer);
      }
      this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, selection);
      this._afterOptionsMenu.clearHighlight();
    },

    onFocus(powerSelectAPI, event) {
      const selection = this.get('selection');
      const targetValue = event.target.value;
      // If we gain focus and `lastSearchText` exists, power-select will use
      // that to down-select the list of options. This can happen if the user
      // enters some text, focuses away, then come back. What they previously
      // typed will effect the list of options.
      if (powerSelectAPI.lastSearchedText && !selection) {
        // There was no previous selection, so see if the user has started
        // typing something into the <input>. If they have use that to
        // perform a search of options.
        const txt = targetValue || '';
        powerSelectAPI.actions.search(txt);
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
     * result (a result that accepts a pill value), then broadcast a `select`
     * event. Ultimately, this triggers the `onChange` action above.
     * If the input string is empty, it resets the `selection`. We do this to
     * prevent the previously highlighted item from staying highlighted.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      const isSpace = input.slice(-1) === ' ';
      const { options, results } = powerSelectAPI;
      const operatorAcceptsValue = this._operatorAcceptsValue(options, input);
      if (isSpace && results.length === 1 && operatorAcceptsValue) {
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
     * before power-select reacts to the key that was pressed. As a side note,
     * we cannot combine `onInput`'s functionality here because this code runs
     * before any down-selection of options happens.
     * @private
     */
    onKeyDown(powerSelectAPI, event) {
      if (isEscape(event)) {
        this._broadcast(MESSAGE_TYPES.OPERATOR_ESCAPE_KEY);
      } else if (isEnter(event)) {
        const { selected } = powerSelectAPI;
        const selection = this.get('selection');
        const afterOptionsMenuItem = this._afterOptionsMenu.highlightedItem;
        if (afterOptionsMenuItem) {
          // If the user presses ENTER while all the operators are filtered out,
          // the assumption is that they want to create a free-form filter.
          // Since we have access to the power-select API, we'll perform an
          // empty search to restore all the options in the dropdown.
          this._createPillFromAdvancedOption(afterOptionsMenuItem.label);
          powerSelectAPI.actions.search('');
        } else if (selection && selected && selection === selected) {
          // This is called before the change event. We need to delay
          // performing this action to see if a change event occures. If it
          // does, we should ignore this event. See `onChange()`.
          this.set('operationSelectedTimer', later(this, this._broadcast, {
            type: MESSAGE_TYPES.OPERATOR_SELECTED,
            data: selection
          }, 50));
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
      } else if (isArrowDown(event)) {
        const { highlighted, results } = powerSelectAPI;
        const lastItem = results[results.length - 1];
        if (event.ctrlKey || event.metaKey || highlighted === lastItem) {
          // CTRL/META was pressed or at bottom of meta list
          // Jump to advanced options
          powerSelectAPI.actions.highlight(null);
          this._afterOptionsMenu.highlightNextIndex();
          return false;
        } else if (this._afterOptionsMenu.highlightedIndex !== -1) {
          // In after options, move to next item
          this._afterOptionsMenu.highlightNextIndex();
          return false;
        }
      } else if (isArrowUp(event)) {
        if (this._afterOptionsMenu.highlightedIndex > 0) {
          // In after options, move to previous item
          this._afterOptionsMenu.highlightPreviousIndex();
          return false;
        } else if (this._afterOptionsMenu.highlightedIndex === 0) {
          // At top of advanced options, move back to meta
          const { actions, results } = powerSelectAPI;
          const lastItem = results[results.length - 1];
          this._afterOptionsMenu.clearHighlight();
          actions.scrollTo(lastItem);
          actions.highlight(lastItem);
          return false;
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
    },

    onOptionMouseEnter() {
      this._afterOptionsMenu.clearHighlight();
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
   * Used by power-select to position the dropdown.
   * @private
   */
  _calculatePosition: (trigger, dropdown) => {
    const { innerWidth } = window;
    const { offsetWidth } = dropdown;
    const pill = trigger.closest('.query-pill');
    const { top, left } = pill ? pill.getBoundingClientRect() : { top: 0, left: 0 };
    const rightEdge = left + offsetWidth;
    const offset = (rightEdge > innerWidth) ? rightEdge - innerWidth + 14 : 5;
    const style = {
      top: top + 34,
      left: Math.max(0, left - offset)
    };
    return {
      horizontalPosition: 'auto',
      verticalPosition: 'auto',
      style
    };
  },

  _createPillFromAdvancedOption(selection) {
    // get input text
    const el = this.element.querySelector('.ember-power-select-typeahead-input');
    const { value } = el;
    // cleanup
    el.value = '';
    this._focusOnPowerSelectTrigger();
    this._afterOptionsMenu.clearHighlight();
    // _debugContainerKey is a private Ember property that returns the full
    // component name (component:query-container/pill-meta).
    const [ , source ] = this._debugContainerKey.split('/');
    // send value up to create a complex pill
    if (selection === AFTER_OPTION_FREE_FORM_LABEL) {
      this._broadcast(MESSAGE_TYPES.CREATE_FREE_FORM_PILL, [value, source]);
    } else if (selection === AFTER_OPTION_TEXT_LABEL) {
      this._broadcast(MESSAGE_TYPES.CREATE_TEXT_PILL, [value, source]);
    }
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
    const _input = input.toLowerCase().replace(LEADING_SPACES, '');
    const _displayName = operator.displayName.toLowerCase();
    return _displayName.indexOf(_input) === 0 ? 0 : -1;
  },

  /**
   * Helps determine if the text typed in is an operator that accepts a value,
   * or if it's a valueless operator like "exists" and "!exists".
   * @param {Object} operators An Array of possible operators
   * @param {string} text The possible operator text.
   * @return {boolean}
   * @private
   */
  _operatorAcceptsValue(operators, text) {
    const _text = text.trim();
    // find operator given the supplied text
    const operator = operators.find((d) => d.displayName === _text);
    return operator ? operator.hasValue : false;
  }
});