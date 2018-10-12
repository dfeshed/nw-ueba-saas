import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';

const { log } = console; // eslint-disable-line no-unused-vars

const MENU_OPTIONS = [
  { label: 'Free Form Filter', disabled: false, highlighted: false }
];

export default Component.extend({
  tagName: 'ul',
  classNameBindings: [':ember-power-select-after-options'],
  attributeBindings: ['role'],
  role: 'listbox',

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
  options: MENU_OPTIONS,

  /**
   * Index of the item that is currently highlighted.
   * @type {Number}
   * @default undefined
   * @private
   */
  _highlightIndex: undefined,

  didUpdateAttrs() {
    const { results } = this.get('select');
    if (results.length === 0) {
      // No results, auto highlight first item in options list
      this._highlightByIndex(0);
    } else if (this.get('_highlightIndex') !== undefined) {
      // Something was highlighted, so clear it out
      this._removeAllHighlights();
    }
  },

  actions: {
    onMouseOver(e) {
      const el = e.target.closest('[data-option-index]');
      if (el) {
        const index = parseInt(el.getAttribute('data-option-index'), 10);
        this._highlightByIndex(index);
      }
    },
    onMouseUp(e) {
      this._chooseByElement(e.currentTarget);
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
    const description = el.querySelector('.description');
    switch (description.textContent.trim()) {
      case MENU_OPTIONS[0].label:
        this._broadcast(MESSAGE_TYPES.CREATE_FREE_FORM_PILL);
        break;
    }
    // Clear the search and remove any highlighting
    const { actions } = this.get('select');
    this._removeAllHighlights();
    actions.search('');
  },

  _highlightByIndex(index) {
    const _highlightIndex = this.get('_highlightIndex');
    if (index === _highlightIndex) {
      return;
    } else {
      const newOptions = MENU_OPTIONS.map((d, i) => {
        return {
          ...d,
          highlighted: i === index
        };
      });
      this.setProperties({
        options: newOptions,
        _highlightIndex: index
      });
      this._broadcast(MESSAGE_TYPES.HIGHLIGHTED_AFTER_OPTION, MENU_OPTIONS[index].label);
    }
  },

  _removeAllHighlights() {
    const newOptions = MENU_OPTIONS.map((d) => {
      return {
        ...d,
        highlighted: false
      };
    });
    this.setProperties({
      options: newOptions,
      _highlightIndex: undefined
    });
    this._broadcast(MESSAGE_TYPES.HIGHLIGHTED_AFTER_OPTION, null);
  }
});
