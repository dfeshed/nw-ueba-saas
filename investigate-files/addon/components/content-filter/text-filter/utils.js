const TEXT_FILTER_REGEX_TYPES = {
  alphaNumericChars: /^[A-Za-z0-9]*$/,
  allKeyboardChars: /^([!-~])*$/
};

export const evaluateTextAgainstRegEx = (propertyValues, regEx = 'allKeyboardChars') => {
  const isValidText = propertyValues.filter((item) => !TEXT_FILTER_REGEX_TYPES[regEx].test(item.value));
  return isValidText.length;
};