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
 * and is managed by the current server
 * @public
 * @param selectedAgentList
 */
export const getSelectedAgentIds = (selectedAgentList) => {
  return _.map(selectedAgentList.filter((agent) => agent && agent.version && !agent.version.startsWith('4.4') && agent.managed), 'id');
};

/* Checks if the clicked on file is already selected */

export const isAlreadySelected = (selections, item) => {
  let selected = false;
  if (selections && selections.length) {
    selected = selections.findBy('id', item.id) || false;
  }
  return selected;
};