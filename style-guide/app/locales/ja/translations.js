import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/ja/trans-data';
import SGTranslations from './trans-data';

const { $ } = Ember;

export default $.extend({}, BaseTranslations, SGTranslations);
