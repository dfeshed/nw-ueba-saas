import * as ACTION_TYPES from './types';
import { selectedPills, focusedPill, pillsData } from 'investigate-events/reducers/investigate/query-node/selectors';
import { languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import validateQueryFragment from './fetch/query-validation';
import { selectPillsFromPosition } from 'investigate-events/actions/utils';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';
import { ValidatableFilter } from 'investigate-events/util/filter-types';
import { COMPLEX_FILTER, TEXT_FILTER } from 'investigate-events/constants/pill';
import { lookup } from 'ember-dependency-lookup';
import RSVP from 'rsvp';

const { log } = console; // eslint-disable-line no-unused-vars

// Check if a given set a pills contains any orphaned
// twins. If it does, return the matching twins from state
const _findMissingTwins = (pills, pillsFromState) => {
  // filter to those that are missing twins in the input
  const twins = pills.filter((pD) => {
    if (pD.twinId) {
      const twinPresent = pills
        // filter out the pill being processed as it'll
        // obviously have a matching twin id
        .filter((p) => p.id !== pD.id)
        // find twin
        .some((potentialTwinPill) => {
          return pD.twinId === potentialTwinPill.twinId;
        });
      return !twinPresent;
    }
    return false;
  }).map((twinsie) => {
    // now find the twins
    return pillsFromState.find((pill) => {
      // want a matching twin id, but not the exact same pill
      return pill.twinId === twinsie.twinId && pill.id !== twinsie.id;
    });
  });

  return twins;
};

/**
 * Client side validation. Parser handles most validation, but if the pill has
 * not been through the parser already, do so.
 * 1. If the pill returned is a normal query pill & is not marked invalid,
 *    send for server side validation.
 * 2. If the pill is complex, send for server side validation.
 * 3. If the pill was marked invalid by the parser, no action is needed. The
 *    invalid pill is already in state with the correct error message.
 * @private
 */
const _clientSideValidation = ({ pillData, position, isFromParser = false }) => {
  return (dispatch, getState) => {
    const { type } = pillData;
    if (!isFromParser && type !== COMPLEX_FILTER) {
      // If not from parser, no validation has been performed yet. Re-get pillData
      // by putting through parser to do client side validation.
      const { language, aliases } = languageAndAliasesForParser(getState());
      const { meta, operator, value } = pillData;
      const pills = transformTextToPillData(`${meta || ''} ${operator || ''} ${value || ''}`.trim(), { language, aliases, returnMany: true });
      if (pills.length > 1) {
        const i18n = lookup('service:i18n');
        pillData.isInvalid = true;
        pillData.validationError = i18n.t('queryBuilder.validationMessages.tooManyPills');
      } else {
        pillData = pills[0];
      }
    }

    const { isInvalid } = pillData;
    if (isInvalid && !isFromParser && type !== COMPLEX_FILTER) {
      // If the pill is marked invalid but was not from the parser, dispatch an action now to mark it invalid.
      // Otherwise, it was already marked invalid.
      dispatch({
        type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
        promise: RSVP.Promise.reject({ meta: pillData.validationError }),
        meta: {
          position, // position is needed to update pill in reducer
          isServerSide: false // sets `isValidationInProgress = true` while the req is being processed
        }
      });
    } else if (!isInvalid || type === COMPLEX_FILTER) {
      // This catches complex pills, and pills that passed client side validation.
      // We still want to perform server side validation on those.
      dispatch(_serverSideValidation(pillData, position));
    }
    // The only pills left once we get here are invalid pills that were invalid
    // before being sent to this method. Those are already marked invalid in
    // state, so no work needs to be done.
  };
};

export const _serverSideValidation = (pillData, position) => {
  return (dispatch, getState) => {
    const { meta, operator, value, complexFilterText } = pillData;
    let stringifiedPill;
    // create stringified pill data, or just use the what was entered for
    // complex filters
    if (meta && operator) {
      stringifiedPill = `${meta || ''} ${operator || ''} ${value || ''}`.trim();
    } else {
      stringifiedPill = complexFilterText;
    }
    // encode the string and pull out the service id
    const encodedPill = encodeURIComponent(stringifiedPill);
    const { serviceId } = getState().investigate.queryNode;
    dispatch({
      type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
      promise: validateQueryFragment(serviceId, encodedPill),
      meta: {
        position, // position is needed to update pill in reducer
        isServerSide: true // sets `isValidationInProgress = false` after the req was processed
      }
    });
  };
};

export const addGuidedPill = ({ pillData, position, shouldAddFocusToNewPill }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ADD_PILL,
      payload: {
        pillData,
        position,
        shouldAddFocusToNewPill
      }
    });
    dispatch(_clientSideValidation({ pillData, position }));
  };
};

export const batchAddPills = ({ pillsData, initialPosition }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.BATCH_ADD_PILLS,
      payload: {
        pillsData,
        initialPosition
      }
    });
    pillsData.forEach((pillData, i) => {
      // Any pill that extends from the `ValidatableFilter` class will need to
      // be validated. Excludes pill types like text, open/close paren, etc.
      if (pillData instanceof ValidatableFilter) {
        dispatch(_clientSideValidation({
          pillData,
          position: initialPosition + i,
          isFromParser: true
        }));
      }
    });
  };
};

export const editGuidedPill = ({ pillData, position }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.EDIT_GUIDED_PILL,
      payload: {
        pillData
      }
    });
    // Don't validate Text filters
    if (pillData.type !== TEXT_FILTER) {
      dispatch(_clientSideValidation({ pillData, position }));
    }
  };
};

