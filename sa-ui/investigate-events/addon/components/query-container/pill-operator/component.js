import Component from '@ember/component';
import { cancel, later, next, scheduleOnce, debounce } from '@ember/runloop';
import computed from 'ember-computed-decorators';

import * as MESSAGE_TYPES from '../message-types';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL,
  AFTER_OPTION_TEXT_DISABLED_DUPLICATE_LABEL,
  AFTER_OPTION_TEXT_DISABLED_PARENS_LABEL,
  AFTER_OPTION_TEXT_UNAVAILABLE_LABEL,
  POWER_SELECT_INPUT
} from 'investigate-events/constants/pill';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import KEY_MAP, {
  isArrowDown,
  isArrowLeft,
  isArrowRight,
  isArrowUp,
  isBackspace,
  isEnter,
  isEscape,
  isShiftTab,
  isSpace,
  isTab
} from 'investigate-events/util/keys';
import BoundedList from 'investigate-events/util/bounded-list';
import { assert } from '@ember/debug';
import { hasOperator } from 'investigate-events/util/query-parsing';

const { log } = console;// eslint-disable-line no-unused-vars

const DISABLED_TEXT_SEARCH_DUPLICATE = {
  label: AFTER_OPTION_TEXT_DISABLED_DUPLICATE_LABEL,
  disabled: true,
  highlighted: false
};

const DISABLED_TEXT_SEARCH_PARENS = {
  label: AFTER_OPTION_TEXT_DISABLED_PARENS_LABEL,
  disabled: true,
  highlighted: false
};

const UNAVAILABLE_TEXT_SEARCH = {
  label: AFTER_OPTION_TEXT_UNAVAILABLE_LABEL,
  disabled: true,
  highlighted: false
};

const ENABLED_TEXT_SEARCH = {
  label: AFTER_OPTION_TEXT_LABEL,
  disabled: false,
  highlighted: false
};

const AFTER_OPTIONS_MENU = [
  { label: AFTER_OPTION_FREE_FORM_LABEL, disabled: false, highlighted: false },
  ENABLED_TEXT_SEARCH
];

const LEADING_SPACES = /^[\s\uFEFF\xA0]+/;

const AFTER_OPTIONS_COMPONENT = 'query-container/power-select-after-options';

