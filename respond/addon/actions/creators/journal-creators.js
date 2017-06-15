import Ember from 'ember';
import { journal } from '../api';
import * as ACTION_TYPES from '../types';
import * as ErrorHandlers from '../util/error-handlers';

const {
  Logger
} = Ember;

const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * Action creator for creating a journal entry on an incident
 * @method createJournalEntry
 * @public
 * @param journalEntry An {Object} containing the incidentId, author, notes and other properties for creating an entry
 * @returns {Object}
 */
const createJournalEntry = (journalEntry, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.CREATE_JOURNAL_ENTRY,
    promise: journal.createEntry(journalEntry),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.CREATE_JOURNAL_ENTRY, response);
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        ErrorHandlers.handleContentCreationError(response, 'journal entry');
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
 * @returns {Object}
 */
const deleteJournalEntry = (incidentId, journalId) => {
  return {
    type: ACTION_TYPES.DELETE_JOURNAL_ENTRY,
    promise: journal.deleteEntry(incidentId, journalId),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.DELETE_JOURNAL_ENTRY, response),
      onFailure: (response) => ErrorHandlers._handleContentRetrievalError(response, 'journal')
    }
  };
};

export {
  createJournalEntry,
  deleteJournalEntry
};