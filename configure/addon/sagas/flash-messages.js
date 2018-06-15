import { lookup } from 'ember-dependency-lookup';
import { FLASH_MESSAGE_TYPES } from 'component-lib/mixins/notifications';

function showFlashMessage(type, i18nKey, context) {
  const i18n = lookup('service:i18n');
  const flashMessages = lookup('service:flashMessages');
  flashMessages[type.name](i18n.t(i18nKey, context), { iconName: type.icon });
}

export function info(i18nKey, context) {
  showFlashMessage(FLASH_MESSAGE_TYPES.INFO, i18nKey, context);
}

export function success(i18nKey, context) {
  showFlashMessage(FLASH_MESSAGE_TYPES.SUCCESS, i18nKey, context);
}

export function failure(i18nKey, context) {
  showFlashMessage(FLASH_MESSAGE_TYPES.ERROR, i18nKey, context);
}
