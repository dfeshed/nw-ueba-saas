import Ember from 'ember';
import moment from 'moment';

const {
  Service,
  computed,
  inject: {
    service
  },
  Logger
} = Ember;

export default Service.extend({

  request: service(),

  options: moment.tz.names(),

  selected: computed({
    get() {
      return this.get('_selected');
    },

    set(key, value) {

      this.get('request').promiseRequest({
        method: 'setPreference',
        modelName: 'preferences',
        query: {
          timeZone: value
        }
      }).then(() => {
        this.set('_selected', value);
      }).catch(() => {
        Logger.error('Error updating preferences');
      });

      return value;
    }
  })

});
