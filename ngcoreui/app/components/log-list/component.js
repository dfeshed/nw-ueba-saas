import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  // TODO selectors?
  availablePermissions: state.shared.availablePermissions,
  logs: state.shared.logs,
  logsLoading: state.shared.logsLoading
});

const logList = Component.extend({
  tagName: 'vbox',
  classNames: ['max-height', 'padding', 'scroll-box', 'border-line-top', 'log-container'],
  isScrolledToBottom: false,

  @computed('logs')
  noLogs: (logs) => logs ? logs.length === 0 : false,

  @computed('availablePermissions')
  logsPermissionDenied: (availablePermissions) => availablePermissions ? availablePermissions.indexOf('logs.manage') === -1 : false,

  willUpdate() {
    const [ container ] = document.getElementsByClassName('log-container');
    const isScrolledToBottom = container.scrollHeight - container.clientHeight <= container.scrollTop + 1;
    this.set('isScrolledToBottom', isScrolledToBottom);
  },

  didUpdate() {
    const [ container ] = document.getElementsByClassName('log-container');
    const isScrolledToBottom = this.get('isScrolledToBottom');
    if (isScrolledToBottom) {
      container.scrollTop = container.scrollHeight - container.clientHeight;
    }
  }
});

export default connect(stateToComputed)(logList);
