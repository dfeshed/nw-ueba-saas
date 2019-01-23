// monitor-mixin
// This component handles the fetching and monitoring of data and
// passes this information on to the visual components. It also handles
// cleanup when the component is destroyed.

import Mixin from '@ember/object/mixin';
import { inject as service } from '@ember/service';
import Immutable from 'seamless-immutable';
import computed from 'ember-computed-decorators';

export default Mixin.create({
  type: null,

  monitor: null,
  monitorHandles: null,
  intervalHandle: null,
  nodes: null,
  data: null,
  valuesAdapter: null,
  transport: service(),

  dataFunction: null,

  /**
   * @private
   * Converts data into what the rsa charting component expects,
   * an array of arrays of flat objects.
   */
  @computed('data')
  graphAdapter: (data) => {
    return data.map((d) => {
      return d.map((point, index) => {
        return { x: index, y: point };
      });
    });
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
    this.setProperties({
      monitorHandles: {},
      nodes: {},
      valuesAdapter: {},
      data: [],
      dataFunction: parseFloat
    });
    const monitor = this.get('monitor');

    // initiate monitoring of *all* values
    monitor.forEach((mon) => this.startMonitoring(mon.path));

    // if there is series data, fetchHistory and then create poller to update series data from monitors
    const monitorSeries = monitor.filter((mon) => mon.isSeries === true);
    if (monitorSeries && monitorSeries.length !== 0) {
      this.fetchHistory(monitorSeries)
        .then(() => {
          // set a poll to append to series data the current values (as updated by monitoring)
          this.set('intervalHandle', setInterval(() => {
            const series = this.get('data');
            const data = Array(series.length).fill([]);
            monitorSeries.forEach((mon, index) => {
              let value = this.get('nodes')[mon.path];
              value = value ? (mon.dataFunction || this.dataFunction)(value) : 0;

              // use the series monitor index for the data series
              data[index] = series[index].concat(value).slice(-60);
            });
            this.set('data', data);
          }, 1000));
        });
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
    handles.forEach((handle) => transport.stopStream(handle));
    clearInterval(this.get('intervalHandle'));
  },

  /**
   * @private
   * Makes the API call to start receiving updates when nodes
   * are changed.
   */
  startMonitoring(path) {
    const { transport, monitorHandles, nodes, valuesAdapter } = this.getProperties(
      'transport', 'monitorHandles', 'nodes', 'valuesAdapter'
    );
    monitorHandles[path] = transport.stream({
      path,
      message: {
        message: 'mon'
      },
      messageCallback: (message) => {
        const updates = message.nodes ? message.nodes : [ message.node ];
        if (updates && updates.length > 0) {
          updates.forEach((value) => nodes[value.path] = value.value);
          this.set('nodes', Immutable.from(nodes));
          this.updateValues(nodes, valuesAdapter);
        }
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
  fetchHistory(monitorSeries) {
    const transport = this.get('transport');
    const seriesPaths = monitorSeries.map((mon) => mon.path);
    return transport.send('/sys', {
      message: 'statHist',
      params: {
        // 10 minutes ago in POSIX time, as a string
        // Necessary to fetch the past 10 minutes and then cut down to 60 seconds
        time1: (Math.floor((new Date()).getTime() / 1000) - 600).toString(),
        include: seriesPaths.join(','),
        showAll: 'true'
      }
    }).then((history) => {
      // initialize result set
      const data = Array(monitorSeries.length).fill().map(() => []);

      // Cut off everything but the last 60 entries
      const params = history.params.slice(-60);
      params.forEach((statInstance) => {
        monitorSeries.forEach((mon, index) => {
          const value = statInstance[mon.path];
          const df = mon.dataFunction || this.dataFunction;
          data[index].push(value ? df(value) : 0);
        });
      });
      this.set('data', Immutable.from(data));
      return;
    });
  },

  /**
   * @private
   */
  updateValues(nodes, values) {
    const monitor = this.get('monitor');
    monitor.forEach((mon) => {
      if (mon.name) {
        let value = nodes[mon.path];
        if (value && mon.displayFunction) {
          value = mon.displayFunction(value);
        }
        values[mon.name] = value;
      }
    });
    this.set('valuesAdapter', Immutable.from(values));
  }
});
