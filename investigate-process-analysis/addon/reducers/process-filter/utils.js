import _ from 'lodash';

export const updatefilterActionArray = function(filterActionList, schemaActionList) {
  const actionListFromSchema = schemaActionList.map((item) => item.name);
  if (actionListFromSchema.length) {
    return _.filter(filterActionList, function(actionItem) {
      return actionListFromSchema.includes(actionItem);
    });
  }
  return [];
};