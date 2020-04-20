import { lookup } from 'ember-dependency-lookup';
import { FLASH_MESSAGE_TYPES } from 'component-lib/mixins/notifications';

function showFlashMessage(type, i18nKey, context, isTranslationRequired = true) {
  const i18n = lookup('service:i18n');
  const flashMessages = lookup('service:flashMessages');
  if (isTranslationRequired) {
    flashMessages[type.name](i18n.t(i18nKey, context), { iconName: type.icon });
  } else {
    flashMessages[type.name](i18nKey, { iconName: type.icon });
  }
}

export function success(i18nKey, context) {
  showFlashMessage(FLASH_MESSAGE_TYPES.SUCCESS, i18nKey, context);
}

export function failure(i18nKey, context, isTranslationRequired) {
  showFlashMessage(FLASH_MESSAGE_TYPES.ERROR, i18nKey, context, isTranslationRequired);
}

export function warning(i18nKey, context) {
  showFlashMessage(FLASH_MESSAGE_TYPES.WARNING, i18nKey, context);
}
