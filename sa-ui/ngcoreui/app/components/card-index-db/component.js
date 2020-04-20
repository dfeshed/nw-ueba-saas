import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  monitor: [
    {
      name: 'queries_active',
      path: '/sdk/stats/queries.active',
      isSeries: true
    },
    {
      name: 'queries_pending',
      path: '/sdk/stats/queries.pending',
      isSeries: true
    },
    {
      name: 'query_memory',
      path: '/sdk/stats/query.memory'
    },
    {
      name: 'sessions_since_save',
      path: '/index/stats/sessions.since.save'
    },
    {
      name: 'time_begin',
      path: '/index/stats/time.begin'
    },
    {
      name: 'time_end',
      path: '/index/stats/time.end'
    },
    {
      name: 'meta_overflow',
      path: '/index/stats/meta.overflow'
    },
    {
      name: 'updater_state',
      path: '/index/stats/updater.state'
    }
  ],

  @computed('valuesAdapter')
  totalQueries: (valuesAdapter) => {
    const active = valuesAdapter.queries_active || '0';
    const pending = valuesAdapter.queries_pending || '0';
    return parseFloat(active) + parseFloat(pending);
  }

});
