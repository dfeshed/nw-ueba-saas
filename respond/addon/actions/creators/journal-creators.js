import { journal } from '../api';
import * as ACTION_TYPES from '../types';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * Action creator for creating a journal entry on an incident
 * @method createJournalEntry
 * @public
 * @param journalEntry An {Object} containing the incidentId, notes, milestone
 * @param callbacks { onSuccess, onFailure }
 * @returns {Object}
 */
const createJournalEntry = (journalEntry, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.CREATE_JOURNAL_ENTRY,
    promise: journal.createEntry(journalEntry),
    meta: {
      onSuccess: (response) => {
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        callbacks.onFailure(response);
      }
    }
  };
};

/**
 * Action creator for deleting a journal entry attached to an incident
 * @method deleteJournalEntry
 * @public
 * @param incidentId
 * @param journalId
 * @param callbacks { onSuccess, onFailure }
 * @returns {Object}
 */
const deleteJournalEntry = (incidentId, journalId, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.DELETE_JOURNAL_ENTRY,
    promise: journal.deleteEntry(incidentId, journalId),
    meta: {
      onSuccess: (response) => {
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        callbacks.onFailure(response);
      }
    }
  };
};


/**
 * Action creator for deleting a journal entry attached to an incident
 * @method updateJournalEntry
 * @public
 * @param incidentId
 * @param journalId
 * @param journalMap {object}
 * @param callbacks { onSuccess, onFailure }
 * @returns {Object}
 */
const updateJournalEntry = (incidentId, journalId, journalMap, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.UPDATE_JOURNAL_ENTRY,
    promise: journal.updateEntry(incidentId, journalId, journalMap),
    meta: {
      onSuccess: (response) => {
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        callbacks.onFailure(response);
      }
    }
  };
};

export {
  createJournalEntry,
  deleteJournalEntry,
  updateJournalEntry
};