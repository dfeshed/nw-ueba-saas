const MAX_TEXT_LENGTH = 20;

/**
 * Truncate the process name
 * @param text
 * @returns {*}
 * @public
 */
export const truncateText = function(text) {
  const len = (text || '').length;
  if (len <= MAX_TEXT_LENGTH) {
    return text;
  } else {
    return `${text.substr(0, MAX_TEXT_LENGTH)}...`;
  }
};
