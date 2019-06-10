import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { next } from '@ember/runloop';
import { hasComplexText } from 'investigate-events/util/query-parsing';

const { log } = console; // eslint-disable-line no-unused-vars

// index values for Advanced Options
const FREE_FORM_INDEX = 0;
const TEXT_INDEX = 1;

export default Component.extend({
  tagName: 'ul',
  classNameBindings: [':ember-power-select-after-options'],
  attributeBindings: ['role'],
  role: 'listbox',

  /**
   * What is the currently selected tab
   */
  activePillTab: undefined,

  canPerformTextSearch: true,

  hasTextPill: false,

  /**
   * Is this component being used by the pill value component? Why do we care?
   * Because meta/operator behave differently than value as value never
   * down-selects to zero options. This forces us to handle updated attributes
   * differently.
   * @see `didReceiveAttrs()`.
   */
  isPillValue: false,

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
    const { resultsCount, searchText } = this.get('select');
    // Since this is a power-select scoped component, the API that's passed to
    // power-select sub-components (named `select` in this case), get's a new
    // API every time power-select updates itself. For our concerns, this
    // happens when the list get's down-selected as the user types.
    // Any messages that are broadcast from this component are done on the next
    // runloop because this API update happens before power-select visually
    // reacts to the change. If we didn't delay our intent, it would get
    // overwritten by power-selects default behavior.
    if (this.get('isPillValue')) {
      // Pill Value will auto-select between Query Filter and Free-Form Filter.
      // Text Filter is never auto-selected, so we don't consider it like we do
      // for meta/operator.
      if (this._prevSearchText !== searchText) {
        if (hasComplexText(searchText)) {
          next(this, this._broadcast, MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT, FREE_FORM_INDEX);
        } else {
          next(this, this._broadcast, MESSAGE_TYPES.AFTER_OPTIONS_REMOVE_HIGHLIGHT);
        }
      }
    } else {
      // Pill Meta/Operator will auto-select between Free-Form Filter and
      // Text Filter. If all options were filtered out, make a smart guess about
      // which Advanced Option to highlight.
      if (this._prevSearchText !== searchText) {
        if (resultsCount === 0) {
          // All options filterd out. If text is complex or a text filter was
          // previously created, choose free-form, otherwise default to text.
          if (hasComplexText(searchText) || !this.get('canPerformTextSearch') || this.get('hasTextPill')) {
            next(this, this._broadcast, MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT, FREE_FORM_INDEX);
          } else {
            next(this, this._broadcast, MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT, TEXT_INDEX);
          }
        } else {
          // Something was highlighted in main list, so remove any highlighting of
          // these options
          next(this, this._broadcast, MESSAGE_TYPES.AFTER_OPTIONS_REMOVE_HIGHLIGHT);
        }
      }
    }
    this._prevSearchText = searchText;
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
