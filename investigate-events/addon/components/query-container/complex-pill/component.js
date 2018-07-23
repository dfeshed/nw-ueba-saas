import Component from '@ember/component';
import { later } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  classNames: ['complex-pill'],
  classNameBindings: ['isActive', 'isSelected'],
  tagName: 'span',
  attributeBindings: ['title'],
  i18n: service(),

  pillData: undefined,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  // Not used now, but will be set to true
  // when editing later
  isActive: false,

  // Whether or not a double click has fired
  doubleClickFired: false,

  /**
   * Whether or not this pill is selected
   * @type {boolean}
   * @public
   */
  @computed('pillData')
  isSelected: (pillData) => !!pillData && pillData.isSelected,

  @computed('pillData')
  title(pillData) {
    const notEditable = this.get('i18n').t('queryBuilder.notEditable').string;
    return `${notEditable}\n${pillData.complexFilterText}`;
  },

  init() {
    this._super(arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.DELETE_CLICKED]: (data) => this._deletePill(data)
    });
  },

  click() {
    // If not active, and clicking pill, then process
    // this as a selection event
    if (!this.get('isActive')) {
      this._pillSelected();
    }
  },

  doubleClick() {
    // set flag to prevent previous single-clicks from executing
    this.set('doubleClickFired', true);

    // If not active, and double clicking pill, then process
    // this as a desire to open for edit
    if (!this.get('isActive')) {
      this._pillOpenForEdit();
    }
  },

  _pillOpenForEdit() {
    const pillData = this.get('pillData');
    this._broadcast(MESSAGE_TYPES.PILL_OPEN_FOR_EDIT, pillData);
  },

  _pillSelected() {
    // Waiting 175 milliseconds in order to give
    // a double click a chance to occur. If we do not
    // delay execution here, double clicks will execute
    // click processing twice.
    later(() => {
      if (!this.get('doubleClickFired')) {
        const pillData = this.get('pillData');
        if (pillData.isSelected) {
          this._broadcast(MESSAGE_TYPES.PILL_DESELECTED, pillData);
        } else {
          this._broadcast(MESSAGE_TYPES.PILL_SELECTED, pillData);
        }
      } else {
        // if double click had been fired, reset flag, however
        // decent chance component no longer exists, so make
        // that check
        if (!this.isDestroyed || !this.isDestroying) {
          this.set('doubleClickFired', false);
        }
      }
    }, 175);
  },

  actions: {
    /**
     * Handler for all messages coming from pill components
     * (meta/operator/value).
     * @param {string} type The event type from `event-types`
     * @param {Object} data The event data
     * @public
     */
    handleMessage(type, data) {
      const messageHandlerFn = this.get('_messageHandlerMap')[type];
      if (messageHandlerFn) {
        messageHandlerFn(data);
      } else {
        // Any messages that do not match expected message types get send up
        // to the query-pills component.
        this._broadcast(type, data);
      }
    }
  },

  /**
   * Handles messaging around deleting this pill
   * @private
   */
  _deletePill() {
    this._broadcast(MESSAGE_TYPES.PILL_DELETED, this.get('pillData'));
  },

    /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type, data) {
    this.get('sendMessage')(type, data, this.get('position'));
  }
});