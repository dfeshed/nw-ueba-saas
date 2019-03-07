import Component from '@ember/component';
import { connect } from 'ember-redux';
import { toggleQueryConsole } from 'investigate-events/actions/interaction-creators';
import computed from 'ember-computed-decorators';

import {
  isConsoleEmpty,
  hasError,
  hasWarning,
  isQueryComplete,
  hasOfflineServices
} from 'investigate-events/reducers/investigate/query-stats/selectors';

const dispatchToActions = {
  toggleQueryConsole
};

const stateToComputed = (state) => ({
  description: state.investigate.queryStats.description,
  hasOfflineServices: hasOfflineServices(state),
  isDisabled: isConsoleEmpty(state),
  isOpen: state.investigate.queryStats.isConsoleOpen,
  hasError: hasError(state),
  hasWarning: hasWarning(state),
  isQueryComplete: isQueryComplete(state)
});

const ConsoleTrigger = Component.extend({
  classNames: ['console-trigger'],
  classNameBindings: [
    'isDisabled',
    'isOpen',
    'hasError',
    'hasWarning',
    'hasOfflineServices'
  ],

  @computed('description', 'isDisabled', 'isOpen', 'hasError', 'hasWarning', 'isQueryComplete', 'i18n')
  label: (description, isDisabled, isOpen, hasError, hasWarning, isQueryComplete, i18n) => {
    let label;
    if (isDisabled) {
      label = i18n.t('investigate.queryStats.disabledLabel');
    } else if (hasError) {
      label = i18n.t('investigate.queryStats.hasError');
    } else if (hasWarning) {
      label = i18n.t('investigate.queryStats.hasWarning');
    } else if (description && isOpen && !isQueryComplete) {
      label = description;
    } else {
      label = i18n.t('investigate.queryStats.openCloseLabel');
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
    let parentClass;
    if (clickedEl && clickedEl.parentElement && (typeof clickedEl.parentElement.className === 'string')) {
      parentClass = clickedEl.parentElement.className;
    } else {
      parentClass = '';
    }

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
