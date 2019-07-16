/**
 * filter for valid meta
 * @param {*} meta
 */
export const filterValidMeta = (meta) => !meta.isIndexedByNone || meta.metaName === 'sessionid';
