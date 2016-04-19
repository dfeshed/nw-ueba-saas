import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';
import timeUtil from 'sa/utils/time';

export default Ember.Controller.extend({

  /**
   * The time range unit of the current data query. A value from the enumeration sa/utils/time.UNITS.
   * @type String
   * @public
   */
  timeRangeUnit: timeUtil.UNITS.DAY,

  /**
   * The time range for these data records. An object with 2 properties, 'from' and 'to', which are both UTC Dates
   * (in milliseconds) cast as long integers.  This property is computed from 'timeRangeUnit'. To change the
   * timeRange, simply update the timeRangeUnit.  Note that timeRange is always computed using now as the 'to' value.
   * @type {from: number, to: number}
   * @public
   */
  timeRange: function() {
    let now = Number(new Date());
    return {
      from: now - timeUtil.toMillisec(this.get('timeRangeUnit')),
      to: now
    };
  }.property('timeRangeUnit'),

  /**
   * Fetches a new model for the current time range, and discards the old model (if any) to free up memory usage.
   * Waits to discard the old model until once we have wired up a new model; that has two benefits:
   * (1) if an error occurs, we're not left with an empty UI, and
   * (2) the UI isn't updated unnecessarily as the old model dumps its data.
   * Note that these models are cubes, which can contain large caches that are memory intensive, so discarding them
   * after done using them is important.
   * @public
   */
  fetchModel: function() {
    let me = this,
        timeRangeUnit = this.get('timeRangeUnit'),
        timeRange = this.get('timeRange'),
        oldModel = this.get('model');

    // If any old model is still streaming, stop the stream. But don't destroy the model yet, so at least
    // the user can still view its data while awaiting for the new data stream.
    if (oldModel) {
      let arr = oldModel.get('records');
      if (arr && arr.cancel) {
        arr.cancel();
      }
    }

    // Create a new model: a cube to wrap the incoming results.
    let newModel = IncidentsCube.create({
      array: [],
      timeRangeUnit,
      timeRange
    });

    // Preserve the old model's sort & filters onto the new model, so the only change is the time range.
    // @assumes  Time range filter is applied only on server, not on client.
    if (oldModel) {
      newModel
        .sort(oldModel.get('sortField'), oldModel.get('sortDesc'))
        .filter(oldModel.filters());
    }
    me.set('model', newModel);

    // Now that we are pointing to a new model, destroy the old one, if any.
    if (oldModel) {
      oldModel.destroy();
    }

    // Kick off the data request.
    this.store.stream('incident', {
      sort: [{ field: 'created', descending: true }],
      filter: [{ field: 'created', range: timeRange }]
    }).autoStart()
      .toArray(newModel.get('records'));

  }.observes('timeRangeUnit')
});
