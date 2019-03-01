import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { loadLogs, logsFilterChange, logsFilterChangeDone } from 'ngcoreui/actions/actions';

const stateToComputed = (state) => ({
  logsFilterChangePending: state.logsFilterChangePending
});

const dispatchToActions = {
  loadLogs,
  logsFilterChange,
  logsFilterChangeDone
};

const logControls = Component.extend({
  transport: service(),

  tagName: 'hbox',
  classNames: ['bottom-padding', 'log-controls'],

  selectionType: 'id',
  debug: true,
  info: true,
  audit: true,
  warning: true,
  failure: true,

  id1: '1',
  id2: '101',
  time1: null,
  time2: null,
  time1Local: null,
  time2Local: null,
  latest: true,
  count: 1000,
  filter: '',
  regexMode: false,

  @computed('selectionType')
  isId: (type) => type === 'id',

  @computed('selectionType')
  isTime: (type) => type === 'time',

  // Turns 5 true/false properties into a string containing a comma-separated
  // list of the name of each true property
  @computed('debug', 'info', 'audit', 'warning', 'failure')
  logTypes: (debug, info, audit, warning, failure) => {
    return Object.entries({ debug, info, audit, warning, failure })
      .filter((type) => {
        return type[1];
      })
      .map((type) => {
        return type[0];
      })
      .join(',');
  },

  @computed('logTypes', 'selectionType', 'id1', 'id2', 'latest', 'count', 'time1', 'time2', 'regexMode', 'filter')
  params: (logTypes, selectionType, id1, id2, latest, count, time1, time2, regexMode, filter) => {
    const result = {
      logTypes
    };
    if (selectionType === 'id' && latest) {
      result.count = count;
      result.latest = true;
    } else if (selectionType === 'id') {
      result.id1 = id1;
      result.id2 = id2;
    } else if (selectionType === 'time') {
      result.time1 = time1;
      result.time2 = time2;
    }
    if (filter.length > 0) {
      if (regexMode) {
        result.regex = filter;
      } else {
        result.match = filter;
      }
    }
    return result;
  },

  didReceiveAttrs() {
    this.set('time1', Math.floor(this.set('time1Local', Math.floor((new Date()).getTime() - (24 * 60 * 60 * 1000))) / 1000).toString());
    this.set('time2', Math.floor(this.set('time2Local', Math.floor((new Date()).getTime())) / 1000).toString());
    this.send('updateParams');
  },

  willDestroyElement() {
    this.send('stopUpdates');
  },

  actions: {
    stopUpdates() {
      const intervalHandle = this.get('intervalHandle');
      if (intervalHandle) {
        intervalHandle.stop();
        this.set('intervalHandle', undefined);
      }
    },

    updateParams() {
      this.send('stopUpdates');
      this.send('logsFilterChangeDone');
      this.send('loadLogs', this.get('params'), (intervalHandle) => {
        this.set('intervalHandle', intervalHandle);
      });
    },

    typeChanged() {
      this.send('logsFilterChange');
    },

    toggleDebug() {
      this.toggleProperty('debug');
      this.send('logsFilterChange');
    },
    toggleInfo() {
      this.toggleProperty('info');
      this.send('logsFilterChange');
    },
    toggleAudit() {
      this.toggleProperty('audit');
      this.send('logsFilterChange');
    },
    toggleWarning() {
      this.toggleProperty('warning');
      this.send('logsFilterChange');
    },
    toggleFailure() {
      this.toggleProperty('failure');
      this.send('logsFilterChange');
    },
    toggleLatest() {
      this.toggleProperty('latest');
      this.send('logsFilterChange');
    },
    setTime1(time) {
      // time is an array of JS Date objects
      this.set('time1Local', time);
      // Convert from JS time to UNIX/epoc time
      this.set('time1', Math.floor(time[0].getTime() / 1000).toString());
      this.send('logsFilterChange');
    },
    setTime2(time) {
      this.set('time2Local', time);
      this.set('time2', Math.floor(time[0].getTime() / 1000).toString());
      this.send('logsFilterChange');
    },
    toggleRegexMode() {
      this.toggleProperty('regexMode');
      this.send('logsFilterChange');
    }
  },

  init() {
    this._super(...arguments);

    this.get('transport').one('close', () => {
      this.get('intervalHandle').stop();
    });
  },

  // Necessary to pass to the date-time picker because there is no
  // dateFormat service in this app
  dateFormat: {
    selected: {
      key: 'MM/dd/yyyy'
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(logControls);
