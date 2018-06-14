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

const ignoredKeyCodes = [37, 38, 39, 40]; // left-arrow, up-arrow, right-arrow, down-arrow

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

  /**
   * Handler for debounced key-up events to trigger text highlighting. Using innerText prevents html/markup from
   * being submitted in the highlight request, and also ensures whitespace (e.g., new lines) is preserved.
   *
   * The plain text is also stored locally in the _tempLogs property, which is displayed during the highlighting API call to prevent
   * glimmer errors on re-render. We do this because the contenteditable `pre` tag can dynamically insert some html content (e.g.,
   * divs for new lines) as the user types. Glimmer can then get confused when trying to reconcile expected dom structure
   * with the actual dom structure (after the user types). To work around this issue, an entirely new `pre` element is rendered
   * using the _tempLogs text to replace the original `pre` tag as soon as we submit the highlight request. Once highlighting
   * is complete, the _tempLogs `pre` tag is completely replaced with a new one that includes the highlighted content.
   * @private
   */
  handleKeyUp() {
    const editedSampleLogs = this.element.querySelector('pre').innerText;
    if (editedSampleLogs.trim()) {
      this.set('_tempLogs', editedSampleLogs);
      this.send('highlightSampleLogs', editedSampleLogs);
    }
  },
  /**
   * If a user selects text (e.g., from a web page in Chrome) there's a possibility that the pasted information will
   * include HTML elements. The paste event handler here ensures that pasted information is treated as raw text.
   * @method paste
   * @param event
   * @private
   * @returns {boolean}
   */
  paste(event) {
    const text = event.originalEvent.clipboardData.getData('text');
    document.execCommand('insertText', false, text);
    return false;
  },
  /**
   * Handle the key up event, ignoring the arrow keys so that cursor navigation in the contentedible element does not
   * trigger the keyup handler. Debounce is used to prevent highlight requests from happening as the user types.
   * @method keyUp
   * @param event
   * @private
   */
  keyUp(event) {
    const key = event.which || event.keyCode;
    if (!ignoredKeyCodes.includes(key)) {
      debounce(this, this.handleKeyUp, this.get('keyUpDelay'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SampleLogMessage);
