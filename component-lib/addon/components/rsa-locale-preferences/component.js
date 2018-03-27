import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { updateLocale } from 'sa/actions/creators/preferences';
import { getLocale, getLocales } from 'sa/reducers/global/preferences/selectors';

const stateToComputed = (state) => ({
  locale: getLocale(state),
  locales: getLocales(state)
});

const dispatchToActions = {
  updateLocale
};

const LocalePreferences = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(LocalePreferences);
