import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/ja/trans-data';
import SATranslations from './trans-data';

const { $ } = Ember;

export default $.extend({}, BaseTranslations, SATranslations);
