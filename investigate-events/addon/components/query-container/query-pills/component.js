import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { warn } from '@ember/debug';

const { log } = console;
const _debug = (data) => log('pills', data);

export default Component.extend({
  classNames: ['query-pills'],

  filters: [],

  /**
   * @private
   */
  _hasFocus: false,

  /**
   * Map of filters that have been converted to pills for display on the UI.
   * @private
   */
  _filterMap: new Map(),

  willDestroyElement() {
    this._super(arguments);
    const map = this.get('_filterMap');
    if (map) {
      map.clear();
    }
  },

  click() {
    this.set('_hasFocus', true);
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
        case MESSAGE_TYPES.PILL_CREATED:
          this._pillCreated(id, data);
          break;
        case MESSAGE_TYPES.PILL_INITIALIZED:
          this._pillInitialized(id);
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
   * Adds pill to `_filters` Map.
   * @param {*} key A unique id
   * @param {*} value The data for the pill
   * @private
   */
  _pillCreated(key, value) {
    const filterMap = this.get('_filterMap');
    filterMap.set(key, value);
    // Set the filters array so that 2-way data binding will pick up the pills
    // and make them available for the route's `executeQuery` action. Also set
    // `saved` to true so `uriEncodeMetaFilters` picks it up.
    const filtersAsArray = new Array();
    filterMap.forEach((d) => filtersAsArray.push({ ...d, saved: true }));
    this.set('filters', filtersAsArray);
  },

  /**
   * Add empty object to `_filters` Map.
   * @param  {*} key A unique id
   * @private
   */
  _pillInitialized(key) {
    const filterMap = this.get('_filterMap');
    filterMap.set(key, null);
  }
});
