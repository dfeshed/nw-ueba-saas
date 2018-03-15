import { createSelector } from 'reselect';

const _lookupData = (state) => {
  return state.lookupData || [];
};

export const getLookupData = createSelector(
 [ _lookupData],
 ([lookupData]) => {
   return lookupData;
 });
