const TEXT_FILTER_REGEX_TYPES = {
  ip: /^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
  agentID: /^[A-Za-z0-9-]*$/,
  agentVersion: /^[0-9.]*$/,
  macAddress: /^(?:[0-9A-Fa-f]{2}([:-]))(?:[0-9A-Fa-f]{2}\1){4}([0-9A-Fa-f]{2})$/,
  osDescription: /^[A-Za-z0-9-.()\s]*$/,
  onlyAlphabetChars: /^[A-Za-z]*$/,
  allKeyboardChars: /^([!-~])*$/,
  allKeyboardCharsWithSpace: /^([!-~\s])*$/
};

export const evaluateTextAgainstRegEx = (propertyValues, regEx = 'allKeyboardChars') => {
  const isValidText = propertyValues.filter((item) => !TEXT_FILTER_REGEX_TYPES[regEx].test(item.value));
  return isValidText.length;
};