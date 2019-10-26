import { helper } from '@ember/component/helper';
import { lookup } from 'ember-dependency-lookup';

export function isColumnGroupInProfile([columnGroup, profiles]) {
  const profilesWithColumnGroup = profiles.filter((profile) => columnGroup.id === profile?.columnGroup?.id);
  const disableDelete = profilesWithColumnGroup.length > 0;
  let reason = '';
  if (disableDelete) {
    const i18n = lookup('service:i18n');
    const profileNames = profilesWithColumnGroup.map((d) => d.name).join(', ');
    reason = i18n.t('investigate.events.columnGroups.disabled.delete', { profileNames }).toString();
  }

  return {
    disableDelete,
    reason
  };
}

export default helper(isColumnGroupInProfile);