import { GET_RIAC_SETTINGS } from '../types';

const createRiacAction = function(promise) {
  return {
    type: GET_RIAC_SETTINGS,
    promise
  };
};

export default {
  createRiacAction
};