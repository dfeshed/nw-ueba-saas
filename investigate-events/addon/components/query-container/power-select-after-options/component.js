import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { next } from '@ember/runloop';

const { log } = console; // eslint-disable-line no-unused-vars

export default Component.extend({
  tagName: 'ul',
  classNameBindings: [':ember-power-select-after-options'],
  attributeBindings: ['role'],
  role: 'listbox',

  /**
   * What is the currently selected tab
   */
  activePillTab: undefined,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  /**
   * List of options to display
   * @type {Array}
   * @public
   */
  options: [],

  _previouslyHighlightedIndex: null,

  _prevResultCount: null,

  didReceiveAttrs() {
    this._super(...arguments);
    // When creating Free-Form of Text filters, we need to show the full text of
    // the query in the "value" portion of the after-option option. For example,
    // in the case of operator, we should show meta plus whatever's been
    // typed in operator. So something like "action end".
    const activePill = document.querySelector('.query-pill.is-active');
    const text = activePill ? activePill.textContent : '';
    const trimmedText = text.replace(/\s+/g, ' ').trim();
    this.set('fullPillText', trimmedText);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { resultsCount } = this.get('select');
    // Since this is a power-select scoped component, the API that's passed to
    // power-select sub-components (named `select` in this case), get's a new
    // API every time power-select updates itself. For our concerns, this
    // happens when the list get's down-selected as the user types.
    // If the user types something that filters out all options, the
    // `resultsCount` will be 0. This is how we know we should automatically
    // highlight the first item. If the `resultsCount` was more than 0, we send
    // a "remove highlight" message. Both of these are done on the next runloop
    // because this API update happens before power-select visually reacts to
    // the change. If we didn't delay our intent, it would get overwritten by
    // power-selects default behavior.
    if (this._prevResultCount !== resultsCount) {
      if (resultsCount === 0) {
        // No results, auto highlight first item in options list
        next(this, this._broadcast, MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT, 0);
      } else {
        // Something was highlighted in main list, so remove any highlighting of
        // these options
        next(this, this._broadcast, MESSAGE_TYPES.AFTER_OPTIONS_REMOVE_HIGHLIGHT);
      }
      this._prevResultCount = resultsCount;
    }
  },

  actions: {
    onMouseEnter(e) {
      const el = e.target.closest('[data-option-index]');
      if (el) {
        const index = parseInt(el.getAttribute('data-option-index'), 10);
        const prevIndex = this.get('_previouslyHighlightedIndex');
        if (index !== prevIndex) {
          this.set('_previouslyHighlightedIndex', index);
          this._broadcast(MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT, index);
        }
      }
    },
    onMouseLeave() {
      this.set('_previouslyHighlightedIndex', null);
      this._broadcast(MESSAGE_TYPES.AFTER_OPTIONS_REMOVE_HIGHLIGHT);
    },
    onMouseUp(e) {
      this._chooseByElement(e.currentTarget);
    },
    // Not creating a message handler just yet as there is only one
    // type of message coming in.
    handleMessage(type) {
      this._broadcast(type);
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

  _chooseByElement(el) {
    const description = el.querySelector('.description').textContent.trim();
    this._broadcast(MESSAGE_TYPES.AFTER_OPTIONS_SELECTED, description);
    // Clear the search
    const { actions } = this.get('select');
    actions.search('');
  }
});
