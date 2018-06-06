import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedLogParserName,
  hasDeployableRules
} from 'configure/reducers/content/log-parser-rules/selectors';
import { deployLogParser } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  selectedLogParserName: selectedLogParserName(state),
  hasNoDeployableRules: !hasDeployableRules(state)
});

const dispatchToActions = {
  deployLogParser
};

const LogParsersToolbar = Component.extend({
  tagName: 'div',
  classNames: ['log-parser-toolbar', 'buttons-container']
});
export default connect(stateToComputed, dispatchToActions)(LogParsersToolbar);
