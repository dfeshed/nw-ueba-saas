import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { assert } from '@ember/debug';
import { connect } from 'ember-redux';
import { next, scheduleOnce, debounce, later } from '@ember/runloop';
import { isEmpty } from '@ember/utils';

import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL,
  AFTER_OPTION_TEXT_DISABLED_DUPLICATE_LABEL,
  AFTER_OPTION_TEXT_DISABLED_PARENS_LABEL,
  AFTER_OPTION_TAB_RECENT_QUERIES,
  AFTER_OPTION_TAB_META,
  LOADING_SPINNER_SELECTOR,
  POWER_SELECT_INPUT,
  POWER_SELECT_TRIGGER_INPUT
} from 'investigate-events/constants/pill';
import * as MESSAGE_TYPES from '../message-types';
import BoundedList from 'investigate-events/util/bounded-list';
import {
  isArrowDown,
  isArrowUp,
  isEnter,
  isEscape,
  isShiftTab,
  isTab,
  isHome,
  isEnd,
  isDelete,
  isBackspace,
  isCtrlA
} from 'investigate-events/util/keys';
import {
  addAndRemoveElements,
  addNoResultsMessage,
  removeNoResultsMessage
} from '../query-pill/query-pill-util';
import { hasOperator } from 'investigate-events/util/query-parsing';

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

const _isFirstChar = (event) => event?.target?.value === '';

const stateToComputed = (state) => ({
  recentQueriesUnfilteredList: state.investigate.queryNode.recentQueriesUnfilteredList || [],
  recentQueriesFilteredList: state.investigate.queryNode.recentQueriesFilteredList || [],
  recentQueriesCallInProgress: state.investigate.queryNode.recentQueriesCallInProgress
});

