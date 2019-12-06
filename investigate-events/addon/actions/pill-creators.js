import { lookup } from 'ember-dependency-lookup';
import RSVP from 'rsvp';

import * as ACTION_TYPES from './types';
import { selectedPills, focusedPill, pillsData } from 'investigate-events/reducers/investigate/query-node/selectors';
import { languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import validateQueryFragment from './fetch/query-validation';
import {
  findEmptyParensAtPosition,
  findSelectedPills,
  findMissingTwins,
  isLogicalOperator
} from 'investigate-events/actions/pill-utils';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';
import { ValidatableFilter } from 'investigate-events/util/filter-types';
import {
  CLOSE_PAREN,
  COMPLEX_FILTER,
  TEXT_FILTER,
  OPERATOR_AND,
  OPERATOR_OR
} from 'investigate-events/constants/pill';

const { log } = console; // eslint-disable-line no-unused-vars

const _isCloseParen = (pill) => pill?.type === CLOSE_PAREN;

const _getAdjacentDeletableLogicalOperatorAt = (pills, idx) => {
  // P & _ P      not deletable
  // P & _ (      not deletable
  // ( P & _ )    deletable
  // P & _        deletable
  const prevPill = pills[idx - 1];
  const nextPill = pills[idx];
  if (nextPill) {
    // If there's a pill to the right, then we need it to be a close paren or another logical operator.
    // Otherwise we're deleting operators that should exist.
    return isLogicalOperator(prevPill) && (_isCloseParen(nextPill) || isLogicalOperator(nextPill)) ? prevPill : undefined;
  } else {
    return isLogicalOperator(prevPill) ? prevPill : undefined;
  }
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

export const addGuidedPill = ({ pillData, position, shouldAddFocusToNewPill = false }) => {
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

export const cancelPillCreation = (position) => {
  return (dispatch, getState) => {
    const { investigate: { queryNode: { pillsData } } } = getState();
    const emptyParens = findEmptyParensAtPosition(pillsData, position);
    const adjacentOperator = _getAdjacentDeletableLogicalOperatorAt(pillsData, position);
    let pillData;
    if (emptyParens.length > 0) {
      pillData = emptyParens;
    } else if (adjacentOperator) {
      pillData = [adjacentOperator];
    }
    if (pillData) {
      dispatch({
        type: ACTION_TYPES.DELETE_GUIDED_PILLS,
        payload: { pillData }
      });
    }
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
    const missingTwins = findMissingTwins(pillData, pillsData(getState()));
    if (missingTwins.length > 0) {
      pillData = [...pillData, ...missingTwins];
    }
    // Delete passed in pills
    dispatch({
      type: ACTION_TYPES.DELETE_GUIDED_PILLS,
      payload: {
        pillData
      }
    });
  };
};

export const deleteAllGuidedPills = () => {
  return (dispatch, getState) => {
    const { investigate: { queryNode: { pillsData } } } = getState();
    dispatch(deleteGuidedPill({ pillData: pillsData }));
  };
};

/**
 * Can come from right-click action -> delete selected pills/parens + paren contents
 * Can come from delete pressed on a selected, focused pill (isKeyPress = true) -> delete selected paren/pills (not their contents)
 */
export const deleteSelectedGuidedPills = (pillData, isKeyPress = false) => {
  return (dispatch, getState) => {
    if (!pillData || pillData.isSelected) {
      const { investigate: { queryNode: { pillsData } } } = getState();
      const selectedPD = isKeyPress ? selectedPills(getState()) : findSelectedPills(pillsData);
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

const _removePillFocus = (getState) => {
  const pillData = focusedPill(getState());
  if (pillData) {
    return {
      type: ACTION_TYPES.REMOVE_FOCUS_GUIDED_PILL,
      payload: { pillData }
    };
  }
};

export const removePillFocus = () => {
  return (dispatch, getState) => {
    dispatch(_removePillFocus(getState));
  };
};

export const openGuidedPillForEdit = ({ pillData }) => ({
  type: ACTION_TYPES.OPEN_GUIDED_PILL_FOR_EDIT,
  payload: {
    pillData
  }
});

export const addFreeFormFilter = ({ pillData, position = 0, shouldAddFocusToNewPill = false, fromFreeFormMode = false }) => {
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

export const addTextFilter = ({ pillData, position = 0, shouldAddFocusToNewPill = false }) => {
  return (dispatch, getState) => {
    const batch = [];
    batch.push({
      type: ACTION_TYPES.ADD_PILL,
      payload: {
        pillData,
        position,
        shouldAddFocusToNewPill
      }
    });
    const prevPill = pillsData(getState())[position - 1];
    if (prevPill?.type === OPERATOR_OR) {
      // Text pills must always have an AND next to them, not an OR
      const newPillData = {
        ...prevPill,
        type: OPERATOR_AND
      };
      batch.push(replaceLogicalOperator({ pillData: newPillData, position }));
    }
    dispatch(batch);
  };
};

// Transform the text to what it would look like in pill form
export const updatedFreeFormText = (freeFormText) => {
  return {
    type: ACTION_TYPES.UPDATE_FREE_FORM_TEXT,
    payload: {
      freeFormText
    }
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

export const replaceAllGuidedPills = (pillData) => ({
  type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
  payload: {
    pillData
  }
});

export const addLogicalOperator = ({ pillData, position }) => {
  return (dispatch, getState) => {
    const prevPill = pillsData(getState())[position - 1];
    // If the first pill is a text pill, any operator after must be an AND
    if ((position - 1) === 0 && prevPill?.type === TEXT_FILTER && pillData.type === OPERATOR_OR) {
      pillData.type = OPERATOR_AND;
      // TODO: Notify the user why this happened
    }
    const action = {
      type: ACTION_TYPES.INSERT_LOGICAL_OPERATOR,
      payload: {
        pillData,
        position
      }
    };
    dispatch(action);
  };
};

export const replaceLogicalOperator = ({ pillData, position }) => ({
  type: ACTION_TYPES.REPLACE_LOGICAL_OPERATOR,
  payload: {
    pillData,
    position
  }
});

export const focusAndToggleLogicalOperator = ({ pillData, position }) => {
  return (dispatch, getState) => {
    const batchActions = [];

    const removeFocusAction = _removePillFocus(getState);
    if (removeFocusAction) {
      batchActions.push(removeFocusAction);
    }

    const newPillData = {
      ...pillData,
      isFocused: true,
      type: pillData.type === OPERATOR_AND ? OPERATOR_OR : OPERATOR_AND
    };
    const replaceAction = replaceLogicalOperator({ pillData: newPillData, position: position + 1 });
    batchActions.push(replaceAction);

    dispatch(batchActions);
  };
};

export const wrapWithParens = ({ startIndex, endIndex }) => ({
  type: ACTION_TYPES.WRAP_WITH_PARENS,
  payload: {
    startIndex,
    endIndex
  }
});

export const unstashPills = () => {
  return (dispatch, getState) => {
    const { investigate: { queryNode: { isPillsDataStashed } } } = getState();
    if (isPillsDataStashed) {
      dispatch({ type: ACTION_TYPES.UNSTASH_PILLS });
    }
  };
};