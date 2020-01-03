import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { updateLocale } from 'netwitness-ueba/actions/creators/preferences';
import { getLocale, getLocales } from 'netwitness-ueba/reducers/global/preferences/selectors';

const stateToComputed = (state) => ({
  locale: getLocale(state),
  locales: getLocales(state)
});

const dispatchToActions = {
  updateLocale
};

const LocalePreferences = Component.extend({
  layout,
  testId: 'locale-preferences',
  attributeBindings: ['testId:test-id']
});

export default connect(stateToComputed, dispatchToActions)(LocalePreferences);
