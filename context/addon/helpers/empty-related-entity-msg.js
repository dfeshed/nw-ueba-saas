import { helper } from 'ember-helper';
import { isEmpty } from 'ember-utils';

export function emptyRelatedEntityMsg([dataStore, contextData, i18n]) {
  let entityMsg = '';
  if (dataStore == 'RelatedIps' && isEmpty(contextData.RelatedIps.resultList)) {
    entityMsg = 'context.lc.ips';
  } else if (dataStore == 'RelatedFiles' && isEmpty(contextData.RelatedFiles.resultList)) {
    entityMsg = 'context.lc.files';
  } else if (dataStore == 'RelatedDomains' && isEmpty(contextData.RelatedDomains.resultList)) {
    entityMsg = 'context.lc.domains';
  }
  if (entityMsg != '') {
    return i18n.t('context.lc.noRelatedData', { entity: i18n.t(entityMsg) });
  }
}
export default helper(emptyRelatedEntityMsg);
