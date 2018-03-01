import { isPresent } from '@ember/utils';
import { assert } from '@ember/debug';
import { promiseRequest } from 'streaming-data/services/data-access/requests';

export default {
  /**
   * Executes a websocket call to create a journal entry for a particular incident (id)
   *
   * @method createEntry
   * @public
   * @param {incidentId, author, notes } journal entry object must include an incidentId (string), author (string), notes (string)
   * @returns {Promise}
   */
  createEntry({ incidentId, ...journalMap }) {
    const { notes, milestone } = journalMap;

    assert('IncidentId, author and notes properties are required parameters for createEntry()',
      (isPresent(incidentId) && isPresent(notes)));

    return promiseRequest({
      method: 'createRecord',
      modelName: 'journal-entry',
      query: {
        incidentId,
        journalMap,
        milestone
      }
    });
  },

  /**
   * Executes a websocket call to delete a journal entry (by journal entry id)
   * @public
   * @param incidentId
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
  },

  /** Executes a websocket call to update a journal entry
   * @param incidentId
   * @param journalId
   * @param journalMap
   * @returns {*}
   * @public
   */
  updateEntry(incidentId, journalId, journalMap = {}) {
    assert('An incidentId and journalId must be provided', (isPresent(incidentId) && isPresent(journalId)));

    return promiseRequest({
      method: 'updateRecord',
      modelName: 'journal-entry',
      query: {
        incidentId,
        journalId,
        journalMap
      }
    });
  }
};
