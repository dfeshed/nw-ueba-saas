export const findElement = function(selector, text) {
  const elements = document.querySelectorAll(selector);
  return Array.prototype.filter.call(elements, (element) => {
    const elementText = element.textContent;
    const trimmedText = elementText && elementText.trim();
    return RegExp(text).test(trimmedText);
  });
};
