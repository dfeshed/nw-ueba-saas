import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import * as MESSAGE_TYPES from '../message-types';

// const { log } = console;

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
      powerSelectAPI.actions.open();
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

  // Function that power-select uses to make an autosuggest match. This function
  // looks at the meta's metaName and displayName properties for a match.
  _matcher: (m, input) => m.metaName.indexOf(input) & m.displayName.indexOf(input)
});

export default connect(stateToComputed)(PillMeta);