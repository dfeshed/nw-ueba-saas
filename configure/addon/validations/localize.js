import { lookup } from 'ember-dependency-lookup';

export const localizeMessage = (key, context) => {
  const i18n = lookup('service:i18n');
  const lookupKey = `${context}.${key}`;
  return i18n.t(lookupKey).toString();
};
