import * as ACTION_TYPES from './types';
import { clientSideValidation, getMetaFormat } from './utils';

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

export const deleteNextGenPill = ({ pillData }) => ({
  type: ACTION_TYPES.DELETE_NEXT_GEN_PILL,
  payload: {
    pillData
  }
});

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