import Ember from 'ember';

const {
  Helper,
  Logger,
  inject
} = Ember;

export default Helper.extend({
  i18n: inject.service('i18n'),
  compute(params) {
    const [column] = params;
    const i18n = this.get('i18n');
    let translatedString = i18n.t(column.get('title'));
    const newTranslation = {};
    i18n.on('missing', function(locale, key) {
      const templateLog = `Missing translation handled: ${key}`;
      Logger.warn(templateLog);
    });

    if (typeof(translatedString) === 'string' && translatedString.indexOf('Missing') > -1) {
      translatedString = column.get('description');
      newTranslation[column.get('title')] = column.get('description');
      this.get('i18n').addTranslations('en', newTranslation);
      this.get('i18n').addTranslations('ja', newTranslation);
    }
    return translatedString;
  }
});