import * as ACTION_TYPES from './types';
import { selectedPills, focusedPill, pillsData } from 'investigate-events/reducers/investigate/query-node/selectors';
import {
  findEmptyParensAtPosition,
  findSelectedPills,
  findMissingTwins,
  getAdjacentDeletableLogicalOperatorAt,
  isValidatablePill
} from 'investigate-events/actions/pill-utils';
import {
  TEXT_FILTER,
  OPERATOR_AND,
  OPERATOR_OR
} from 'investigate-events/constants/pill';
import {
  batchValidation,
  clientSideValidation
} from 'investigate-events/actions/pill-validation-creators';

const { log } = console; // eslint-disable-line no-unused-vars
const BATCH_VALIDATION_PILL_COUNT = 5;

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
    dispatch(clientSideValidation({ pillData, position }));
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
    // In order to optimize performance, we use batch validation for pills when they are
    // more than some decided number. Batch validation enables us to make one call instead
    // of individual server calls.
    if (pillsData.filter(isValidatablePill).length >= BATCH_VALIDATION_PILL_COUNT) {
      dispatch(batchValidation(pillsData, initialPosition, true));
    } else {
      pillsData.forEach((pillData, i) => {
        // Any pill that extends from the `ValidatableFilter` class will need to
        // be validated. Excludes pill types like text, open/close paren, etc.
        if (isValidatablePill(pillData)) {
          dispatch(clientSideValidation({
            pillData,
            position: initialPosition + i,
            isFromParser: true
          }));
        }
      });
    }
  };
};

export const cancelPillCreation = (position) => {
  return (dispatch, getState) => {
    const { investigate: { queryNode: { pillsData } } } = getState();
    const emptyParens = findEmptyParensAtPosition(pillsData, position);
    const adjacentOperator = getAdjacentDeletableLogicalOperatorAt(pillsData, position);
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
      dispatch(clientSideValidation({ pillData, position }));
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
    dispatch(clientSideValidation({ pillData, position }));
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
    const nextPill = pillsData(getState())[position];
    if (nextPill?.type === OPERATOR_OR) {
      // Text pills must always have an AND next to them, not an OR
      const newPillData = {
        ...nextPill,
        type: OPERATOR_AND
      };
      batch.push(replaceLogicalOperator({ pillData: newPillData, position: position + 2 }));
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
    const nextPill = pillsData(getState())[position];
    // If adjacent to a text filter, must be an AND
    if (prevPill?.type === TEXT_FILTER || nextPill?.type === TEXT_FILTER) {
      pillData.type = OPERATOR_AND;
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