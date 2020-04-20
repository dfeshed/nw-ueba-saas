export const t = (context, key, params) => {
  const i18n = context.owner.lookup('service:i18n');
  return i18n.t(key, params).toString();
};
