import Component from '@ember/component';
import { next, scheduleOnce } from '@ember/runloop';
import { htmlSafe } from '@ember/string';
import computed, { equal } from 'ember-computed-decorators';
import {
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
  AFTER_OPTION_QUERY_LABEL,
  AFTER_OPTION_TAB_META,
  AFTER_OPTION_TAB_RECENT_QUERIES
} from 'investigate-events/constants/pill';
import * as MESSAGE_TYPES from '../message-types';
import Ember from 'ember';
import BoundedList from 'investigate-events/util/bounded-list';
import { hasComplexText } from 'investigate-events/util/query-parsing';

const { log } = console;// eslint-disable-line no-unused-vars

const DISABLED_TEXT_SEARCH = {
  label: AFTER_OPTION_TEXT_DISABLED_LABEL,
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
* The options used for the power-select. They are grouped to provide a way to
* have 2 "lists". One list is the default type of pill to create, the second
* list is the advanced options (creating free-form or text pills). The first
* group's name is hidden via CSS, but we still need to give it a name so it
* renders. That's why it's a space character.
*/
const _dropDownOptions = [AFTER_OPTION_QUERY_LABEL];

export default Component.extend({
  classNameBindings: ['isPopulated', ':pill-value'],

  /**
   * Does the entire pills list have a text pill already?
   * @type {string}
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

  _dropDownOptions: [AFTER_OPTION_QUERY_LABEL],

  /**
   * List of recent queries
   * @type {Array}
   * @public
   */
  recentQueries: null,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  /**
   * The value to display.
   * @type {string}
   * @public
   */
  valueString: null,

  /**
   * List object for advanced dropdown options
   */
  _afterOptionsMenu: BoundedList.create({ list: AFTER_OPTIONS_MENU }),

  /**
   * Based on the current tab selected, we replace options
   * for the power select component.
   * Default is Query_Pill option.
   */
  @computed('activePillTab', 'recentQueries')
  selectableOptions(activePillTab, recentQueries) {
    const options = this.get('_dropDownOptions');
    return (activePillTab === AFTER_OPTION_TAB_RECENT_QUERIES) ? recentQueries : options;
  },

  /**
   * This indicates if the meta tab is active.
   * Not related to pill-value drop-down.
   */
  @equal('activePillTab', AFTER_OPTION_TAB_META)
  isMetaTabActive: false,

  /**
   * Placeholder messages
   */
  @computed('isMetaTabActive', 'i18n.locale')
  noMatchesMessage(isMetaTabActive) {
    const i18n = this.get('i18n');
    if (!isMetaTabActive) {
      return i18n.t('queryBuilder.recentQueriesNoMatch');
    } else {
      // Query Pill will always be highlighted for pill-value
      return undefined;
    }
  },

  @computed('hasTextPill')
  _groomedAfterOptionsMenu(hasTextPill) {
    if (hasTextPill) {
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
    return isEditing ? _dropDownOptions : AFTER_OPTIONS_COMPONENT;
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
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.AFTER_OPTIONS_SELECTED]: (d) => this._createPillFromAdvancedOption(d),
      [MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT]: (index) => this._afterOptionsMenu.highlightIndex = index,
      [MESSAGE_TYPES.AFTER_OPTIONS_REMOVE_HIGHLIGHT]: () => this._afterOptionsMenu.clearHighlight(),
      [MESSAGE_TYPES.AFTER_OPTIONS_TAB_CLICKED]: () => this._afterOptionsTabToggle()
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
      this._broadcast(MESSAGE_TYPES.VALUE_CLICKED);
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

    onBlur(powerSelectAPI) {
      const { searchText } = powerSelectAPI;
      // If this component looses focus while there is a value, we need to save
      // it off so that the inactive state renders properly.
      if (searchText !== null && searchText !== '') {
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
      if (isEscape(event)) {
        this._broadcast(MESSAGE_TYPES.VALUE_ESCAPE_KEY);
      } else if (isBackspace(event) && event.target.value === '') {
        next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_BACKSPACE_KEY));
      } else if (isBackspace(event) && event.target.value.length === 1) {
        // This handles the situation where you clear out the value, but don't
        // press backspace to move back to operator. We reset `valueString` so
        // that the cleared out value doesn't reappear when losing/gaining
        // focus.
        next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_SET, ''));
      } else if (isArrowLeft(event) && event.target.selectionStart === 0) {
        const { value } = event.target;
        next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY, value));
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
      } else if (isEnter(event)) {
        const afterOptionsMenuItem = this._afterOptionsMenu.highlightedItem;
        const { searchText } = powerSelectAPI;
        if (searchText === '' && !afterOptionsMenuItem) {
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

    /*
     * This function is called when you press ENTER. It's effectively the same
     * as clicking on the 'Query Pill` option
     */
    onChange(selection, powerSelectAPI/* , event */) {
      if (selection !== null && selection === AFTER_OPTION_QUERY_LABEL) {
        const { actions, searchText } = powerSelectAPI;
        const isComplex = this.get('_isComplex');
        let value;
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
   * Active tab was toggled.
   */
  _afterOptionsTabToggle() {
    this._broadcast(MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, {});
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

  _createPillFromAdvancedOption(selection) {
    // get input text
    const el = this.element.querySelector('.ember-power-select-typeahead-input');
    const { value } = el;
    // cleanup
    el.value = '';
    this._focusOnPowerSelectTrigger();
    this._afterOptionsMenu.clearHighlight();
    // send value up to create a complex pill
    // _debugContainerKey is a private Ember property that returns the full
    // component name (component:query-container/pill-meta).
    const [ , source ] = this._debugContainerKey.split('/');
    if (selection === AFTER_OPTION_FREE_FORM_LABEL) {
      this._broadcast(MESSAGE_TYPES.CREATE_FREE_FORM_PILL, [value, source]);
    } else if (selection === AFTER_OPTION_TEXT_LABEL) {
      this._broadcast(MESSAGE_TYPES.CREATE_TEXT_PILL, [value, source]);
    }
  },

  _isInputEmpty: (input) => {
    const trimmedInput = input.trim();
    const isEmpty = trimmedInput.length === 0;
    const hasEmptyQuotes = trimmedInput.match(/^['"]\s*['"]$/);
    return isEmpty || (hasEmptyQuotes && hasEmptyQuotes.length > 0);
  },

  /**
   * Function used by EPS to down-select options given what's typed into the
   * search box. Since we don't want it to actually change, this function
   * just returns `true`.
   * @private
   */
  _matcher: () => true,

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
  }
});