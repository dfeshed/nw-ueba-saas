import { createSelector } from 'reselect';

export const listData = (state) => {
  return state.context.list.list || [];
};

const errorMessageType = [ 'listDuplicateName', 'listValidName'];

export const errorMessage = (state) => {
  return state.context.list.errorMessage;
};

export const entityType = (state) => {
  return state.context.list.entityType;
};

export const isError = createSelector(
  [errorMessage],
  (errorMessage) => {
    return !!errorMessage;
  }
);

export const isDisabled = createSelector(
  [errorMessage],
  (errorMessage) => {
    return !!errorMessageType.includes(errorMessage);
  }
);

