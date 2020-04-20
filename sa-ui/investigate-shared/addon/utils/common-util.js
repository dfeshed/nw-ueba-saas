/**
 * Converts the given pixel into 'VW' format
 * @param width in pixel
 * @returns {string}
 */
export const convertPixelToVW = (width) => {

  if (!isNaN(width)) {
    const clientWidth = (100 / document.documentElement.clientWidth);
    const result = Math.floor(width * clientWidth);
    return `${result}vw`;
  }
  return width;
};
