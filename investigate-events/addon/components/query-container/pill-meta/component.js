import Component from '@ember/component';
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
  classNameBindings: ['isActive', ':pill-meta'],

  isActive: false,
  selection: null,
  sendMessage: () => {},

  actions: {
    onChange(selection /* powerSelectAPI, event */) {
      this._broadcast(MESSAGE_TYPES.META_SELECTED, selection);
    },
    onFocus(powerSelectAPI /* event */) {
      if (powerSelectAPI.lastSearchedText) {
        // When gaining focus, if there was a previous search term, let's clear
        // it out by performing a blank search.
        powerSelectAPI.actions.search('');
      }
      powerSelectAPI.actions.open();
    },
    /**
     * This function is called on every `input` event from the power-select's
     * trigger element. It's looking for an input string that ends with a space.
     * If it finds one and the power-select has been down-selected to one
     * result, then trigger a `select` event on the power-select. Ultimately,
     * this triggers the `onChange` action above.
     * @private
     */
    onInput(input, powerSelectAPI /* event */) {
      const isSpace = input.slice(-1) === ' ';
      const { results } = powerSelectAPI;
      if (isSpace && results.length === 1) {
        powerSelectAPI.actions.select(results[0]);
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

  @computed('isActive', 'options')
  _isActive: (isActive, options) => isActive && options.length > 0,

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