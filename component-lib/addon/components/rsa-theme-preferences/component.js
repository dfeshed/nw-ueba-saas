import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { updateTheme } from 'netwitness-ueba/actions/creators/preferences';
import { getTheme } from 'netwitness-ueba/reducers/global/preferences/selectors';

const stateToComputed = (state) => ({
  theme: getTheme(state)
});

const dispatchToActions = {
  updateTheme
};

const ThemePreferences = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(ThemePreferences);
