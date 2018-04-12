import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { warn } from '@ember/debug';

const { log } = console;
const _debug = (data) => log('pills', data);

export default Component.extend({
  classNames: ['query-pills'],

  filters: [],

  hasFocus: false,
  pillIDs: [],

  click() {
    this.set('hasFocus', true);
  },

  actions: {
    /**
     * Handler for all messages coming from pills.
     * @param {string} id The unique id of the pill sending the message
     * @param {string} type The event type from `message-types`
     * @param {Object} data The event data
     * @public
     */
    handleMessage(id, type, data) {
      switch (type) {
        case MESSAGE_TYPES.PILL_INITIALIZED:
          this._addPill(id);
          break;
        case MESSAGE_TYPES.DEBUG:
          _debug(data);
          break;
        default:
          // The buck stops here
          warn(`An unhandled query pill message of type "${type}" has occured \
            from an element with the id "${id}".`);
      }
    }
  },

  // ************************************************************************ //
  //                          PRIVATE FUNCTIONS                               //
  // ************************************************************************ //
  /**
   * Add pill ID to `pillIDs` tracking array.
   * @param {string} id Unique id of pill
   * @private
   */
  _addPill(id) {
    this.get('pillIDs').push(id);
  }
});
