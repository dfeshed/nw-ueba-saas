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

export const isolateMachineValidation = (value) => {
  const IPv4IPv6Format = /((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))/;
  let listOfIPs = value.split(',');
  listOfIPs = listOfIPs.filter((item) => item.trim() !== '').map((item) => item.trim());
  const isInvalidIPFormatPresent = listOfIPs.some((item) => !IPv4IPv6Format.test(item));
  return { listOfIPs, isInvalidIPFormatPresent };
};

export const convertBytesIntoKbOrMb = (bytes = 1024) => {
  const KB = 1024; // 1KB in bytes.
  const MB = 1048576; // 1MB in bytes.
  let unit = 'KB';
  let value = 1;
  if (bytes >= MB) {
    value = bytes / MB;
    unit = 'MB';
  } else {
    value = bytes / KB;
  }
  return {
    value: value.toFixed(2),
    unit
  };
};
/**
 * filePathValidation checks if the file path is follows the format of the selected host OS.
 * It also checks if more than 1 '*' is present in the directory path or the file name.
 * @param path
 * @param pathType
 * @param filePathSeparatorFormat
 * @returns {boolean}
 */
export const filePathValidation = (path, pathType = 'windows', filePathSeparatorFormat = '\\') => {

  const windows = /^(?:[\w]:)(\\([^/\\]+))+$/;
  const linux = /^((\/)[^/\\]+)+$/;

  const osTypeRegex = { windows, linux, mac: linux };

  if (osTypeRegex[pathType].test(path)) {
    const lastIndexOfSeparator = path.lastIndexOf(filePathSeparatorFormat);
    const filePath = path.slice(0, lastIndexOfSeparator);
    const fileName = path.slice(lastIndexOfSeparator + 1);

    return fileName.length ? ((filePath.match(/\*/g) || []).length <= 1) && ((fileName.match(/\*/g) || []).length <= 1) : false;
  }

  return false;
};

/**
 * numberValidation checks to see if the value sent is a number and if it is within the range specified.
 * @param value
 * @param valueRangeObj
 * @returns {{isInvalid: boolean, value: number}}
 */

export const numberValidation = (value, valueRangeObj = {}) => {
  const { lowerLimit = value, upperLimit = value, defaultValue = 1 } = valueRangeObj;
  let isInvalid = false;
  let roundedOffValue = ((typeof value === 'string') && (value.trim().length === 0)) ? defaultValue : Math.round(value);

  if (isNaN(roundedOffValue)) {
    isInvalid = true;
    roundedOffValue = value;
  } else {
    const lowerLimitEvaluation = lowerLimit ? roundedOffValue >= lowerLimit : true;
    const upperLimitEvaluation = upperLimit ? roundedOffValue <= upperLimit : true;
    isInvalid = !(lowerLimitEvaluation && upperLimitEvaluation);
  }

  return { isInvalid, value: roundedOffValue };

};
