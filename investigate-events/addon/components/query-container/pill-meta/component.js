import Component from '@ember/component';
import { cancel, later, next, scheduleOnce, debounce } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import * as MESSAGE_TYPES from '../message-types';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL,
  AFTER_OPTION_TEXT_DISABLED_LABEL,
  AFTER_OPTION_TEXT_UNAVAILABLE_LABEL,
  LOGICAL_OPERATORS,
  OPERATOR_AND,
  OPERATOR_OR,
  POWER_SELECT_INPUT,
  POWER_SELECT_TRIGGER_INPUT
} from 'investigate-events/constants/pill';
import KEY_MAP, {
  isArrowDown,
  isArrowLeft,
  isArrowRight,
  isArrowUp,
  isCloseParen,
  isEnter,
  isEscape,
  isOpenParen,
  isShiftTab,
  isSpace,
  isTab,
  isHome,
  isEnd,
  isDelete
} from 'investigate-events/util/keys';
import BoundedList from 'investigate-events/util/bounded-list';
import { inject as service } from '@ember/service';
import { assert } from '@ember/debug';
import { matcher } from '../query-pill/query-pill-util';
import { filterValidMeta, lastValidMeta } from 'investigate-events/util/meta';

const { log } = console;// eslint-disable-line

const AND_OPERATOR_STRINGS = ['&&', 'AND'];
const OR_OPERATOR_STRINGS = ['||', 'OR'];

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

const _dropFocus = () => {
  const el = document.querySelector('.pill-meta input');
  if (el && el === document.activeElement) {
    el.blur();
  }
};

const _isFirstChar = (event) => event?.target?.value === '';

const _isLogicalOperator = (string) => LOGICAL_OPERATORS.includes(string);

const _getOperatorType = (str) => {
  let type;
  if (AND_OPERATOR_STRINGS.includes(str)) {
    type = OPERATOR_AND;
  } else if (OR_OPERATOR_STRINGS.includes(str)) {
    type = OPERATOR_OR;
  }
  return type;
};

const AFTER_OPTIONS_COMPONENT = 'query-container/power-select-after-options';

