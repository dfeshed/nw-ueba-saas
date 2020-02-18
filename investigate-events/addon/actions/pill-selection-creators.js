import * as ACTION_TYPES from './types';
import {
  findMissingTwins,
  selectPillsFromPosition
} from 'investigate-events/actions/pill-utils';
import { selectedPills } from 'investigate-events/reducers/investigate/query-node/selectors';

const _pillSelectDeselect = (actionType, pillData, shouldIgnoreFocus = false) => {
  return (dispatch, getState) => {

    // now locate potential twins
    const { investigate: { queryNode: { pillsData } } } = getState();
    const missingTwins = findMissingTwins(pillData, pillsData);
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

export const selectAllPills = () => {
  return (dispatch, getState) => {
    const { investigate: { queryNode: { pillsData } } } = getState();
    // remove operators from list of pills to select
    const pillData = pillsData.filter((p) => !p.type.startsWith('operator-'));
    dispatch(selectGuidedPills({ pillData }, true));
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