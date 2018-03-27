import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { updateLocale } from 'sa/actions/creators/preferences';

const stateToComputed = (state) => ({
  locale: state.global.preferences.locale,
  locales: state.global.preferences.locales
});

const dispatchToActions = {
  updateLocale
};

const LocalePreferences = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(LocalePreferences);
