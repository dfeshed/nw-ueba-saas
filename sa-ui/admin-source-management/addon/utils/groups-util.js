export function sourceCountTooltip(i18n, isDirty, sourceCount, lastPublishedOn) {
  if (isDirty && lastPublishedOn > 0 && sourceCount >= 0) {
    // unpublished edit
    return i18n.t('adminUsm.groups.list.sourceCountUnpublishedEditedGroupTooltip');
  } else {
    switch (sourceCount) {
      case -1:
        return i18n.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
      case -2:
        return i18n.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
      case -3:
        return i18n.t('adminUsm.groups.list.sourceCountUnpublishedNewGroupTooltip');
      default:
        return '';
    }
  }
}

export function getSourceCount(sourceCount) {
  switch (sourceCount) {
    case -1:
      return 'Updating';
    case -2:
    case -3:
      return 'N/A';
    default:
      return sourceCount;
  }
}