const RecentQueryComponent = Component.extend({
  classNameBindings: [':recent-query', 'isExpanded', 'inProgress'],

  /**
   * Does this component currently have focus?
   * @type {boolean}
   * @public
   */
  isActive: false,

  /**
   * Clean up input trailing text
   */
  shouldCleanInputFields: false,

  /**
   * Does the entire pills list have a text pill already?
   * @type {string}
   * @public
   */
  hasTextPill: false,

  /**
   * Whether or not this pill is inside one or more pairs of parens
   */
  isInsideParens: false,

  /**
   * power-select's public API
   * Comes in handy when we want to use its actions
   * to interact with it.
   */
  powerSelectAPI: undefined,

  /**
   * Will keep it undefined most of the times. But
   * when it's not, this string will be used to prepopulate
   * recent-query EPS input.
   * @type {String}
   * @public
   */
  prepopulatedRecentQueryText: undefined,

  /**
   * Placeholder text
   */
  pillPlaceholder: null,

  /**
   * Will contain the name of the component
   */
  source: undefined,

  /**
   * In order to force selectable options computed
   * property to recompute itself in certain situations,
   * we use this flag as a trigger.
   */
  triggerRecentQueryMaintenance: false,

  /**
   * If this is the first empty pill?
   * We will use this flag to close meta dropdown if HOME is pressed
   * from an right most empty pill. If it's the first pill, we do not
   * need to close the dropdown, as there would be no pill on the left.
   * @type {boolean}
   * @public
   */
  isFirstPill: false,

  /**
   * If this is the last empty pill?
   * We will use this flag to close meta dropdown if END is pressed
   * from an empty pill. If it's the right most empty pill, we do not
   * need to close the dropdown, as there would be no pill on the right.
   * @type {boolean}
   * @public
   */
  isLastPill: false,

  /**
   * Does this component consume the full width of its parent, or is it sized to
   * match its contents?
   * @type {boolean}
   * @public
   */
  @alias('isActive')
  isExpanded: false,

  /**
   * Function to call when sending messages and data to the parent component.
   */
  sendMessage: () => {},

  /**
   * Which is the active tab for EPS?
   * Options will change based on it
   */
  @computed('isActive')
  activePillTab(isActive) {
    if (isActive) {
      return AFTER_OPTION_TAB_RECENT_QUERIES;
    }
    return AFTER_OPTION_TAB_META;
  },

  /**
   * We take away the ability to create FF in edit mode.
   * Therefore, no Advanced Options while editing.
   */
  @computed('isEditing')
  optionsComponent(isEditing) {
    return isEditing ? undefined : AFTER_OPTIONS_COMPONENT;
  },

  @computed('hasTextPill', 'isInsideParens')
  _groomedAfterOptionsMenu(hasTextPill, isInsideParens) {
    if (hasTextPill) {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_LABEL, DISABLED_TEXT_SEARCH_DUPLICATE);
    } else if (isInsideParens) {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_LABEL, DISABLED_TEXT_SEARCH_PARENS);
    } else {
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_DISABLED_DUPLICATE_LABEL, ENABLED_TEXT_SEARCH);
      this._afterOptionsMenu.replaceItemByLabel(AFTER_OPTION_TEXT_DISABLED_PARENS_LABEL, ENABLED_TEXT_SEARCH);
    }
    return this._afterOptionsMenu;
  },

  /**
   * Based on the current tab selected, we replace options
   * for the power select component.
   * Default are metaOptions.
   */
  @computed('recentQueriesUnfilteredList', 'recentQueriesFilteredList', 'triggerRecentQueryMaintenance')
  options(recentQueriesUnfilteredList, recentQueriesFilteredList) {

    const el = this.element.querySelector(POWER_SELECT_INPUT);
    if (el) {
      // If there is some text in the input, we display the filteredList,
      // otherwise, unfilteredList.
      const { value } = el;

      if (value && !isEmpty(value.trim())) {

        // If recentQueriesCallInProgress just completed and based on the filteredList provided,
        // we determine if we need to add a No Results message or not.
        if (!this.get('recentQueriesCallInProgress')) {
          this._handleNoResultsMessage(recentQueriesFilteredList);
        }
        return recentQueriesFilteredList;
      }

    }
    this._handleNoResultsMessage(recentQueriesUnfilteredList);
    return recentQueriesUnfilteredList;
  },

  /**
   * If this pill-meta is active, and the status of recentQueriesCallInProgress changes,
   * we need to maintain the spinner's presence and deal with maintaining a potential no results message
   */
  @computed('recentQueriesCallInProgress')
  inProgress(recentQueriesCallInProgress) {
    addAndRemoveElements(recentQueriesCallInProgress, this);
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
    // _debugContainerKey is a private Ember property that returns the full
    // component name (component:query-container/pill-meta).
    const [ , source ] = this._debugContainerKey.split('/');
    this.set('source', source);
  },

  didReceiveAttrs() {
    this._super(...arguments);

    if (this.get('isActive')) {

      // If there is some prepopulated text coming in from meta tabs,
      // the intent is to focus.
      // onFocus function takes in that text, searches on it using the public
      // API, which automatically sets the text in this component.
      scheduleOnce('afterRender', this, '_focusOnPowerSelectTrigger');
    } else {
      // Not active, please close the drop-down
      const powerSelectAPI = this.get('powerSelectAPI');
      if (powerSelectAPI) {
        powerSelectAPI.actions.close();
      }
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    if (this.get('shouldCleanInputFields')) {
      this._cleanupInputField();
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

    onChange(selection) {
      if (selection !== null) {
        this._broadcast(MESSAGE_TYPES.RECENT_QUERY_SELECTED, selection.query);
        this._afterOptionsMenu.clearHighlight();
      }
    },

    onClose() {
      this._cleanupRecentQueries();
    },

    /**
     * This function is called on every `input` event from the power-select's
     * trigger element. It happens before power-select has reacted to what was
     * typed. It clear out any EPS search related properties to prevent the
     * previously highlighted item from staying highlighted.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      if (input.trim().length === 1) {
        this._broadcast(MESSAGE_TYPES.PILL_META_CHAR_ENTERED);
      } else if (input.trim().length === 0) {
        powerSelectAPI.actions.search('');
        powerSelectAPI.actions.highlight(null);
        this._afterOptionsMenu.clearHighlight();
        this._broadcast(MESSAGE_TYPES.META_SELECTED, null);
        this.toggleProperty('triggerRecentQueryMaintenance');
      }
    },

    onFocus(powerSelectAPI) {
      this.set('powerSelectAPI', powerSelectAPI);
      if (this.get('prepopulatedRecentQueryText') !== undefined) {
        powerSelectAPI.actions.search(this.get('prepopulatedRecentQueryText'));
        if (powerSelectAPI.resultsCount === 0) {
          // Force free-form pill highlight if the search text has operators or a
          // text pill already exists
          if (hasOperator(this.get('prepopulatedRecentQueryText')) || this.get('hasTextPill')) {
            this._afterOptionsMenu.highlightIndex = 0; // Free-form
          // Otherwise, set it to text pill
          } else {
            this._afterOptionsMenu.highlightIndex = 1; // Text pill
          }
        }
      }
      powerSelectAPI.actions.open();
      // If RQ tab is open and unfilteredList is empty, we need to add
      // No results message
      // It takes a moment for the text to actully appear in the power select input.
      // We add a small delay so that options computed takes into consideration that there
      // is some text in the input and so it should display it's filtered list.
      later(() => this.toggleProperty('triggerRecentQueryMaintenance'), 100);
    },

    onKeyDown(powerSelectAPI) {
      if (isEscape(event)) {
        // Close dropdown
        powerSelectAPI.actions.search('');
        powerSelectAPI.actions.close();
        this._cleanupInputField();
        this._dropFocus();
        // Let others know ESC was pressed
        this._broadcast(MESSAGE_TYPES.RECENT_QUERIES_ESCAPE_KEY);
      } else if (isEnter(event)) {
        const afterOptionsMenuItem = this._afterOptionsMenu.highlightedItem;
        if (afterOptionsMenuItem) {
          // If the user presses ENTER while the "after option" is set, the
          // assumption is that they want to create a free-form or text filter.
          // We check this first because it's possible to have a `selection` and
          // a `selected` that matches the `if` case below because this code
          // runs before power-select reacts to the key press.
          this._createPillFromAdvancedOption(afterOptionsMenuItem.label);
          powerSelectAPI.actions.search('');
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
        } else if (this._afterOptionsMenu.highlightedIndex === 0 && powerSelectAPI.resultsCount > 0) {
          // At top of advanced options, move back to meta
          const { actions, results } = powerSelectAPI;
          const lastItem = results[results.length - 1];
          this._afterOptionsMenu.clearHighlight();
          actions.scrollTo(lastItem);
          actions.highlight(lastItem);
          return false;
        } else if (this._afterOptionsMenu.highlightedIndex === 0 && powerSelectAPI.resultsCount === 0) {
          // At top of after options, but there are no options to highlight in
          // the meta list, so do nothing.
          return false;
        }
      } else if (isTab(event) || isShiftTab(event)) {
        event.preventDefault();
        // For now we have just 2 options, so can toggle.
        // Will need to make an informed decision once more tabs
        // are added.
        this._afterOptionsTabToggle();
        return false;
      } else if (isHome(event) && event.target.value == '') {
        // PILL_HOME_PRESSED message is broadcasted when home button is pressed and recent query value is empty
        if (!this.get('isFirstPill')) {
          this._clearDropDown(powerSelectAPI);
          this._broadcast(MESSAGE_TYPES.PILL_HOME_PRESSED, { isFromRecentQuery: true });
        }
      } else if (isEnd(event) && event.target.value == '') {
        // PILL_END_PRESSED message is broadcasted when end button is pressed and and recent query value is empty
        if (!this.get('isLastPill')) {
          this._clearDropDown(powerSelectAPI);
          this._broadcast(MESSAGE_TYPES.PILL_END_PRESSED);
        }
      } else if (isDelete(event) && !this.get('isLastPill')) {
        // Message is broadcasted when delete button is pressed.
        this._clearDropDown(powerSelectAPI);
        this._broadcast(MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, {
          isFocusedPill: false,
          isDeleteEvent: true,
          isBackspaceEvent: false,
          isFromRecentQuery: true
        });
      } else if (isBackspace(event) && !this.get('isFirstPill')) {
        // Message is broadcasted when delete button is pressed.
        this._clearDropDown(powerSelectAPI);
        this._broadcast(MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, {
          isFocusedPill: false,
          isDeleteEvent: false,
          isBackspaceEvent: true,
          isFromRecentQuery: true
        });
      } else if (isCtrlA(event)) {
        if (!this.get('isEditing') && _isFirstChar(event)) {
          this._broadcast(MESSAGE_TYPES.FOCUSED_PILL_CTRL_A_PRESSED);
        }
      } else {
        // If recent queries tab is open and some text is typed in,
        // this is a possible search against recent queries API.
        this._handleRecentQuerySearch(event);
      }
    },

    onOptionMouseEnter() {
      this._afterOptionsMenu.clearHighlight();
    }
  },

  // ************************ Event Listeners *************************  //

  /**
   * In case some text is pasted in queries tab, we force an API call
   */
  paste() {
    this._handleRecentQuerySearch();
  },

  /**
   * In case some text is cut in queries tab, we force an API call
   */
  cut() {
    this._handleRecentQuerySearch();
  },

  // ************************************************************************ //
  //                          PRIVATE FUNCTIONS                               //
  // ************************************************************************ //
  /**
   * Active tab was toggled.
   */
  _afterOptionsTabToggle() {
    this._afterOptionsMenu.clearHighlight();
    const el = this.element.querySelector(POWER_SELECT_INPUT);
    const { value } = el;
    removeNoResultsMessage(this);
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

  /**
   * Used by power-select to position the dropdown.
   * @private
   */
  _calculatePostion(trigger, dropdown) {
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
    if (value && value.length > 0) {
      if (selection === AFTER_OPTION_FREE_FORM_LABEL) {
        this._broadcast(MESSAGE_TYPES.CREATE_FREE_FORM_PILL, [value, this.get('source')]);
      } else if (selection === AFTER_OPTION_TEXT_LABEL) {
        this._broadcast(MESSAGE_TYPES.CREATE_TEXT_PILL, [value, this.get('source')]);
      }
    }
  },

  _currentInputText() {
    const element = this.element.querySelector(POWER_SELECT_INPUT);
    if (element) {
      const { value } = element;
      return value;
    }
  },

  /**
   * In cases when Escape is pressed and there is some leftover text present,
   * clean that up.
   */
  _cleanupInputField() {
    const el = this.element.querySelector(POWER_SELECT_INPUT);
    const { value } = el;
    if (value) {
      el.value = '';
    }
  },

  /**
   * Cleanup activities when either escape is pressed or clicked away
   */
  _cleanupRecentQueries() {
    const spinner = document.querySelector(LOADING_SPINNER_SELECTOR);
    if (spinner) {
      spinner.parentNode.removeChild(spinner);
    }
    this.toggleProperty('triggerRecentQueryMaintenance');

  },

  _dropFocus() {
    const el = this.element.querySelector('.recent-queries input');
    if (el && el === document.activeElement) {
      el.blur();
    }
  },

  _focusOnPowerSelectTrigger() {
    const trigger = this.element.querySelector(POWER_SELECT_TRIGGER_INPUT);
    if (trigger) {
      trigger.focus();
    }
  },

  /**
   * Based on the optionsArray passed in, determine if we want a No Results
   * Message or not.
   */
  _handleNoResultsMessage(optionsArray) {
    if (optionsArray) {
      if (optionsArray.length > 0) {
        removeNoResultsMessage(this);
      } else {
        const i18n = this.get('i18n');
        addNoResultsMessage(i18n.t('queryBuilder.recentQueriesNoMatch'), this);
      }
    }
  },

  /**
   * Determine if the text typed in power-select needs to be sent out
   * as a searchTerm for recentQuery API call.
   */
  _handleRecentQuerySearch(event) {
    if (event) {
      next(() => {
        const { value } = event.target;
        const searchTerm = value;
        if (value && value.trim().length > 0) {
          // send the original text with possible spaces
          debounce(this, this._broadcastRecentQuerySearch, searchTerm, 100);
        }
      });
    } else {
      // Give a moment for text to appear
      next(() => {
        const text = this._currentInputText();
        this._broadcastRecentQuerySearch(text);
      });
    }
  },

  _matcher: (option, input) => {
    const _input = input.toLowerCase().replace(LEADING_SPACES, '');
    const _query = option.query.toLowerCase();
    return _query.indexOf(_input);
  },

  _clearDropDown(powerSelectAPI) {
    powerSelectAPI.actions.search('');
    powerSelectAPI.actions.close();
    this._cleanupInputField();
    this._dropFocus();
  }
});

export default connect(stateToComputed, undefined)(RecentQueryComponent);
