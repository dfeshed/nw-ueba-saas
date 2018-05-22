import * as ACTION_TYPES from './types';

export const addNextGenPill = ({ pillData, position }) => ({
  type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
  payload: {
    pillData,
    position
  }
});

export const deleteNextGenPill = ({ pillData }) => ({
  type: ACTION_TYPES.DELETE_NEXT_GEN_PILL,
  payload: {
    pillData
  }
});