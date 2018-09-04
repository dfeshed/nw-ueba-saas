import * as ACTION_TYPES from './types';
import { clientSideParseAndValidate, getMetaFormat } from './utils';
import { selectedPills, focusedPill } from 'investigate-events/reducers/investigate/query-node/selectors';
import validateQueryFragment from './fetch/query-validation';

import { transformTextToPillData, selectPillsFromPosition } from 'investigate-events/actions/utils';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';


const _validateGuidedPill = (pillData, position) => {
  return (dispatch, getState) => {
    // client side validation first
    // if that fails, no server side validation
    // if client side passes, send for server side validation
    const { meta, value } = pillData;
    // if there is no value, no need to validate.
    // The only pills that can be created without value are exists/!exits,
    // and they have relevant operators logic behind displaying them.
    if (value) {
      const { investigate: { dictionaries: { language } } } = getState();
      const metaFormat = getMetaFormat(meta, language);
      dispatch({
        type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
        promise: clientSideParseAndValidate(metaFormat, value),
        meta: {
          position, /*  position is needed to update pill in reducer  */
          onSuccess() {
            dispatch(_serverSideValidation(pillData, position));
          }
        }
      });
    }
  };
};

export const _serverSideValidation = (pillData, position) => {
  return (dispatch, getState) => {
    const { meta, operator, value } = pillData;
    // extract stringified pill data
    const stringifiedPill = `${meta || ''} ${operator || ''} ${value || ''}`.trim();
    const investigateState = getState().investigate;

    // encode the string and pull out the service id
    const encodedPill = encodeURIComponent(stringifiedPill);
    const { serviceId } = investigateState.queryNode;
    dispatch({
      type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
      promise: validateQueryFragment(serviceId, encodedPill),
      meta: {
        position, /*  position is needed to update pill in reducer  */
        isServerSide: true /*  sets a flag isPillBeingValidated while the req is being processed  */
      }
    });
  };
};

export const addGuidedPill = ({ pillData, position, shouldAddFocusToNewPill }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ADD_GUIDED_PILL,
      payload: {
        pillData,
        position,
        shouldAddFocusToNewPill
      }
    });
    dispatch(_validateGuidedPill(pillData, position));
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
    dispatch(_validateGuidedPill(pillData, position));
  };
};

export const deleteGuidedPill = ({ pillData }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DELETE_GUIDED_PILLS,
      payload: {
        pillData
      }
    });
    dispatch(deselectAllGuidedPills());
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

export const removeGuidedPillFocus = () => {
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

export const deselectGuidedPills = ({ pillData }, shouldIgnoreFocus = false) => ({
  type: ACTION_TYPES.DESELECT_GUIDED_PILLS,
  payload: {
    pillData,
    shouldIgnoreFocus
  }
});

export const selectGuidedPills = ({ pillData }, shouldIgnoreFocus = false) => ({
  type: ACTION_TYPES.SELECT_GUIDED_PILLS,
  payload: {
    pillData,
    shouldIgnoreFocus
  }
});

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

export const addFreeFormFilter = (freeFormText) => {
  return (dispatch, getState) => {
    const pillData = transformTextToPillData(
      freeFormText.trim(),
      metaKeySuggestionsForQueryBuilder(getState())
    );

    dispatch({
      type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
      payload: {
        pillData: [pillData]
      }
    });
  };
};

// Transform the text to what it would look like in pill form
export const updatedFreeFormText = (freeFormText) => {
  return (dispatch, getState) => {
    const pillData = transformTextToPillData(
      freeFormText.trim(),
      metaKeySuggestionsForQueryBuilder(getState())
    );
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