import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';

export default Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  moduleName: null,

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
