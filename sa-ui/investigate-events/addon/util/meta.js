/**
 * filter for valid meta
 * @param {*} meta
 */
export const filterValidMeta = (meta) => !meta.isIndexedByNone || meta.metaName === 'sessionid';

/**
 * returns last valid meta from array
 * @param {object[]} metaOptions
 */
export const lastValidMeta = (metaOptions = []) => [...metaOptions].reverse().find((d) => !d.disabled);
