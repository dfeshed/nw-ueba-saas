import _ from 'lodash';
/* Generates OS specific columns config */
export const generateColumns = (customColumns, defaultColumns) => {
  for (const key in customColumns) {
    if (customColumns.hasOwnProperty(key)) {
      customColumns[key] = [...defaultColumns, ...customColumns[key]];
    }
  }
  return customColumns;
};

/**
 * get all the selected agent ids, not having agent version = 4.4
 * @public
 * @param selectedAgentList
 */
export const getSelectedAgentIds = (selectedAgentList) => {
  return _.map(selectedAgentList.filter((agent) => agent && !agent.version.startsWith('4.4')), 'id');
};
