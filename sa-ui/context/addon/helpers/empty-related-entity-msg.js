import { helper } from '@ember/component/helper';

const dataStoreMap = {
  RelatedIps: 'ips',
  RelatedFiles: 'files',
  RelatedDomains: 'domains'
};
export function emptyRelatedEntityMsg([dataStore, count, i18n]) {
  if (count !== 0) {
    return;
  }
  return i18n.t('context.lc.noRelatedData', { entity: i18n.t(`context.lc.${dataStoreMap[dataStore]}`) });
}
export default helper(emptyRelatedEntityMsg);
