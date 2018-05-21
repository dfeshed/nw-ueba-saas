import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { warn } from '@ember/debug';
import { connect } from 'ember-redux';

import { pillsData } from 'investigate-events/reducers/investigate/next-gen/selectors';
import { addNextGenPill } from 'investigate-events/actions/next-gen-creators';

const { log } = console;
const _debug = (data) => log('pills', data);

const stateToComputed = (state) => ({
  pillsData: pillsData(state)
});

const dispatchToActions = {
  addNextGenPill
};

const QueryPills = Component.extend({
  classNames: ['query-pills'],

  actions: {
    /**
     * Handler for all messages coming from pills.
     * @param {string} type The event type from `message-types`
     * @param {Object} data The event data
     * @param {Object} position The position of the pill being messaged
     * @public
     */
    handleMessage(type, data, position) {
      switch (type) {
        case MESSAGE_TYPES.PILL_CREATED:
          this._pillCreated(data, position);
          break;
        case MESSAGE_TYPES.PILL_INITIALIZED:
          // Do nothing right now
          break;
        case MESSAGE_TYPES.DEBUG:
          _debug(data);
          break;
        default:
          // The buck stops here
          warn(`An unhandled query pill message of type "${type}" has occured \
            from an element with the id "${data.id}".`);
      }
    }
  },

  // ************************************************************************ //
  //                          PRIVATE FUNCTIONS                               //
  // ************************************************************************ //
  /**
   * Adds pill to state
   * @param {*} value The data for the pill
   * @private
   */
  _pillCreated(data, position) {
    // LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING
    // Take current pills, add new one, mark that they are 'saved'
    const pillsData = [ ...this.get('pillsData'), data ]
      .map((d) => {
        return { ...d, saved: true };
      });
    this.set('filters', pillsData);
    // END LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING

    this.send('addNextGenPill', { pillData: data, position });
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryPills);