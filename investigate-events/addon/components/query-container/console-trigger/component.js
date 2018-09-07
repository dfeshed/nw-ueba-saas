import Component from '@ember/component';
import { connect } from 'ember-redux';
import { toggleQueryConsole } from 'investigate-events/actions/interaction-creators';
import computed from 'ember-computed-decorators';

import {
  isConsoleEmpty,
  hasError,
  hasWarning
} from 'investigate-events/reducers/investigate/query-stats/selectors';

const dispatchToActions = {
  toggleQueryConsole
};

const stateToComputed = (state) => ({
  description: state.investigate.queryStats.description,
  isDisabled: isConsoleEmpty(state),
  isOpen: state.investigate.queryStats.isConsoleOpen,
  hasError: hasError(state),
  hasWarning: hasWarning(state)
});

const ConsoleTrigger = Component.extend({
  classNames: ['console-trigger'],
  classNameBindings: [
    'isDisabled',
    'isOpen',
    'hasError',
    'hasWarning'
  ],

  @computed('description', 'isDisabled', 'isOpen', 'i18n')
  label: (description, isDisabled, isOpen, i18n) => {
    let label;
    if (isDisabled) {
      label = i18n.t('investigate.queryStats.disabledLabel');
    } else if (!isOpen) {
      label = i18n.t('investigate.queryStats.closedLabel');
    } else {
      label = description;
    }
    return label;
  },

  didInsertElement() {
    this._super(...arguments);

    const _boundEscapeListener = this._onEscapeKey.bind(this);
    this.set('_boundEscapeListener', _boundEscapeListener);
    window.addEventListener('keydown', _boundEscapeListener);

    const _boundClickListener = this._appClick.bind(this);
    this.set('_boundClickListener', _boundClickListener);
    window.addEventListener('click', _boundClickListener);
  },

  willDestroyElement() {
    this._super(...arguments);

    window.removeEventListener('keydown', this.get('_boundEscapeListener'));
    window.removeEventListener('click', this.get('_boundClickListener'));
  },

  _appClick(e) {
    const clickedEl = e.target;
    const parentClass = clickedEl.parentElement.className;
    const triggerClicked = parentClass && parentClass.includes('console-trigger');
    const consoleClicked = this.$().closest('.query-bar-selection').find('.console-panel').find(clickedEl).length > 0;
    const isOpen = this.get('isOpen');

    if (triggerClicked || (!triggerClicked && !consoleClicked && isOpen)) {
      this.send('toggleQueryConsole');
    }
  },

  _onEscapeKey(e) {
    if (this.get('isOpen') && e.keyCode === 27) {
      this.send('toggleQueryConsole');
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ConsoleTrigger);
