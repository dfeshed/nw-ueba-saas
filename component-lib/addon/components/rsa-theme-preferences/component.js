import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { updateTheme } from 'sa/actions/creators/preferences';

const stateToComputed = (state) => ({
  theme: state.global.preferences.theme
});

const dispatchToActions = {
  updateTheme
};

const ThemePreferences = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(ThemePreferences);
