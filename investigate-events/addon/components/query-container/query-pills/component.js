import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { warn } from '@ember/debug';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

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

  @computed('pillsData')
  newPillPosition: (pillsData) => pillsData.length,

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
        case MESSAGE_TYPES.PILL_DELETED:
          this._pillDeleted(data);
          break;
        case MESSAGE_TYPES.PILL_EDITED:
          this._pillEdited(data);
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
   * @param {*} pillData The data for the pill
   * @param {*} position The position of the pill in the array
   * @private
   */
  _pillCreated(pillData, position) {
    // LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING
    // Take current pills, add new one, mark that they are 'saved'
    const pillsData = [ ...this.get('pillsData'), pillData ]
      .map((d) => {
        return { ...d, saved: true };
      });
    this.set('filters', pillsData);
    // END LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING

    this.send('addNextGenPill', { pillData, position });
  },

  /**
   * Delete pill from state
   * @param {*} pillData The data for the pill
   * @private
   */
  _pillDeleted(pillData) {
    // LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING
    // Take current pills, add new one, mark that they are 'saved'
    const pillsData = this.get('pillsData').filter((pD) => pD.id !== pillData.id);
    this.set('filters', pillsData);
    // END LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING

    this.send('deleteNextGenPill', { pillData });
  },

  /**
   * Edit pill in state
   * @param {*} pillData The data for the pill
   * @private
   */
  _pillEdited(pillData) {
    // LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING
    // Take current pills, add new one, mark that they are 'saved'
    const pillsData = this.get('pillsData');
    const position = pillsData.map((pD) => pD.id).indexOf(pillData.id);
    const newPillsData = [
      ...pillsData.slice(0, position),
      { ...pillData },
      ...pillsData.slice(position + 1)
    ];
    this.set('filters', newPillsData);
    // END LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING

    this.send('editNextGenPill', { pillData });
  }

});

export default connect(stateToComputed, dispatchToActions)(QueryPills);