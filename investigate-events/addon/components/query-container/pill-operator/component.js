import Component from '@ember/component';
import { scheduleOnce } from '@ember/runloop';
import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';
import * as MESSAGE_TYPES from '../message-types';

// const { log } = console;

const makeOperatorExpensive = (obj) => ({ ...obj, isExpensive: true });

const eq = { displayName: '=', isExpensive: false, hasValue: true };
const notEq = { displayName: '!=', isExpensive: false, hasValue: true };
const exists = { displayName: 'exists', isExpensive: false, hasValue: false };
const notExists = { displayName: '!exists', isExpensive: false, hasValue: false };
const begins = { displayName: 'begins', isExpensive: false, hasValue: true };
const contains = { displayName: 'contains', isExpensive: true, hasValue: true };
const ends = { displayName: 'ends', isExpensive: true, hasValue: true };

const operatorsForMetaIndexedByKey = [exists, notExists, makeOperatorExpensive(eq), makeOperatorExpensive(notEq)];
const operatorsForMetaIndexedByKeyWithTextFormat = [exists, notExists, makeOperatorExpensive(eq), makeOperatorExpensive(notEq), makeOperatorExpensive(begins), ends, contains];
const operatorsForMetaIndexedByValue = [exists, notExists, eq, notEq ];
const operatorsForMetaIndexedByValueWithTextFormat = [exists, notExists, eq, notEq, begins, ends, contains];
const operatorsForSessionId = [exists, notExists, eq, notEq];
const defaultOperators = [eq, notEq, exists, notExists, contains, begins, ends];

const NONE = 'none';
const KEY = 'key';
const VALUE = 'value';
const indices = [NONE, KEY, VALUE];

export default Component.extend({
  classNameBindings: ['isActive', ':pill-operator', 'selection:has-selection'],

  isActive: false,
  meta: null,
  selection: null,
  sendMessage: () => {},

  @computed('meta')
  options(meta) {
    let options = [];
    if (!isEmpty(meta)) {
      const { format, flags = 1, metaName } = meta;
      const index = flags & '0xF' - 1;
      const indexedBy = indices[index];
      if (indexedBy === KEY) {
        options = (format === 'Text') ?
          operatorsForMetaIndexedByKeyWithTextFormat :
          operatorsForMetaIndexedByKey;
      } else if (indexedBy === VALUE) {
        options = (format === 'Text') ?
          operatorsForMetaIndexedByValueWithTextFormat :
          operatorsForMetaIndexedByValue;
      } else if (metaName === 'sessionid') {
        // sessionid is a special case in the sense that it is the only
        // non-indexed key that has these 4 options because it's a primary key.
        options = operatorsForSessionId;
      }
      options = defaultOperators;
    }
    return options;
  },

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
      this._broadcast(MESSAGE_TYPES.OPERATOR_CLICKED);
    }
  },

  actions: {
    onChange(selection /* powerSelectAPI, event */) {
      this._broadcast(MESSAGE_TYPES.OPERATOR_SELECTED, selection);
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

  _focusOnPowerSelectTrigger() {
    const trigger = this.element.querySelector('.ember-power-select-trigger input');
    if (trigger) {
      trigger.focus();
    }
  },

  // Function that power-select uses to make an autosuggest match. This function
  // looks at the operators's displayName property for a match.
  _matcher: (o, input) => {
    const _displayName = o.displayName.toLowerCase();
    const _input = input.toLowerCase();
    return _displayName.indexOf(_input);
  }
});