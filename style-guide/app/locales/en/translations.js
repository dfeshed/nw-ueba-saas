import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/en/trans-data';
import SGTranslations from './trans-data';

const { $ } = Ember;

export default $.extend({}, BaseTranslations, SGTranslations);
