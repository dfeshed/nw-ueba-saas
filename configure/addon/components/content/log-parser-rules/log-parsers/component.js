import Component from '@ember/component';
import Confirmable from 'component-lib/mixins/confirmable';
import { connect } from 'ember-redux';
import {
  logParsers,
  selectedLogParserIndex,
  hasRuleChanges
} from 'configure/reducers/content/log-parser-rules/selectors';
import parserRuleCreators from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  logParsers: logParsers(state),
  selectedLogParserIndex: selectedLogParserIndex(state),
  hasRuleChanges: hasRuleChanges(state)
});

const dispatchToActions = {
  selectLogParser: parserRuleCreators.selectLogParser
};

const LogParsers = Component.extend(Confirmable, {
  classNames: ['log-parsers'],
  actions: {
    selectParser(parserIndex) {
      if (this.get('hasRuleChanges')) {
        this.send('showConfirmationDialog', 'confirm-unsaved-changes', {}, () => {
          this.send('selectLogParser', parserIndex);
        });
      } else {
        this.send('selectLogParser', parserIndex);
      }
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(LogParsers);
