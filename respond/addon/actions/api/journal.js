import Ember from 'ember';
import { promiseRequest } from 'streaming-data/services/data-access/requests';

const { assert, isPresent, Object: EmberObject } = Ember;

const JournalAPI = EmberObject.extend({});

JournalAPI.reopenClass({
  /**
   * Executes a websocket call to create a journal entry for a particular incident (id)
   *
   * @method createEntry
   * @public
   * @param {incidentId, author, notes } journal entry object must include an incidentId (string), author (string), notes (string)
   * @returns {Promise}
   */
  createEntry({ incidentId, ...journalMap }) {
    const { author, notes } = journalMap;

    assert('IncidentId, author and notes properties are required parameters for createEntry()',
        (isPresent(incidentId) && isPresent(author) && isPresent(notes)));

    return promiseRequest({
      method: 'createRecord',
      modelName: 'journal-entry',
      query: {
        incidentId,
        journalMap
      }
    });
  },

  /**
   * Executes a websocket call to delete a journal entry (by journal entry id)
   * @public
   * @param journalEntryId
   * @returns {Promise}
   */
  deleteEntry(incidentId, journalId) {
    assert('An incidentId and journalId must be provided', (isPresent(incidentId) && isPresent(journalId)));

    return promiseRequest({
      method: 'deleteRecord',
      modelName: 'journal-entry',
      query: {
        incidentId,
        journalId
      }
    });
  }
});


export default JournalAPI;