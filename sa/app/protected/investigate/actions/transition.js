/**
 * @file Investigate Route transition actions
 * Actions for transitioning to and from the Investigate route.
 * @public
 */
import Ember from 'ember';

const { run, Mixin } = Ember;

export default Mixin.create({
  actions: {
    // When entering this route, fetch services (if we haven't already).
    didTransition() {
      run.next(() => {
        this.send('servicesGet', false);
      });
    },

    // When leaving this route, close the server streams that may still be open (if any).
    willTransition() {
      this.send('servicesStop');
      this.send('eventsStop');
      this.send('eventCountStop');
    }
  }
});
