import Component from '@ember/component';
import { next, scheduleOnce } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import * as MESSAGE_TYPES from '../message-types';
import { isArrowLeft, isArrowRight, isEnter, isEscape } from 'investigate-events/util/keys';

const { log } = console;// eslint-disable-line no-unused-vars

const leadingSpaces = /^[\s\uFEFF\xA0]+/;

const stateToComputed = (state) => ({
  options: metaKeySuggestionsForQueryBuilder(state)
});

const PillMeta = Component.extend({
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

  @computed('isActive', 'options')
  isActiveWithOptions: (isActive, options) => isActive && options.length > 0,

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
    onOptionMouseDown() {
      // An option mouse down is the precursor to a click
      // of a power select option which causes a focus out
      // of this component. It isn't really a focusOut of this
      // component because control returns to it, so set
      // a flag to make sure the next focus out doesn't escape.
      this.set('swallowNextFocusOut', true);
    },
    onChange(selection /* powerSelectAPI, event */) {
      this._broadcast(MESSAGE_TYPES.META_SELECTED, selection);
    },
    onFocus(powerSelectAPI /* event */) {
      const selection = this.get('selection');
      if (powerSelectAPI.lastSearchedText && !selection) {
        // If we gain focus and `lastSearchText` exists, power-select will use
        // that to down-select the list of options. This can happen if the user
        // enters some text, focuses away, then comes back. What they previously
        // types will effect the list of options.
        powerSelectAPI.actions.search('');
      } else if (selection) {
        // Check to see if the selected option is valid for the power-select
        // options and select it if it is; otherwise clear it out.
        const option = this.get('options').find((d) => d.metaName === selection.metaName);
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
      const { options, results } = powerSelectAPI;
      if (isSpace && results.length === 1) {
        this._broadcast(MESSAGE_TYPES.META_SELECTED, results[0]);
      } else if (isSpace && results.length > 1) {
        const match = this._hasExactMatch(input.trim(), results);
        if (match) {
          this._broadcast(MESSAGE_TYPES.META_SELECTED, match);
        }
      } else if (input.length === 0) {
        this.set('selection', null);
        // Set the power-select highlight on the next runloop so that the
        // power-select has time to render the full list of options.
        next(this, () => powerSelectAPI.actions.highlight(options[0]));
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
        const el = document.querySelector('.pill-meta input');
        if (el && el === document.activeElement) {
          el.blur();
        }
        // Let others know ECS was pressed
        this._broadcast(MESSAGE_TYPES.META_ESCAPE_KEY);
      } else if (isEnter(event)) {
        // If the user presses ENTER, selecting an operator that was already
        // selected, power-select does nothing. We want the focus to move onto
        // the pill value. If nothing has been selected, this is an indication
        // we should execute the query.
        const { selected } = powerSelectAPI;
        const selection = this.get('selection');
        if (selection && selected && selection === selected) {
          this._broadcast(MESSAGE_TYPES.META_SELECTED, selection);
        }
      } else if (isArrowRight(event)) {
        const { selected } = powerSelectAPI;
        // Check if cursor position (selectionStart) is at the end of the
        // string. We use the selected metaName for comparision because we only
        // want to move forward if there's a selection.
        if (selected && event.target.selectionStart === selected.metaName.length) {
          next(this, () => this._broadcast(MESSAGE_TYPES.META_ARROW_RIGHT_KEY));
        }
      } else if (isArrowLeft(event) && event.target.selectionStart === 0) {
        // Move to the left of this pill
        next(this, () => this._broadcast(MESSAGE_TYPES.META_ARROW_LEFT_KEY));
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
  }
});

export default connect(stateToComputed)(PillMeta);