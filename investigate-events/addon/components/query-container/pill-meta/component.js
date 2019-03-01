import Component from '@ember/component';
import { next, scheduleOnce } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import * as MESSAGE_TYPES from '../message-types';
import { isArrowLeft, isArrowRight, isEnter, isEscape } from 'investigate-events/util/keys';
import { inject as service } from '@ember/service';

const { log } = console;// eslint-disable-line no-unused-vars

const leadingSpaces = /^[\s\uFEFF\xA0]+/;

const dropFocus = () => {
  const el = document.querySelector('.pill-meta input');
  if (el && el === document.activeElement) {
    el.blur();
  }
};

const AFTER_OPTIONS_COMPONENT = 'query-container/power-select-after-options';

export default Component.extend({
  classNameBindings: ['isExpanded', ':pill-meta'],

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
   * The option that is currently selected
   * @type {Object}
   * @public
   */
  selection: null,

  /**
   * List of meta for selection
   * @type {Object}
   * @public
   */
  metaOptions: null,

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

  i18n: service(),

  @computed('isActive', 'metaOptions')
  isActiveWithOptions: (isActive, metaOptions) => isActive && metaOptions.length > 0,

  @computed('isFirstPill', 'i18n.locale')
  placeholder(isFirstPill) {
    const i18n = this.get('i18n');
    return isFirstPill ? i18n.t('queryBuilder.placeholder') : '';
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
      [MESSAGE_TYPES.CREATE_FREE_FORM_PILL]: () => this._createFreeFormPill(),
      [MESSAGE_TYPES.HIGHLIGHTED_AFTER_OPTION]: (d) => this.set('_highlightedAfterOption', d)
    });
  },

  didUpdateAttrs() {
    this._super(...arguments);
    if (this.get('isActive')) {
      if (this.get('isAutoFocused')) {
        // We schedule this after render to give time for the power-select to
        // be rendered before trying to focus on it.
        scheduleOnce('afterRender', this, '_focusOnPowerSelectTrigger');
      }
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
      this._broadcast(MESSAGE_TYPES.META_SELECTED, selection);
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
      }
      powerSelectAPI.actions.open();
    },

    /**
     * This function is called on every `input` event from the power-select's
     * trigger element. It's looking for an input string that ends with a space.
     * If it finds one and the power-select has been down-selected to one
     * result, or the full `metaName` has been typed, then trigger a `select`
     * event on the power-select.
     * If the input string is empty, it resets the `selection`. We do this to
     * prevent the previously highlighted item from staying highlighted.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      const isSpace = input.slice(-1) === ' ';
      const { results } = powerSelectAPI;
      if (isSpace && results.length === 1) {
        this._broadcast(MESSAGE_TYPES.META_SELECTED, results[0]);
      } else if (isSpace && results.length > 1) {
        const match = this._hasExactMatch(input.trim(), results);
        if (match) {
          this._broadcast(MESSAGE_TYPES.META_SELECTED, match);
        }
      } else if (input.length === 0) {
        this.set('selection', null);
        this._broadcast(MESSAGE_TYPES.META_SELECTED, null);
        // Set the power-select highlight on the next runloop so that the
        // power-select has time to render the full list of options.
        next(this, () => powerSelectAPI.actions.highlight(null));
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
      // if the key pressed is an escape, then bubble that out and
      // escape further processing
      if (isEscape(event)) {
        // Close dropdown
        powerSelectAPI.actions.close();
        // If we have focus, drop it like it's hot, drop it like it's hot.
        dropFocus();
        // Let others know ECS was pressed
        this._broadcast(MESSAGE_TYPES.META_ESCAPE_KEY);
      } else if (isEnter(event)) {
        const { selected } = powerSelectAPI;
        const selection = this.get('selection');
        const _highlightedAfterOption = this.get('_highlightedAfterOption');
        if (_highlightedAfterOption === 'Free-Form Filter') {
          // If the user presses ENTER while the "after option" is set, the
          // assumption is that they want to create a free-form filter. We check
          // this first because it's possible to have a `selection` and a
          // `selected` that matches the `if` case below because this code runs
          // before power-select reacts to the key press.
          // We'll also perform some cleanup.
          this._createFreeFormPill();
          powerSelectAPI.actions.search('');
          this.set('_highlightedAfterOption', null);
        } else if (selection && selected && selection === selected) {
          // If the user presses ENTER, selecting a meta that was already
          // selected, power-select does nothing. We want the focus to move onto
          // the pill operator.
          this._broadcast(MESSAGE_TYPES.META_SELECTED, selection);
        } else {
          dropFocus();
          next(this, () => {
            // We need to run this check in the next runloop so EPS has time to
            // react to the ENTER press in the first place. For example, to
            // make a selection.
            const selection = this.get('selection');
            const { value } = event.target;
            if (selection === null && !value) {
              this._broadcast(MESSAGE_TYPES.META_ENTER_KEY);
            }
          });
        }
      } else if (isArrowRight(event)) {
        const { selected } = powerSelectAPI;
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
      }
    },

    onOptionMouseDown() {
      // An option mouse down is the precursor to a click
      // of a power select option which causes a focus out
      // of this component. It isn't really a focusOut of this
      // component because control returns to it, so set
      // a flag to make sure the next focus out doesn't escape.
      this.set('swallowNextFocusOut', true);
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

  _createFreeFormPill() {
    // get input text
    const el = this.element.querySelector('.ember-power-select-typeahead-input');
    const { value } = el;
    // cleanup (close dropdown, etc)
    el.value = '';
    this._focusOnPowerSelectTrigger();
    // send value up to create a complex pill
    if (value && value.length > 0) {
      // _debugContainerKey is a private Ember property that returns the full
      // component name (component:query-container/pill-meta).
      const [ , source ] = this._debugContainerKey.split('/');
      this._broadcast(MESSAGE_TYPES.CREATE_FREE_FORM_PILL, [value, source]);
    }
  },

  _focusOnPowerSelectTrigger() {
    const trigger = this.element.querySelector('.ember-power-select-trigger input');
    if (trigger) {
      trigger.focus();
    }
  },

  /**
   * Looks for a meta object with an exact match based on `metaName`.
   * @param {string} text The text to look for
   * @param {Object[]} metas An array of meta objects
   * @return {Object|undefined} The matching object or undefined
   * @private
   */
  _hasExactMatch: (text, metas = []) => metas.find((m) => m.metaName === text),

  /**
   * Function that power-select uses to determine which item in the list of
   * options to highlight.
   * @param {Object} powerSelectAPI The power select public API
   * @private
   */
  _highlighter(powerSelectAPI) {
    const { options, results } = powerSelectAPI;
    return options.length !== results.length ? results[0] : null;
  },

  /**
   * Function that power-select uses to make an autosuggest match. This function
   * looks at the meta's `metaName` and `displayName` properties for a match.
   * If it finds a match anywhere within those two strings, it's considered a
   * match.
   * @param {Object} meta A meta object
   * @param {string} input The search string
   * @return {number} The index of the string match
   * @private
   */
  _matcher: (meta, input) => {
    const _input = input.toLowerCase().replace(leadingSpaces, '');
    const _metaName = meta.metaName.toLowerCase();
    const _displayName = meta.displayName.toLowerCase();
    return _metaName.indexOf(_input) & _displayName.indexOf(_input);
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
  }
});