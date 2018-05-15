import Component from '@ember/component';
import { scheduleOnce } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import * as MESSAGE_TYPES from '../message-types';

// const { log } = console;

const leadingSpaces = /^[\s\uFEFF\xA0]+/;

const stateToComputed = (state) => ({
  options: metaKeySuggestionsForQueryBuilder(state)
});

const PillMeta = Component.extend({
  classNameBindings: ['isActive', 'isExpanded', ':pill-meta'],

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

  @computed('isActive', 'options')
  isActiveWithOptions: (isActive, options) => isActive && options.length > 0,

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
      this._broadcast(MESSAGE_TYPES.META_CLICKED);
    }
  },

  actions: {
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
     * result, then trigger a `select` event on the power-select. Ultimately,
     * this triggers the `onChange` action above. If the input string is empty,
     * it resets the `selection`. We do this to prevent the previously
     * highlighted item from staying highlighted.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      const isSpace = input.slice(-1) === ' ';
      const { results } = powerSelectAPI;
      if (isSpace && results.length === 1) {
        powerSelectAPI.actions.select(results[0]);
      } else if (input.length === 0) {
        this.set('selection', null);
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