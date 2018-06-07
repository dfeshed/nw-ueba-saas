import reselect from 'reselect';
const { createSelector } = reselect;

const _language = (language) => language;
/**
 * A selector that returns a ip format map.
 * @private
 */
export const metaFormatMap = createSelector(
  _language,
  (language = []) => {
    const metaFormatMap = {};
    // Special condition for respond acceptance test failure need to correct this.
    if (!language || !language.forEach) {
      return metaFormatMap;
    }
    language.forEach((meta) => {
      metaFormatMap[meta.metaName] = meta.format;
    });
    return metaFormatMap;
  });