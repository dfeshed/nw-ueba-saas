export const getTextFromDOMArray = (arr) => {
  return arr.reduce((a, c) => a + c.textContent.trim().replace(/\s+/g, ''), '');
};
