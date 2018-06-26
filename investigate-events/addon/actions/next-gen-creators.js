import * as ACTION_TYPES from './types';
import { clientSideValidation, getMetaFormat } from './utils';
import { selectedPills } from 'investigate-events/reducers/investigate/next-gen/selectors';

const _validateNextGenPill = (pillData, position) => {
  return (dispatch, getState) => {
    // client side validation first
    // if that fails, no server side validation - send edit action
    // if client side passes, send for server side validation - send response for editing the pill
    const { meta, value } = pillData;
    const { investigate, investigate: { dictionaries: { language } } } = getState();
    const metaFormat = getMetaFormat(meta, language);
    const { isInvalid, validationError } = clientSideValidation(metaFormat, value);
    if (isInvalid) {
      // Need to extract the id for the pill that's already been
      // added to the state.
      // Will update the pill with the validation error
      const { pillsData } = investigate.nextGen;
      const currentPill = pillsData[position];
      const validatedPillData = {
        ...pillData,
        id: currentPill.id,
        isInvalid,
        validationError
      };
      dispatch({
        type: ACTION_TYPES.VALIDATE_NEXT_GEN_PILL,
        payload: { validatedPillData }
      });
    }
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
  return (dispatch, getState) => {
    let pillsToDelete = [pillData];
    const selectedPilz = selectedPills(getState());
    // If deleting and there are selected pills
    // then we are deleting all the selected pills
    if (selectedPilz.length > 0) {
      pillsToDelete = selectedPilz;
    }
    dispatch({
      type: ACTION_TYPES.DELETE_NEXT_GEN_PILLS,
      payload: {
        pillData: pillsToDelete
      }
    });
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