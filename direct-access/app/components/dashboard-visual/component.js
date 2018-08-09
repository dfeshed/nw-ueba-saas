// dashboard-visual
// This component wraps other visual components, such as rsa-gauge
// and rsa-chart. It handles the fetching and monitoring of data and
// passes this information on to the visual components. It also handles
// cleanup when the component is destroyed.

import Component from '@ember/component';
import { inject as service } from '@ember/service';
import Immutable from 'seamless-immutable';
import computed from 'ember-computed-decorators';

export default Component.extend({
  type: null,
  // Should be overridden by the user if the array in monitor
  // is given more than one item.
  dataFunction: (nodes, monitor) => {
    return nodes[monitor[0]];
  },
  label: null,
  animate: true,
  monitor: null,
  monitorHandles: null,
  intervalHandle: null,
  nodes: null,
  data: null,
  transport: service(),

  tagName: 'box',
  classNameBindings: ['xsColumns', 'smColumns', 'mdColumns', 'lgColumns'],

  @computed('type')
  xsColumns: (type) => type === 'graph' ? 'col-xs-12' : 'col-xs-6',

  @computed('type')
  smColumns: (type) => type === 'graph' ? 'col-sm-6' : 'col-sm-4',

  @computed('type')
  mdColumns: (type) => type === 'graph' ? 'col-md-6' : 'col-md-3',

  @computed('type')
  lgColumns: (type) => type === 'graph' ? 'col-lg-4' : 'col-lg-2',

  @computed('type')
  isGauge: (type) => type === 'gauge',

  @computed('type')
  isGraph: (type) => type === 'graph',

  /**
   * @private
   * Converts data into what the rsa charting component expects,
   * an array of arrays of flat objects.
   */
  @computed('data', 'isGraph')
  graphAdapter: (data, isGraph) => {
    if (!data || !isGraph) {
      return [];
    }
    return [ data.map((point, index) => {
      return { x: index, y: point };
    }) ];
  },

  /**
   * @private
   * The init hook:
   * - Sets up instance properties.
   * - Fetches the stat history, if the type is graph.
   * - Starts monitoring each node passed to the component in the monitor array.
   * - If the type is graph, it also sets an interval every 1 second to add an entry
   *   to array in data, while removing the oldest data point.
   * - If the type is gauge, data is a flat value instead of an array.
   */
  init() {
    this._super(...arguments);
    const { isGraph, dataFunction } = this.getProperties('isGraph', 'dataFunction');
    this.setProperties({
      monitorHandles: {},
      nodes: {}
    });
    const monitor = this.get('monitor');
    if (isGraph) {
      this.fetchHistory(monitor)
        .then(() => {
          for (let i = 0; i < monitor.length; i++) {
            this.startMonitoring(monitor[i]);
          }
          this.set('intervalHandle', setInterval(() => {
            let data = dataFunction(this.get('nodes'), monitor);
            if (typeof data === 'object') {
              data = data.value;
            }
            this.set('data',
              this.get('data')
                .slice(1)
                .concat(data));
          }, 1000));
        });
    } else {
      for (let i = 0; i < monitor.length; i++) {
        this.startMonitoring(monitor[i]);
      }
    }
  },

  /**
   * @private
   * Stops monitoring every node that was being monitored,
   * and clears the interval setting data points if this
   * has a type of graph.
   */
  willDestroy() {
    const transport = this.get('transport');
    const handles = Object.values(this.get('monitorHandles'));
    for (let i = 0; i < handles.length; i++) {
      transport.stopStream(handles[i]);
    }
    clearInterval(this.get('intervalHandle'));
  },

  /**
   * @private
   * Makes the API call to start receiving updates when nodes
   * are changed.
   */
  startMonitoring(path) {
    const { transport, monitor, monitorHandles, dataFunction, nodes, isGauge } = this.getProperties(
      'transport', 'monitor', 'monitorHandles', 'dataFunction', 'nodes', 'isGauge'
    );
    monitorHandles[path] = transport.stream({
      path,
      message: {
        message: 'mon'
      },
      messageCallback: (message) => {
        let node;
        if (message.nodes) {
          node = message.nodes[0];
        } else if (message.node) {
          node = message.node;
        }
        nodes[node.path] = node.value;
        if (!this.containsAllNodes(nodes)) {
          return;
        }
        if (isGauge) {
          let data = dataFunction(nodes, monitor);
          if (typeof data === 'object') {
            this.set('display', data.display);
            data = data.value;
          }
          this.set('data', data);
        } // Graph data (not gauge) updating handled in the interval set in init()
      },
      errorCallback: () => {
        throw new Error('Unexpected transport API error');
      }
    });
  },

  /**
   * @private
   * Fetches the stat history of all stats this component monitors at once.
   * Due to quirks in the stat database, it fetches the past 10 minutes of data
   * and then cuts it down to the past 1 minute. Lowering the amount of time fetched
   * could cause unexpected behavior.
   */
  fetchHistory(monitor) {
    const { transport, dataFunction } = this.getProperties('transport', 'dataFunction');
    return transport.send('/sys', {
      message: 'statHist',
      params: {
        // 10 minutes ago in POSIX time, as a string
        // Necessary to fetch the past 10 minutes and then cut down to 60 seconds
        time1: (Math.floor((new Date()).getTime() / 1000) - 600).toString(),
        include: monitor.join(','),
        showAll: 'true'
      }
    }).then((history) => {
      // Cut off everything but the last 60 entries
      const data = history.params.slice(-60).map((statInstance) => {
        if (!this.containsAllNodes(statInstance)) {
          return 0;
        } else {
          return dataFunction(statInstance, monitor);
        }
      });
      this.set('data', Immutable.from(data));
      return;
    });
  },

  containsAllNodes(nodes) {
    const monitor = this.get('monitor');
    for (let i = 0; i < monitor.length; i++) {
      if (!nodes[monitor[i]]) {
        return false;
      }
    }
    return true;
  }
});
