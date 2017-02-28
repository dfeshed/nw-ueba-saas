import Ember from 'ember';

const {
  Helper,
  inject: { service }
} = Ember;

// Generates a user-friendly name for a given indicator's source.
export function indicatorSource(i18n, { originalHeaders }) {
  // eslint-disable-next-line camelcase
  let { device_product: device = '', model_name: model = '' } = originalHeaders;
  device = device.replace(/\s/g, '-');
  model = model.replace(/\s/g, '-');

  return i18n.t(`respond.sources.${model}`, {
    default: [
      `respond.sources.${device}`,
      'respond.sources.generic'
    ]
  });
}

/**
 * Generates a localized description string for a given indicator's source.
 *
 * Generally speaking, we don't want to show the raw source; instead we want an i18n string that depends on the
 * indicator's data science model and/or the device that generated the indicator.
 *
 * @example If the source is "ECAT", don't show "ECAT" to the end-user; say "Endpoint" (in their locale) instead.
 * @example If the source is "ESA" and the data science model is WinAuth, say "User Behavior Analytics".
 *
 * Unlike the typical stateless Ember Helpers, this is a class-based Helper because it needs to access the i18n service.
 *
 * @param {object} indicator The indicator object.
 * @public
 */
export default Helper.extend({
  i18n: service(),
  compute([ indicator ]) {
    const i18n = this.get('i18n');
    return indicatorSource(i18n, indicator);
  }
});