export default Component.extend({
  classNameBindings: ['isExpanded', ':pill-meta'],

  /**
   * Are all Core Services at a revision that allows Text searching to be
   * performed?
   * @type {boolean}
   * @public
   */
  canPerformTextSearch: true,

  /**
   * Clean up input trailing text
   */
  shouldCleanInputFields: false,

  /**
   * Does the entire pills list have a text pill already?
   * @type {boolean}
   * @public
   */
  hasTextPill: false,

  /**
   * Does this component currently have focus?
   * @type {boolean}
   * @public
   */
  isActive: false,

  /**
   * Should we automatically display the EPS dropdown?
   * @type {boolean}
   * @public
   */
  isAutoFocused: true,

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
   * Which is the active tab for EPS?
   * Options will change based on it
   */
  activePillTab: undefined,

  /**
   * The option that is currently selected
   * @type {Object}
   * @public
   */
  selection: null,

  /**
   * Will contain the name of the component
   */
  source: undefined,

  /**
   * Function that power-select uses to make an autosuggest match.
   */
  _matcher: matcher,

  /**
   * List of meta for selection
   * @type {Object}
   * @public
   */
  metaOptions: null,

  /**
   * Will keep it undefined most of the times. But
   * when it's not, this string will be used to prepopulate
   * pil-meta EPS input.
   * @type {String}
   * @public
   */
  prepopulatedMetaText: undefined,

  /**
   * Placeholder text
   */
  pillPlaceholder: null,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  /**
   * Whether or not to send the next focus out event that occurs
   * @type {boolean}
   * @public
   */
  swallowNextFocusOut: false,

  /**
   * If this is the first empty pill?
   * We will use this flag to close meta dropdown if ARROW_LEFT is pressed
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

  i18n: service(),

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

  @computed('isActive', 'metaOptions')
  isActiveWithOptions: (isActive, metaOptions) => isActive && metaOptions.length > 0,

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
      [KEY_MAP.closeParen.key]: this._groupingHandler.bind(this),
      [KEY_MAP.enter.key]: this._commandHandler.bind(this),
      [KEY_MAP.escape.key]: this._commandHandler.bind(this),
      [KEY_MAP.openParen.key]: this._groupingHandler.bind(this),
      [KEY_MAP.space.key]: this._keyHandler.bind(this),
      [KEY_MAP.tab.key]: this._navigationHandler.bind(this),
      [KEY_MAP.home.key]: this._navigationHandler.bind(this),
      [KEY_MAP.end.key]: this._navigationHandler.bind(this),
      [KEY_MAP.delete.key]: this._navigationHandler.bind(this)
    });
    // _debugContainerKey is a private Ember property that returns the full
    // component name (component:query-container/pill-meta).
    const [ , source ] = this._debugContainerKey.split('/');
    this.set('source', source);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    if (this.get('isActive') && this.get('isAutoFocused')) {
      // We schedule this after render to give time for the power-select to
      // be rendered before trying to focus on it.
      scheduleOnce('afterRender', this, '_focusOnPowerSelectTrigger');
    }
    if (this.get('shouldCleanInputFields')) {
      this._cleanupInputField();
    }
  },

  click() {
    // If this component is not active and the user clicks on it, dispatch an
    // action so that the parent can coordinate the activation of this component.
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.META_CLICKED);
    }
  },

  focusOut() {
    if (this.get('swallowNextFocusOut')) {
      this.set('swallowNextFocusOut', false);
      return false;
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

      const valueIsSubstringOfMeta = this.get('metaOptions').some((meta) => {
        return meta.metaName.includes(value.toLowerCase());
      });
      // If the value is a substring of the meta, replace whatever the user
      // pasted with it's toLowerCase. Otherwise, don't put any text in the input
      // and paste pills instead.
      if (!valueIsSubstringOfMeta) {
        this._broadcast(MESSAGE_TYPES.META_PASTE, value);
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
      const timer = this.get('metaSelectedTimer');
      if (timer) {
        // When editing a pill and selecting a new meta, the `onKeyDown()`
        // function is called before this function, which causes the previously
        // selected meta to be re-selected. To stop this, we put the
        // META_SELECTED dispatching into a `later` runloop and save off the
        // timer info. If the user actually changed the meta, we'll land
        // here, see if we have an outstanding timer, and cancel it if we do.
        // This will prevent the users' meta selection from being reversed.
        cancel(timer);
      }
      this._broadcast(MESSAGE_TYPES.META_SELECTED, selection);
      this._afterOptionsMenu.clearHighlight();
    },

    onFocus(powerSelectAPI, event) {
      const selection = this.get('selection');
      const targetValue = event.target.value;

      // If we gain focus and `lastSearchText` exists, power-select will use
      // that to down-select the list of metaOptions. This can happen if the
      // user enters some text, focuses away, then comes back. What they
      // previously typed will effect the list of metaOptions.
      if (powerSelectAPI.lastSearchedText && !selection) {
        // There was no previous selection, so see if the user has started
        // typing something into the <input>. If they have use that to
        // perform a search op options.
        const txt = targetValue || '';
        powerSelectAPI.actions.search(txt);
      } else if (selection) {
        // Check to see if the selected metaOption is valid for the power-select
        // options and select it if it is; otherwise clear it out.
        const option = this.get('metaOptions').find((d) => d.metaName === selection.metaName);
        if (option) {
          powerSelectAPI.actions.search(option.metaName);
        } else {
          this.set('selection', null);
        }
      } else if (this.get('prepopulatedMetaText') !== undefined) {
        // Tab has been switched from recent queries to meta
        // Some text has been prepopulated and focused in to be searched.
        powerSelectAPI.actions.search(this.get('prepopulatedMetaText'));
      }
      powerSelectAPI.actions.open();
    },

    /**
     * This function is called on every `input` event from the power-select's
     * trigger element. It happens before power-select has reacted to what was
     * typed. It clear out any EPS search related properties to prevent the
     * previously highlighted item from staying highlighted.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      if (input.length === 0) {
        this.set('selection', null);
        this._broadcast(MESSAGE_TYPES.META_SELECTED, null);
        powerSelectAPI.actions.highlight(null);
      } else if (_isLogicalOperator(input)) {
        // We handle logical operators here instead of in the keydown section
        // because we need to look at a string of characters, not an individual
        // key like the onKeyDown section
        const type = _getOperatorType(input);
        powerSelectAPI.actions.search('');
        this._broadcast(MESSAGE_TYPES.PILL_LOGICAL_OPERATOR, type);
        return false;
      } else {
        const searchTerm = input;
        if (input && input.trim().length > 0) {
          // send the original text with possible spaces
          debounce(this, this._broadcastRecentQuerySearch, searchTerm, 100);
        }
      }
    },

    /**
     * This function is called every time a key is pressed, and is invoked
     * before power-select reacts to the key that was pressed.
     * As a side note, we cannot combine `onInput`'s functionality here because
     * this code runs before any down-selection of options happens.
     * @private
     */
    onKeyDown(powerSelectAPI, event) {
      const fn = this._keyDownHandlerMap[event.key];
      return fn ? fn(powerSelectAPI, event) : true;
    },

    onOptionMouseDown() {
      // An option mouse down is the precursor to a click
      // of a power select option which causes a focus out
      // of this component. It isn't really a focusOut of this
      // component because control returns to it, so set
      // a flag to make sure the next focus out doesn't escape.
      this.set('swallowNextFocusOut', true);
    },

    onOptionMouseEnter() {
      this._afterOptionsMenu.clearHighlight();
    }
  },

  // ************************************************************************ //
  //                          PRIVATE FUNCTIONS                               //
  // ************************************************************************ //
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
   * Handle keys that perform some sort of action like executing a query or
   * canceling out of an edit.
   * @return {boolean} Should further processing by EPS continue?
   * @private
   */
  _commandHandler(powerSelectAPI, event) {
    if (isEscape(event)) {
      // If there is some half formed meta typed in, clean-up, as
      // just setting selectedMeta to null doesn't clear that out in pill-meta.
      if (!this.get('selection')) {
        this._cleanupInputField();
      }
      // Close dropdown
      powerSelectAPI.actions.close();
      // If we have focus, drop it like it's hot, drop it like it's hot.
      _dropFocus();
      // Let others know ESC was pressed
      this._broadcast(MESSAGE_TYPES.META_ESCAPE_KEY);
    } else if (isEnter(event)) {
      const { selected } = powerSelectAPI;
      const selection = this.get('selection');
      const afterOptionsMenuItem = this._afterOptionsMenu.highlightedItem;
      if (afterOptionsMenuItem) {
        // If the user presses ENTER while the "after option" is set, the
        // assumption is that they want to create a free-form or text filter.
        // We check this first because it's possible to have a `selection` and
        // a `selected` that matches the `if` case below because this code
        // runs before power-select reacts to the key press.
        this._createPillFromAdvancedOption(afterOptionsMenuItem.label);
        powerSelectAPI.actions.search('');
        next(this, () => powerSelectAPI.actions.open());
      } else if (selection && selected && selection === selected) {
        // If the user presses ENTER, selecting a meta that was already
        // selected, power-select does nothing. We want the focus to move onto
        // the pill operator.
        this.set('metaSelectedTimer', later(this, () => {
          this._broadcast(MESSAGE_TYPES.META_SELECTED, selection);
        }, 50));
      } else {
        next(this, () => {
          // We need to run this check in the next runloop so EPS has time to
          // react to the ENTER press in the first place. For example, to
          // make a selection.
          const selection = this.get('selection');
          const { value } = event.target;
          if (selection === null && !value) {
            powerSelectAPI.actions.close();
            _dropFocus();
            this._broadcast(MESSAGE_TYPES.META_ENTER_KEY);
          }
        });
      }
    }
    return true;
  },

  /**
   * Handle keys that need attention, but don't fall into a category that would
   * be handled by one of the other "handler" functions.
   * @return {boolean} Should further processing by EPS continue?
   * @private
   */
  _keyHandler(powerSelectAPI, event) {
    if (isSpace(event)) {
      const { results, searchText } = powerSelectAPI;
      // if isIndexedByNone then need to filter out from results
      const resultsFilteredByIsIndexedByNone = results.filter(filterValidMeta);
      const filteredResultsCount = resultsFilteredByIsIndexedByNone.length;

      // These conditionals return false to prevent any further handling of
      // the keypress that brought us here. Specifically, it prevents the
      // pill-operator from having a space at the beginning.
      if (filteredResultsCount === 1) {
        this._broadcast(MESSAGE_TYPES.META_SELECTED, resultsFilteredByIsIndexedByNone[0]);
        return false;
      } else if (filteredResultsCount > 1) {
        if (_isFirstChar(event)) {
          // ignore a leading space
          return false;
        }
        const match = this._hasExactMatch(searchText.trim(), results);
        if (match) {
          this._broadcast(MESSAGE_TYPES.META_SELECTED, match);
          return false;
        }
      }
    }
    return true;
  },

  /**
   * Handle keys that would group things.
   * @return {boolean} Should further processing by EPS continue?
   * @private
   */
  _groupingHandler(powerSelectAPI, event) {
    if (isOpenParen(event) && _isFirstChar(event)) {
      powerSelectAPI.actions.close();
      this._broadcast(MESSAGE_TYPES.PILL_OPEN_PAREN);
      return false;
    } else if (isCloseParen(event) && _isFirstChar(event)) {
      this._broadcast(MESSAGE_TYPES.PILL_CLOSE_PAREN);
    }
    return true;
  },

  /**
   * Handle keys that are used for moving focus around the application.
   * @return {boolean} Should further processing by EPS continue?
   * @private
   */
  _navigationHandler(powerSelectAPI, event) {
    const { selected, highlighted, results, actions } = powerSelectAPI;
    // filter out isIndexedByNone meta options
    const filteredResults = results.filter(filterValidMeta);
    // lastValidItem will be undefined if it does not exist
    const lastValidItem = lastValidMeta(results);
    const lastItemIsValid = results[results.length - 1] && !results[results.length - 1].disabled;

    if (isArrowRight(event)) {
      // Check if cursor position (selectionStart) is at the end of the
      // string. We use the selected metaName for comparision because we only
      // want to move forward if there's a selection.
      if (selected && event.target.selectionStart === selected.metaName.length) {
        next(this, () => this._broadcast(MESSAGE_TYPES.META_ARROW_RIGHT_KEY));
      } else if (event.target.selectionStart === 0) {
        // If there is no selection, we use this event to propogate up to
        // query-pills so that, if applicable, focus can be moved to the
        // pill on the right
        next(this, () => this._broadcast(MESSAGE_TYPES.META_ARROW_RIGHT_KEY_WITH_NO_SELECTION));
      }
    } else if (isArrowLeft(event) && event.target.selectionStart === 0) {
      // Move to the left of this pill
      next(this, () => {
        this._broadcast(MESSAGE_TYPES.META_ARROW_LEFT_KEY);
        // If you press ARROW_LEFT from the rightmost empty pill, we should close
        // the dropdown
        // If you press ARROW_LEFT from the leftmost empty pill, the dropdown should
        // remain open
        if (!this.get('isFirstPill')) {
          powerSelectAPI.actions.close();
        }
      });
    } else if (isArrowDown(event)) {
      const lastItem = results[results.length - 1];

      // Do not allow after options to be highlighted while editing
      if (!this.get('isEditing')) {
        if (event.ctrlKey || event.metaKey || highlighted === lastItem ||
          (lastValidItem && highlighted === lastValidItem && !lastItemIsValid)) {
          // CTRL/META was pressed or at bottom of meta list
          // or at the last valid enabled option
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
      } else if (this._afterOptionsMenu.highlightedIndex === 0 && powerSelectAPI.resultsCount > 0) {
        if (lastValidItem) {
          // At top of advanced options, move back to last valid meta
          this._afterOptionsMenu.clearHighlight();
          actions.scrollTo(lastValidItem);
          actions.highlight(lastValidItem);
          return false;
        } else {
          // At top of advanced options, but there are no options to highlight in
          // the meta list, so do nothing.
          return false;
        }
      } else if (highlighted && results[0] !== filteredResults[0] &&
          highlighted === filteredResults[0]) {
        // At the first valid enabled option
        // do nothing
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
    } else if (isHome(event) && (!this.get('isEditing') || event.target.value == '')) {
      // PILL_HOME_PRESSED message is broadcasted when home button is pressed and the pill is not in edit mode OR
      // home button is pressed and the pill is in edit mode and the pill meta value is empty
      if (!this.get('isFirstPill')) {
        this._clearMetaDropDown(powerSelectAPI);
      }
      this._broadcast(MESSAGE_TYPES.PILL_HOME_PRESSED);
    } else if (isEnd(event) && (!this.get('isEditing') || event.target.value == '')) {
      // PILL_END_PRESSED message is broadcasted when end button is pressed and the pill is not in edit mode OR
      // end button is pressed and the pill is in edit mode and the pill meta value is empty
      if (!this.get('isLastPill')) {
        this._clearMetaDropDown(powerSelectAPI);
      }
      this._broadcast(MESSAGE_TYPES.PILL_END_PRESSED);
    } else if (isDelete(event) && !this.get('isEditing') && !this.get('isLastPill')) {
      this._clearMetaDropDown(powerSelectAPI);
      this._broadcast(MESSAGE_TYPES.META_DELETE_PRESSED);
    }

    return true;
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
   * Used by power-select to position the dropdown.
   * @private
   */
  _calculatePosition(trigger, dropdown) {
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

  /**
   * In cases when Escape is pressed and there is some leftover text present,
   * clean that up.
   */
  _cleanupInputField() {
    const el = this.element.querySelector(POWER_SELECT_INPUT);
    if (el && el.value) {
      el.value = '';
    }
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

  _focusOnPowerSelectTrigger() {
    const trigger = this.element.querySelector(POWER_SELECT_TRIGGER_INPUT);
    if (trigger) {
      trigger.focus();
    }
  },

  _clearMetaDropDown(powerSelectAPI) {
    if (!this.get('selection')) {
      this._cleanupInputField();
    }
    powerSelectAPI.actions.close();
    // If we have focus, drop it.
    _dropFocus();
  },

  /**
   * Looks for a meta object with an exact match based on `metaName`.
   * @param {string} text The text to look for
   * @param {Object[]} metas An array of meta objects
   * @return {Object|undefined} The matching object or undefined
   * @private
   */
  _hasExactMatch: (text, metas = []) => metas.filter(filterValidMeta).find((m) => m.metaName === text),

  /**
   * Function that power-select uses to determine which item in the list of
   * options to highlight.
   * @param {Object} powerSelectAPI The power select public API
   * @private
   */
  _highlighter(powerSelectAPI) {
    const { options, results } = powerSelectAPI;
    if (options.length !== results.length) {
      const indexOfFirstValidMeta = results.findIndex(filterValidMeta);
      // if there is a valid meta option (isIndexedByNone === false), return it
      // if not, return undefined
      return indexOfFirstValidMeta > -1 ? results[indexOfFirstValidMeta] : undefined;

    } else {
      return null;
    }
  }
});
