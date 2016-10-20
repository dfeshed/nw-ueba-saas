import config from 'sa/config/environment';

const types = config.contextLookup.entityTypes || [];

/**
 * Given a meta key name, finds the corresponding entity type, if any.
 * @param {string} metaName Identifier of a meta key (e.g., 'ip.src').
 * @returns {object|undefined} The configuration object for the corresponding context entity type, if any; otherwise undefined.
 * @public
 */
export default function contextEntityTypeByMeta(metaName) {
  return types.find((type) => {
    if (type.enabled && type.metaKeys) {
      return type.metaKeys.indexOf(metaName) > -1;
    }
    return false;
  });
}
