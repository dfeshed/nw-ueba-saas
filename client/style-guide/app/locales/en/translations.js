import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/en/translations';

export default Ember.$.extend({}, BaseTranslations, {
  application: {
    title: 'Style Guide',
    version: 'v1.0',
    copyright: '&copy;2015 RSA Security LLC. All rights reserved.'
  }
});
