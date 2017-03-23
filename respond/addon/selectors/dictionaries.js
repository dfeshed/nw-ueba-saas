import reselect from 'reselect';

const { createSelector } = reselect;

const priorityTypesSelector = (state) => state.respond.dictionaries.priorityTypes;
const statusTypesSelector = (state) => state.respond.dictionaries.statusTypes;

export const statusOptions = createSelector(
  statusTypesSelector,
  (statusTypes) => {
    return statusTypes.map((type) => {
      return {
        name: type,
        label: type,
        labelKey: `respond.status.${type}`
      };
    });
  }
);

export const priorityOptions = createSelector(
  priorityTypesSelector,
  (priorityTypes) => {
    return priorityTypes.map((type) => {
      return {
        name: type,
        label: type,
        labelKey: `respond.priority.${type}`
      };
    });
  }
);