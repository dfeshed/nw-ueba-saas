import * as ACTION_TYPES from './types';
import { clientSideParseAndValidate, getMetaFormat } from './utils';
import { selectedPills } from 'investigate-events/reducers/investigate/query-node/selectors';
import validateQueryFragment from './fetch/query-validation';
import { transformTextToPillData } from 'investigate-events/actions/utils';

const _validateNextGenPill = (pillData, position) => {
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
        type: ACTION_TYPES.VALIDATE_NEXT_GEN_PILL,
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
      type: ACTION_TYPES.VALIDATE_NEXT_GEN_PILL,
      promise: validateQueryFragment(serviceId, encodedPill),
      meta: {
        position, /*  position is needed to update pill in reducer  */
        isServerSide: true /*  sets a flag isPillBeingValidated while the req is being processed  */
      }
    });
  };
};

export const addNextGenPill = ({ pillData, position }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
      payload: {
        pillData,
        position
      }
    });
    dispatch(_validateNextGenPill(pillData, position));
  };
};

export const editNextGenPill = ({ pillData, position }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.EDIT_NEXT_GEN_PILL,
      payload: {
        pillData
      }
    });
    dispatch(_validateNextGenPill(pillData, position));
  };
};

export const deleteNextGenPill = ({ pillData }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DELETE_NEXT_GEN_PILLS,
      payload: {
        pillData: [pillData]
      }
    });
    dispatch(deselectAllNextGenPills());
  };
};

export const deleteSelectedNextGenPills = () => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.DELETE_NEXT_GEN_PILLS,
      payload: {
        pillData: selectedPills(getState())
      }
    });
  };
};

export const deselectAllNextGenPills = () => {
  return (dispatch, getState) => {
    const pillData = selectedPills(getState());
    if (pillData.length > 0) {
      dispatch(deselectNextGenPills({ pillData }));
    }
  };
};

export const deselectNextGenPills = ({ pillData }) => ({
  type: ACTION_TYPES.DESELECT_NEXT_GEN_PILLS,
  payload: {
    pillData
  }
});

export const selectNextGenPills = ({ pillData }) => ({
  type: ACTION_TYPES.SELECT_NEXT_GEN_PILLS,
  payload: {
    pillData
  }
});

export const openNextGenPillForEdit = ({ pillData }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.OPEN_NEXT_GEN_PILL_FOR_EDIT,
      payload: {
        pillData
      }
    });
    dispatch(deselectAllNextGenPills());
  };
};

export const addFreeFormFilter = (freeFormText) => {
  const pillData = transformTextToPillData(freeFormText.trim());
  return {
    type: ACTION_TYPES.REPLACE_ALL_NEXT_GEN_PILLS,
    payload: {
      pillData: [pillData]
    }
  };
};

// Transform the text to what it would look like in pill form
export const updatedFreeFormText = (freeFormText) => {
  const pillData = transformTextToPillData(freeFormText.trim());
  return {
    type: ACTION_TYPES.UPDATE_FREE_FORM_TEXT,
    payload: {
      pillData
    }
  };
};