export const deleteGuidedPill = ({ pillData }) => {
  return (dispatch, getState) => {
    const missingTwins = _findMissingTwins(pillData, pillsData(getState()));

    if (missingTwins.length > 0) {
      pillData = [...pillData, ...missingTwins];
    }

    dispatch({
      type: ACTION_TYPES.DELETE_GUIDED_PILLS,
      payload: {
        pillData
      }
    });
    dispatch(deselectAllGuidedPills());
  };
};

export const deleteAllGuidedPills = () => {
  return (dispatch, getState) => {
    const { investigate: { queryNode: { pillsData } } } = getState();
    dispatch(deleteGuidedPill({ pillData: pillsData }));
  };
};

// can come from right-click action
// can come from delete pressed on focused pill
export const deleteSelectedGuidedPills = (pillData) => {
  // keyPress delete
  return (dispatch, getState) => {

    // if no pill is sent, it's a right click action - delete all selected
    // or if a focused pill is passed that is also selected - delete all selected
    if (!pillData || (pillData && pillData.isSelected)) {
      const selectedPD = selectedPills(getState());
      if (selectedPD.length > 0) {
        dispatch({
          type: ACTION_TYPES.DELETE_GUIDED_PILLS,
          payload: {
            pillData: selectedPD
          }
        });
      }
    } else {
      dispatch(deleteGuidedPill({ pillData: [pillData] }));
    }
  };
};

export const addPillFocus = (position) => {
  return (dispatch) => {
    dispatch(removePillFocus());
    dispatch({
      type: ACTION_TYPES.ADD_PILL_FOCUS,
      payload: { position }
    });
  };
};

export const removePillFocus = () => {
  return (dispatch, getState) => {
    const pillData = focusedPill(getState());
    if (pillData) {
      dispatch({
        type: ACTION_TYPES.REMOVE_FOCUS_GUIDED_PILL,
        payload: { pillData }
      });
    }
  };
};

export const deselectAllGuidedPills = () => {
  return (dispatch, getState) => {
    const pillData = selectedPills(getState());
    if (pillData.length > 0) {
      dispatch(deselectGuidedPills({ pillData }, true));
    }
  };
};

const _pillSelectDeselect = (actionType, pillData, shouldIgnoreFocus = false) => {
  return (dispatch, getState) => {

    // now locate potential twins
    const missingTwins = _findMissingTwins(pillData, pillsData(getState()));
    if (missingTwins.length > 0) {
      dispatch({
        type: actionType,
        payload: {
          pillData: missingTwins,
          // twins don't get focus, not yet at least
          shouldIgnoreFocus: true
        }
      });
    }

    // handle the ones being passed in
    dispatch({
      type: actionType,
      payload: {
        pillData,
        shouldIgnoreFocus
      }
    });
  };
};

export const deselectGuidedPills = ({ pillData }, shouldIgnoreFocus = false) => {
  return (dispatch) => {
    dispatch(_pillSelectDeselect(ACTION_TYPES.DESELECT_GUIDED_PILLS, pillData, shouldIgnoreFocus));
  };
};

export const selectGuidedPills = ({ pillData }, shouldIgnoreFocus = false) => {
  return (dispatch) => {
    dispatch(_pillSelectDeselect(ACTION_TYPES.SELECT_GUIDED_PILLS, pillData, shouldIgnoreFocus));
  };
};

export const selectAllPillsTowardsDirection = (position, direction) => {
  return (dispatch, getState) => {
    const { investigate: { queryNode: { pillsData } } } = getState();
    const pillsToBeSelected = selectPillsFromPosition(pillsData, position, direction);
    dispatch(selectGuidedPills({ pillData: pillsToBeSelected }, true));
  };
};

export const openGuidedPillForEdit = ({ pillData }) => ({
  type: ACTION_TYPES.OPEN_GUIDED_PILL_FOR_EDIT,
  payload: {
    pillData
  }
});

export const addFreeFormFilter = ({ pillData, position = 0, shouldAddFocusToNewPill, fromFreeFormMode = false }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ADD_PILL,
      payload: {
        pillData,
        position,
        shouldAddFocusToNewPill,
        fromFreeFormMode
      }
    });
    dispatch(_clientSideValidation({ pillData, position }));
  };
};

export const addTextFilter = ({ pillData, position = 0, shouldAddFocusToNewPill }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ADD_PILL,
      payload: {
        pillData,
        position,
        shouldAddFocusToNewPill
      }
    });
  };
};

// Transform the text to what it would look like in pill form
export const updatedFreeFormText = (freeFormText) => {
  return (dispatch, getState) => {
    const { language, aliases } = languageAndAliasesForParser(getState());
    const pillData = transformTextToPillData(freeFormText, { language, aliases });
    dispatch({
      type: ACTION_TYPES.UPDATE_FREE_FORM_TEXT,
      payload: {
        pillData
      }
    });
  };
};

export const resetGuidedPill = (pillData) => ({
  type: ACTION_TYPES.RESET_GUIDED_PILL,
  payload: {
    pillData
  }
});

export const addParens = ({ position }) => ({
  type: ACTION_TYPES.INSERT_PARENS,
  payload: {
    position
  }
});

export const addIntraParens = ({ position }) => ({
  type: ACTION_TYPES.INSERT_INTRA_PARENS,
  payload: {
    position
  }
});