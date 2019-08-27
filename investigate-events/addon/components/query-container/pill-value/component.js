import Component from '@ember/component';
import { next, scheduleOnce, debounce } from '@ember/runloop';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import KEY_MAP, {
  isArrowLeft,
  isBackspace,
  isEscape,
  isArrowDown,
  isArrowUp,
  isEnter,
  isTab,
  isShiftTab
} from 'investigate-events/util/keys';
import { escapeBackslash, escapeSingleQuotes, properlyQuoted, stripOuterSingleQuotes } from 'investigate-events/util/quote';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL,
  AFTER_OPTION_TEXT_DISABLED_LABEL,
  AFTER_OPTION_TEXT_UNAVAILABLE_LABEL,
  POWER_SELECT_OPTIONS_QUERY_LABEL,
  POWER_SELECT_INPUT
} from 'investigate-events/constants/pill';
import * as MESSAGE_TYPES from '../message-types';
import Ember from 'ember';
import BoundedList from 'investigate-events/util/bounded-list';
import { hasComplexText } from 'investigate-events/util/query-parsing';

const LEADING_SPACES = /^[\s\uFEFF\xA0]+/;

const { log } = console;// eslint-disable-line no-unused-vars

const DISABLED_TEXT_SEARCH = {
  label: AFTER_OPTION_TEXT_DISABLED_LABEL,
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
const AFTER_OPTIONS_COMPONENT = 'query-container/power-select-after-options';
// This is used for an internal Ember API function: escapeExpression
const { Handlebars: { Utils } } = Ember;

/**
 *
 * @param {String} searchString
 * Default option object `Query Filter` passed to ember-power-select
 */
const defaultOption = (searchString) => {
  return {
    displayName: searchString,
    description: POWER_SELECT_OPTIONS_QUERY_LABEL
  };
};

export default Component.extend({
  classNameBindings: ['isPopulated', ':pill-value'],

  i18n: service(),

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
   * Whether or not to send the next focus out event that occurs
   * @type {boolean}
   * @public
   */
  swallowNextFocusOut: false,

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
   * Should we position the cursor at the beginning of the string when focusing
   * on this component?
   * @type {boolean}
   * @public
   */
  isFocusAtBeginning: false,

  /**
   * Which is the active tab for EPS?
   */
  activePillTab: undefined,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  /**
   * Will contain the name of the component
   */
  source: undefined,

  /**
   * The value to display.
   * @type {string}
   * @public
   */
  valueString: null,

  /**
   * List of value suggestions, to be displayed in the drop-down.
   */
  valueSuggestions: [],

  /**
  * The options used for the power-select. They are grouped to provide a way to
  * have 2 "lists". One list is the default type of pill to create, the second
  * list is suggested values if available.
  * If no suggestions are available, display default `Query Filter` option
  */
  @computed('_searchString', 'valueSuggestions', 'i18n')
  _options(searchString, valueSuggestions) {
    return [defaultOption(searchString), ...valueSuggestions];
  },

  @computed('hasTextPill', 'canPerformTextSearch')
  _groomedAfterOptionsMenu(hasTextPill, canPerformTextSearch) {
    if (!canPerformTextSearch) {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_LABEL, UNAVAILABLE_TEXT_SEARCH);
    } else if (hasTextPill) {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_LABEL, DISABLED_TEXT_SEARCH);
    } else {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_DISABLED_LABEL, ENABLED_TEXT_SEARCH);
    }
    return this._afterOptionsMenu;
  },

  /**
   * We take away the ability to create FF in edit mode.
   * Therefore, no Advanced Options while editing.
   */
  @computed('isEditing')
  optionsComponent(isEditing) {
    return isEditing ? this.get('_options') : AFTER_OPTIONS_COMPONENT;
  },

  /**
   * The value typed into power-selects input
   * @private
   */
  _searchString: null,

  /**
   * Prepares `valueString` for display in the template. If the incoming value
   * is single quoted, it will replace the wrapping single quotes with styled
   * single quotes. This helps the user to differentiate the wrapping quotes
   * from quotes within the string.
   * @private
   */
  @computed('valueString')
  valueDisplay(valueString) {
    let ret = valueString;
    if (typeof(valueString) === 'string') {
      const match = valueString.match(properlyQuoted);
      if (match) {
        // For text values with html tags, we will need to encode the string,
        // before passing it through htmlSafe so that the original value is retained.
        // This is an internal Ember API function
        const transformedString = Utils.escapeExpression(match[1]);
        ret = htmlSafe(`
          <span class="quote-highlight">'</span>
          ${transformedString}
          <span class="quote-highlight">'</span>
        `);
      }
    }
    return ret;
  },

  /**
   * Indicates if something is being rendered by this template and that it isn't
   * empty. Controls whether padding/spacing is required
   * @private
   */
  @computed('valueString', 'isActive')
  isPopulated(valueString, isActive) {
    return (!!valueString && valueString.length > 0) || isActive;
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
      [KEY_MAP.arrowUp.key]: this._navigationHandler.bind(this),
      [KEY_MAP.backspace.key]: this._keyHandler.bind(this),
      [KEY_MAP.enter.key]: this._commandHandler.bind(this),
      [KEY_MAP.escape.key]: this._commandHandler.bind(this),
      [KEY_MAP.tab.key]: this._navigationHandler.bind(this)
    });
    // _debugContainerKey is a private Ember property that returns the full
    // component name (component:query-container/pill-value).
    const [ , source ] = this._debugContainerKey.split('/');
    this.set('source', source);
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
      this._broadcast(MESSAGE_TYPES.VALUE_CLICKED);
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
      if (previousValue) {
        event.target.value = '';
      }
      const value = previousValue + pastedValue;

      if (!value) {
        return;
      }

      this._broadcast(MESSAGE_TYPES.VALUE_PASTE, value);
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

    onBlur(powerSelectAPI) {
      const { searchText } = powerSelectAPI;
      // If this component looses focus while there is a value, we need to save
      // it off so that the inactive state renders properly.
      if (searchText !== null && searchText !== '' && searchText !== this.get('_searchString')) {
        this._broadcast(MESSAGE_TYPES.VALUE_SET, searchText);
        // force text back into view
        const el = this.element.querySelector('input');
        if (el) {
          el.value = searchText;
        }
      }
    },

    onFocus(powerSelectAPI) {
      const { actions } = powerSelectAPI;
      const valueString = this.get('valueString') || '';
      const trimmedValueString = stripOuterSingleQuotes(valueString).trim();
      if (trimmedValueString !== '') {
        // force text back into view
        const el = this.element.querySelector('input');
        if (el) {
          el.value = trimmedValueString;
          this.set('_searchString', trimmedValueString);
        }
        actions.search(trimmedValueString);
      }
      actions.open();
    },

    /**
     * This function is call after a key is pressed but before power-select has
     * had an opportunity to react.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      this.set('_searchString', input);
      const { options } = powerSelectAPI;
      if (input.length === 0) {
        this.set('selection', null);
        // Set the power-select highlight on the next runloop so that the
        // power-select has time to render the full list of options.
        next(this, () => powerSelectAPI.actions.highlight(options[0]));
      }

      debounce(this, this._broadcastRecentQuerySearch, input, 100);
      // Need to make a decision about highlight and marking it as complex
      // only for the first time while creating a pill.
      if (!this.get('isEditing')) {
        const isComplex = hasComplexText(input);
        this.set('_isComplex', isComplex);
        if (isComplex) {
          next(this, () => {
            // Remove highlighting of any item in main list. Advanced Options
            // highlighting is handled in power-select-after-options
            // component
            powerSelectAPI.actions.highlight(null);
          });
        }
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
      const fn = this._keyDownHandlerMap[event.key];
      return fn ? fn(powerSelectAPI, event) : true;
    },

    /*
     * This function is called when you press ENTER. It's effectively the same
     * as clicking on the 'Query Pill` option
     */
    onChange(selection, powerSelectAPI/* , event */) {
      if (selection !== null) {

        let searchText, value;
        if (selection.description === POWER_SELECT_OPTIONS_QUERY_LABEL) {
          // If Default option was chosen, pick out the text typed in.
          searchText = powerSelectAPI.searchText;
        } else if (selection.description === 'Suggestions') {
          // Otherwise, one of the display values were picked.
          searchText = selection.displayName;
        }
        const { actions } = powerSelectAPI;
        const isComplex = this.get('_isComplex');
        if (isComplex) {
          value = searchText.trim();
        } else {
          const trimmedInput = stripOuterSingleQuotes(searchText).trim();
          value = escapeSingleQuotes(escapeBackslash(trimmedInput));
        }
        // cleanup
        this.set('_searchString', undefined);
        actions.search('');

        if (!this._isInputEmpty(value)) {
          this._broadcast(MESSAGE_TYPES.VALUE_ENTER_KEY, value);
        }
      }
    },

    onOptionMouseDown() {
      this.set('swallowNextFocusOut', true);
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
      this._broadcast(MESSAGE_TYPES.VALUE_ESCAPE_KEY);
    } else if (isEnter(event)) {
      const afterOptionsMenuItem = this._afterOptionsMenu.highlightedItem;
      const { searchText, highlighted } = powerSelectAPI;
      if (searchText === '' && !afterOptionsMenuItem && !highlighted) {
        // No text was entered and it's not a FF or text selection,
        // so quick exit
        return false;
      }
      // This is triggered when choosing afterOptions and we hit enter.
      // Since Query Filter option will always be there, onChange will
      // handle it's selection. Here we just take care of afterOptions.
      if (afterOptionsMenuItem) {
        this._createPillFromAdvancedOption(afterOptionsMenuItem.label);
        powerSelectAPI.actions.search('');
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
    if (isBackspace(event) && event.target.value === '') {
      next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_BACKSPACE_KEY));
    } else if (isBackspace(event) && event.target.value.length === 1) {
      // This handles the situation where you clear out the value, but don't
      // press backspace to move back to operator. We reset `valueString` so
      // that the cleared out value doesn't reappear when losing/gaining
      // focus.
      next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_SET, ''));
    }
  },

  /**
   * Handle keys that are used for moving focus around the application.
   * @return {boolean} Should further processing by EPS continue?
   * @private
   */
  _navigationHandler(powerSelectAPI, event) {
    if (isArrowLeft(event) && event.target.selectionStart === 0) {
      const { value } = event.target;
      next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY, value));
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
    this._broadcast(
      MESSAGE_TYPES.RECENT_QUERIES_TEXT_TYPED,
      { data: searchQueryText, dataSource: this.get('source') }
    );
  },

  _focusOnPowerSelectTrigger() {
    const trigger = this.element.querySelector('.pill-value input');
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
    const { value } = el;
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

  _isInputEmpty: (input) => {
    const trimmedInput = input.trim();
    const isEmpty = trimmedInput.length === 0;
    const hasEmptyQuotes = trimmedInput.match(/^['"]\s*['"]$/);
    return isEmpty || (hasEmptyQuotes && hasEmptyQuotes.length > 0);
  },

  /**
   * Function used by EPS to highlight its options.
   * If there is some text in the input, we'll highlight the first option.
   * Otherwise, nothing at all.
   */
  _highlighter(powerSelectAPI) {
    const { searchText } = powerSelectAPI;
    if (searchText && searchText.trim().length > 0) {
      const { results } = powerSelectAPI;
      return results[0];
    }
  },

  /**
   * Function used by EPS to down-select options given what's typed into the
   * search box.
   * Query_Filter option will always match by default.
   * @private
   */
  _matcher(value, input) {
    const _input = input.toLowerCase().replace(LEADING_SPACES, '');
    if (value.description === POWER_SELECT_OPTIONS_QUERY_LABEL) {
      return 0;
    }
    const _displayName = value.displayName.toLowerCase();
    return _displayName.indexOf(_input);
  }
});