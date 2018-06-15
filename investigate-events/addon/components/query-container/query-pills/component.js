import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { warn } from '@ember/debug';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import { enrichedPillsData } from 'investigate-events/reducers/investigate/next-gen/selectors';
import {
  addNextGenPill,
  deleteNextGenPill,
  editNextGenPill
} from 'investigate-events/actions/next-gen-creators';

const { log } = console;
const _debug = (data) => log('pills', data);

const stateToComputed = (state) => ({
  pillsData: enrichedPillsData(state)
});

const dispatchToActions = {
  addNextGenPill,
  deleteNextGenPill,
  editNextGenPill
};

const QueryPills = Component.extend({
  classNames: ['query-pills'],

  classNameBindings: [
    'isPillOpen:pill-open',
    'isPillOpenForEdit:pill-open-for-edit',
    'isPillTriggerOpenForAdd:pill-trigger-open-for-add'
  ],

  // Is a pill rendered by this component open for any reason?
  isPillOpen: false,

  // Is a pill rendered by this component open for edit?
  isPillOpenForEdit: false,

  // Is a pill trigger open for add?
  isPillTriggerOpenForAdd: false,

  @computed('pillsData')
  newPillPosition: (pillsData) => pillsData.length,

  @computed('pillsData')
  legacyPlainPillsData(pillsData) {
    return pillsData.asMutable().map((pillData) => {
      return {
        meta: pillData.meta.metaName,
        operator: pillData.operator.displayName,
        value: pillData.value,
        id: pillData.id
      };
    });
  },

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
        case MESSAGE_TYPES.PILL_CANCELLED:
          this._pillCancelled(data, position);
          break;
        case MESSAGE_TYPES.PILL_CREATED:
          this._pillCreated(data, position);
          break;
        case MESSAGE_TYPES.PILL_DELETED:
          this._pillDeleted(data);
          break;
        case MESSAGE_TYPES.PILL_EDITED:
          this._pillEdited(data);
          break;
        case MESSAGE_TYPES.PILL_ENTERED:
          this._pillEntered(data, position);
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
   * Tracks that a pill is currently being created/edited or otherwise open
   * and focused.
   *
   * We can tell if a pill entered...
   * - is an existing pill being edited if the data in the event has an id
   * - is the end-of-the-list new pill template if the position
   *   matches the position given to the end-of-the-list component
   * - is from an in-between pill trigger if it is neither of the
   *   two conditions above
   *
   * @private
   */
  _pillEntered(data, position) {
    const isEdit = !!(data && data.id);
    const isEndOfListPillTemplate = this.get('newPillPosition') === position;
    const isMiddleOfListPillTrigger = !isEndOfListPillTemplate && !isEdit;

    this.set('isPillOpen', true);
    this.set('isPillOpenForEdit', isEdit);
    this.set('isPillTriggerOpenForAdd', isMiddleOfListPillTrigger);
  },

  _pillCancelled() {
    this._pillsExited();
  },

  /**
   * Adjusts flags to indicate that no pills are currently open/focused
   * for edit/add
   * @private
   */
  _pillsExited() {
    this.set('isPillOpen', false);
    this.set('isPillOpenForEdit', false);
    this.set('isPillTriggerOpenForAdd', false);
  },

  /**
   * Adds pill to state
   * @param {*} pillData The data for the pill
   * @param {*} position The position of the pill in the array
   * @private
   */
  _pillCreated(pillData, position) {
    // LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING
    // Take current pills, add new one, mark that they are 'saved'
    const pillsData = [ ...this.get('legacyPlainPillsData'), pillData ]
      .map((d) => {
        return { ...d, saved: true };
      });
    this.set('filters', pillsData);
    // END LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING

    this._pillsExited();
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
    const pillsData = this.get('legacyPlainPillsData').filter((pD) => pD.id !== pillData.id);
    this.set('filters', pillsData);
    // END LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING

    this.send('deleteNextGenPill', { pillData });
  },

  /**
   *
   * TODO: WHEN EDIT IS INTRODUCED, ALL OF THIS FUNCTION
   * NEEDS TESTING. ALSO NEED TO TEST THAT ENTERING A PILL
   * FOR EDIT SENDS AN ENTERED EVENT UP TO THIS COMPONENT
   * AND TRIGGERS ADDITION OF THE RIGHT CLASSES
   *
   * Edit pill in state
   * @param {*} pillData The data for the pill
   * @private
   */
  _pillEdited(pillData) {
    // LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING
    // Take current pills, add new one, mark that they are 'saved'
    const pillsData = this.get('legacyPlainPillsData');
    const position = pillsData.map((pD) => pD.id).indexOf(pillData.id);
    const newPillsData = [
      ...pillsData.slice(0, position),
      { ...pillData },
      ...pillsData.slice(position + 1)
    ];
    this.set('filters', newPillsData);
    // END LEGACY FILTERS SET TO KEEP NEAR-TERM SEARCH WORKING

    this._pillsExited();
    this.send('editNextGenPill', { pillData });
  }

});

export default connect(stateToComputed, dispatchToActions)(QueryPills);