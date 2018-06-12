import Component from '@ember/component';
import { debounce } from '@ember/runloop';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
import { highlightSampleLogs } from 'configure/actions/creators/content/log-parser-rule-creators';
import {
  highlightedLogs,
  isHighlighting,
  sampleLogsStatus
} from 'configure/reducers/content/log-parser-rules/selectors';

const stateToComputed = (state) => {
  return {
    highlightedLogs: htmlSafe(highlightedLogs(state)),
    isHighlighting: isHighlighting(state),
    sampleLogsStatus: sampleLogsStatus(state)
  };
};

const dispatchToActions = {
  highlightSampleLogs
};

const SampleLogMessage = Component.extend({
  classNames: ['sample-log-message'],

  /**
   * Plain text verion of the edited sample logs to be shown when running log highlighting
   * @property tempLogs
   * @private
   */
  _tempLogs: null,

  /**
   * The delay in milliseconds after a key up event before sending the highlight request
   * property keyUpDelay
   * @public
   */
  keyUpDelay: 2000,

  handleKeyUp() {
    const editedSampleLogs = this.element.querySelector('pre').innerText;
    if (editedSampleLogs.trim()) {
      this.set('_tempLogs', editedSampleLogs);
      this.send('highlightSampleLogs', editedSampleLogs);
    }
  },
  paste(event) {
    const text = event.originalEvent.clipboardData.getData('text');
    document.execCommand('insertText', false, text);
    return false;
  },
  keyUp() {
    debounce(this, this.handleKeyUp, this.get('keyUpDelay'));
  }
});

export default connect(stateToComputed, dispatchToActions)(SampleLogMessage);