export default Component.extend({
  classNameBindings: ['isExpanded', 'isPopulated', ':pill-operator'],

  /**
   * Are all Core Services at a revision that allows Text searching to be
   * performed?
   * @type {boolean}
   * @public
   */
  canPerformTextSearch: true,

  /**
   * Does the entire pills list have a text pill already?
   * @type {boolean}
   * @public
   */
  hasTextPill: false,

  /**
   * Whether or not this pill is inside one or more pairs of parens
   */
  isInsideParens: false,

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
   * Which is the active tab for EPS?
   * Options will change based on it
   */
  activePillTab: undefined,

  /**
   * A meta object. Used to determin which operators to display.
   * @type {Object}
   * @public
   */
  meta: null,

  /**
   * Will keep it undefined most of the times. But
   * when it's not, this string will be used to prepopulate
   * pil-operator EPS input.
   * @type {String}
   * @public
   */
  prepopulatedOperatorText: undefined,

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

  @computed('hasTextPill', 'canPerformTextSearch', 'isInsideParens')
  _groomedAfterOptionsMenu(hasTextPill, canPerformTextSearch, isInsideParens) {
    if (!canPerformTextSearch) {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_LABEL, UNAVAILABLE_TEXT_SEARCH);
    } else if (hasTextPill) {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_LABEL, DISABLED_TEXT_SEARCH_DUPLICATE);
    } else if (isInsideParens) {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_LABEL, DISABLED_TEXT_SEARCH_PARENS);
    } else {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_DISABLED_DUPLICATE_LABEL, ENABLED_TEXT_SEARCH);
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_DISABLED_PARENS_LABEL, ENABLED_TEXT_SEARCH);
    }
    return this._afterOptionsMenu;
  },

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
    this._afterOptionsMenu = BoundedList.create({ list: AFTER_OPTIONS_MENU });
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.AFTER_OPTIONS_SELECTED]: (d) => this._createPillFromAdvancedOption(d),
      [MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT]: (index) => this._afterOptionsMenu.highlightIndex = index,
      [MESSAGE_TYPES.AFTER_OPTIONS_REMOVE_HIGHLIGHT]: () => this._afterOptionsMenu.clearHighlight(),
      [MESSAGE_TYPES.AFTER_OPTIONS_TAB_CLICKED]: () => this._afterOptionsTabToggle()
    });
    this.set('_keyDownHandlerMap', {
      [KEY_MAP.arrowDown.key]: this._navigationHandler.bind(this),
      [KEY_MAP.arrowLeft.key]: this._navigationHandler.bind(this),
      [KEY_MAP.arrowRight.key]: this._navigationHandler.bind(this),
      [KEY_MAP.arrowUp.key]: this._navigationHandler.bind(this),
      [KEY_MAP.backspace.key]: this._keyHandler.bind(this),
      [KEY_MAP.enter.key]: this._commandHandler.bind(this),
      [KEY_MAP.escape.key]: this._commandHandler.bind(this),
      [KEY_MAP.space.key]: this._keyHandler.bind(this),
      [KEY_MAP.tab.key]: this._navigationHandler.bind(this)
    });
    // _debugContainerKey is a private Ember property that returns the full
    // component name (component:query-container/pill-operator).
    const [ , source ] = this._debugContainerKey.split('/');
    this.set('source', source);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    if (this.get('isActive')) {

      // If there is some prepopulated text coming in from recent-query tabs,
      // the intent is to focus.
      // onFocus function takes in that text, searches on it using the public
      // API, which automatically sets the text in this component.
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

  paste(event) {
    // Don't do anything if this pill is being edited
    if (this.get('isEditing')) {
      return;
    }

    // Pull data from both the clipboard event and the element's value.
    // Do this because we want to preventDefault so that the text does not
    // blink in the UI and then turn into pills, but because we do that the
    // text is not available in the value of the element. Also, we want to use
    // any text already in the element typed before the paste, so we need to
    // use both sources. Wait until the next runloop in case the user is typing
    // *really* fast.
    const pastedValue = event.originalEvent.clipboardData.getData('Text');
    event.preventDefault();
    next(this, () => {
      const previousValue = event.target.value;
      const pastePos = event.target.selectionStart;
      if (previousValue) {
        event.target.value = '';
      }
      const value = `${previousValue.slice(0, pastePos)}${pastedValue}${previousValue.slice(pastePos)}`;

      if (!value) {
        return;
      }

      const valueIsSubstringOfOperator = this.get('options').some((operator) => {
        return operator.displayName.includes(value.toLowerCase());
      });
      // If the value is a substring of the meta, replace whatever the user
      // pasted with it's toLowerCase. Otherwise, don't put any text in the input
      // and paste pills instead.
      if (!valueIsSubstringOfOperator) {
        this._broadcast(MESSAGE_TYPES.OPERATOR_PASTE, value);
      } else {
        event.target.value = value.toLowerCase();
        // We prevented the paste event, but we want to still let the other
        // components know that the input changed.
        const inputEvent = new Event('input', { bubbles: true });
        event.target.dispatchEvent(inputEvent);
      }
    });
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
      const prepopulatedOperatorText = this.get('prepopulatedOperatorText');
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
      } else if (prepopulatedOperatorText !== undefined) {
        // Tab has been switched from recent queries to meta
        // Some text has been prepopulated and focused in to be searched.
        powerSelectAPI.actions.search(prepopulatedOperatorText);
        const options = this.get('options');
        const operatorResultsCount = options.reduce((acc, option) => {
          // Add one to acc if prepopulatedOperatorText is a match for our option
          return this._matcher(option, prepopulatedOperatorText) === 0 ? acc + 1 : acc;
        }, 0);
        if (operatorResultsCount === 0) {
          // Force free-form pill highlight if the search text has complex operators or a
          // text pill already exists
          if (hasOperator(prepopulatedOperatorText) || this.get('hasTextPill')) {
            this._afterOptionsMenu.highlightIndex = 0; // Free-form
          // Otherwise, set it to text pill
          } else {
            this._afterOptionsMenu.highlightIndex = 1; // Text pill
          }
        }
      }
      powerSelectAPI.actions.open();
    },
    /**
     * This function is called on every `input` event from the power-select's
     * trigger element. It happens before power-select has reacted to what was
     * typed so when it sets the first item in the list to be highlighted, we
     * do it in the next runloop.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      const { options } = powerSelectAPI;
      if (input.length === 0) {
        this.set('selection', null);
        // if operator was selected initially and we backspaced to length 0,
        // we relay this info query-pill so it can set selectedOperator
        // property as null.
        this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, null);
        // Set the power-select highlight on the next runloop so that the
        // power-select has time to render the full list of options.
        next(this, () => powerSelectAPI.actions.highlight(options[0]));
      }
      debounce(this, this._broadcastRecentQuerySearch, input, 100);
    },
    /**
     * This function is called every time a key is pressed, and is invoked
     * before power-select reacts to the key that was pressed. As a side note,
     * we cannot combine `onInput`'s functionality here because this code runs
     * before any down-selection of options happens.
     * @private
     */
    onKeyDown(powerSelectAPI, event) {
      const fn = this._keyDownHandlerMap[event.key];
      return fn ? fn(powerSelectAPI, event) : true;
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
   * Handle keys that perform some sort of action like executing a query or
   * canceling out of an edit.
   * @return {boolean} Should further processing by EPS continue?
   * @private
   */
  _commandHandler(powerSelectAPI, event) {
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
        next(this, () => powerSelectAPI.actions.open());
      } else if (selection && selected && selection === selected) {
        // This is called before the change event. We need to delay
        // performing this action to see if a change event occures. If it
        // does, we should ignore this event. See `onChange()`.
        this.set('operationSelectedTimer', later(this, () => {
          this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, selection);
        }, 50));
      }
    }
  },

  /**
   * Handle keys that need attention, but don't fall into a category that would
   * be handled by one of the other "handler" functions.
   * @return {boolean} Should further processing by EPS continue?
   * @private
   */
  _keyHandler(powerSelectAPI, event) {
    if (isSpace(event)) {
      const { options, results, searchText } = powerSelectAPI;
      const operatorAcceptsValue = this._operatorAcceptsValue(options, searchText);
      // These conditionals return false to prevent any further handling of
      // the keypress that brought us here. Specifically, it prevents the
      // pill-value from having a space at the beginning.
      if ((results.length === 1 || results[0].displayName === searchText) && operatorAcceptsValue) {
        // For < and >, they will never be the only match because of <= and >=
        // so go ahead and broadcast if the searchText is an exact match as well.
        this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, results[0]);
        return false;
      }
    } else if (isBackspace(event) && event.target.value === '') {
      next(this, () => this._broadcast(MESSAGE_TYPES.OPERATOR_BACKSPACE_KEY));
    }
  },

  /**
   * Handle keys that are used for moving focus around the application.
   * @return {boolean} Should further processing by EPS continue?
   * @private
   */
  _navigationHandler(powerSelectAPI, event) {
    if (isArrowLeft(event) && event.target.selectionStart === 0) {
      next(this, () => this._broadcast(MESSAGE_TYPES.OPERATOR_ARROW_LEFT_KEY));
    } else if (isArrowRight(event)) {
      const { selected } = powerSelectAPI;
      if (selected && event.target.selectionStart === selected.displayName.length) {
        next(this, () => this._broadcast(MESSAGE_TYPES.OPERATOR_ARROW_RIGHT_KEY));
      }
    } else if (isArrowDown(event)) {
      const { highlighted, results } = powerSelectAPI;
      const lastItem = results[results.length - 1];

      // Do not allow after options to be highlighted while editing
      if (!this.get('isEditing')) {
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
    } else if (isTab(event) || isShiftTab(event)) {
      // Won't toggle once a pill is created.
      if (!this.get('isEditing')) {
        event.preventDefault();
        // For now we have just 2 options, so can toggle.
        // Will need to make  a informed decision once more tabs
        // are added.
        this._afterOptionsTabToggle();
        return false;
      }
    }
  },

  /**
   * Active tab was toggled.
   */
  _afterOptionsTabToggle() {
    this._afterOptionsMenu.clearHighlight();
    const el = this.element.querySelector(POWER_SELECT_INPUT);
    const { value } = el;
    this._broadcast(MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, { data: value, dataSource: this.get('source') });
  },

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
   * Broadcasts a message to parent to make an API call.
   */
  _broadcastRecentQuerySearch(searchQueryText) {
    if (!this.get('isEditing')) {
      this._broadcast(
        MESSAGE_TYPES.RECENT_QUERIES_TEXT_TYPED,
        { data: searchQueryText, dataSource: this.get('source') }
      );
    }
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
    const el = this.element.querySelector(POWER_SELECT_INPUT);
    assert('Power Select input was not found', el);
    const value = el.value.trim();
    // cleanup
    el.value = '';
    this._focusOnPowerSelectTrigger();
    this._afterOptionsMenu.clearHighlight();
    // send value up to create a complex pill
    if (selection === AFTER_OPTION_FREE_FORM_LABEL) {
      this._broadcast(MESSAGE_TYPES.CREATE_FREE_FORM_PILL, [value, this.get('source')]);
    } else if (selection === AFTER_OPTION_TEXT_LABEL) {
      this._broadcast(MESSAGE_TYPES.CREATE_TEXT_PILL, [value, this.get('source')]);
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