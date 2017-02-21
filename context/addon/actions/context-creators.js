import * as ACTION_TYPES from './types';

const updateActiveTab = (activeTab) => {
  return {
    type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
    payload: activeTab
  };
};

export {
  updateActiveTab
};