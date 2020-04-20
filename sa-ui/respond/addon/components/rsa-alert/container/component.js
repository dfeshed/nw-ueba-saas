import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
import { resizeAlertInspector } from 'respond/actions/creators/alert-creators';
import { isNumeric } from 'component-lib/utils/jquery-replacement';

const INSPECTOR_MIN_WIDTH = 300;

const resolveWidth = (width) => Math.max((isNumeric(width) ? width : 0), INSPECTOR_MIN_WIDTH);

const stateToComputed = ({ respond: { alert: { inspectorWidth } } }) => ({
  inspectorWidth: resolveWidth(inspectorWidth)
});

const dispatchToActions = (dispatch) => ({
  resizeAction(width) {
    dispatch(resizeAlertInspector(width));
  }
});

const AlertContainer = Component.extend({
  tagName: 'hbox',
  layout,
  classNames: ['rsa-alert-container'],
  inspectorWidth: 400,

  inspectorContainerStyle: computed('inspectorWidth', function() {
    return htmlSafe(`width: ${this.inspectorWidth}px;`);
  })
});

export default connect(stateToComputed, dispatchToActions)(AlertContainer);
