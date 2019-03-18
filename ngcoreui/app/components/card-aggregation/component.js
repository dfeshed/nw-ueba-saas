import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { hasNoAggPermission } from '../../reducers/selectors/permissions';

const stateToComputed = (state) => ({
  hasNoAggPermission: hasNoAggPermission(state)
});

const cardAggregation = Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  moduleName: null,

  @computed('hasNoAggPermission', 'valuesAdapter')
  cannotStart: (hasNoAggPermission, valuesAdapter) => {
    return valuesAdapter.status ? valuesAdapter.status !== 'stopped' || hasNoAggPermission : true;
  },

  @computed('hasNoAggPermission', 'valuesAdapter')
  cannotStop: (hasNoAggPermission, valuesAdapter) => {
    return valuesAdapter.status ? valuesAdapter.status !== 'started' || hasNoAggPermission : true;
  },

  @computed('hasNoAggPermission', 'moduleName')
  noPermissionReason: (hasNoAggPermission, moduleName) => {
    return hasNoAggPermission ? `Aggregation control requires ${moduleName}.manage` : '';
  },

  monitor: [
    {
      name: 'session_rate',
      path: '/${moduleName}/stats/session.rate',
      isSeries: true
    },
    {
      name: 'meta_rate',
      path: '/${moduleName}/stats/meta.rate',
      isSeries: true
    },
    {
      name: 'status',
      path: '/${moduleName}/stats/status'
    }
  ],

  actions: {
    startAggregation() {
      const transport = this.get('transport');
      return transport.send(`/${this.moduleName}`, { message: 'start' });
    },
    stopAggregation() {
      const transport = this.get('transport');
      return transport.send(`/${this.moduleName}`, { message: 'stop' });
    }
  },

  init() {
    // set the correct module name in the monitor paths
    this.monitor.forEach((mon) => mon.path = mon.path.replace('${moduleName}', this.moduleName));

    // the `monitor-mixin` will be initialized which requires the monitor paths
    // set above be properly initialized
    this._super(...arguments);
  }
});

export default connect(stateToComputed)(cardAggregation);
