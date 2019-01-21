import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';

export default Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  domainExtents: {
    y: { fixed: [ 0, 100 ] }
  },

  monitor: [
    {
      name: 'cpu_process',
      path: '/sys/stats/cpu.process',
      dataFunction: (value) => {
        return parseFloat(`${value}`.replace(/%$/, ''));
      },
      isSeries: true
    },
    {
      name: 'cpu',
      path: '/sys/stats/cpu',
      dataFunction: (value) => {
        return parseFloat(`${value}`.replace(/%$/, ''));
      },
      isSeries: true
    },
    {
      name: 'running_since',
      path: '/sys/stats/running.since'
    },
    {
      name: 'service_status',
      path: '/sys/stats/service.status'
    },
    {
      name: 'memory_process',
      path: '/sys/stats/memory.process'
    },
    {
      name: 'uptime',
      path: '/sys/stats/uptime',
      displayFunction: (value) => {

        const duration = parseFloat(value.split(',')[0]);
        value = value.split(',')[1];
        const weeks = parseFloat((/(\d+) week/.exec(value) || [])[1] || '0');
        const days = parseFloat((/(\d+) day/.exec(value) || [])[1] || '0');

        let durationStr = new Date(duration * 1000).toISOString().replace(/^[0-9-]*T/, '').replace(/\.\d*Z$/, '');

        if (weeks > 0 || days > 0) {
          const numDays = 7 * weeks + days;
          const s = numDays > 1 ? 's' : '';
          durationStr = `${numDays} day${s} ${durationStr}`;
        }

        return durationStr;
      }
    }
  ],

  actions: {
    shutdown() {
      const transport = this.get('transport');
      return transport.send('/sys', { message: 'shutdown' });
    }
  }
});
