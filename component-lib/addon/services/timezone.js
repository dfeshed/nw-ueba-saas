import Ember from 'ember';
import moment from 'moment';

const {
  Service,
  computed,
  inject: {
    service
  },
  isNone,
  Logger
} = Ember;

export default Service.extend({

  request: service(),

  options: moment.tz.names(),

  selected: computed({
    get() {
      if (isNone(this.get('_selected'))) {
        this.set('_selected', 'UTC');
      }

      return this.get('_selected');
    },

    set(key, value) {
      if (!isNone(this.get('_selected'))) {
        this.get('request').promiseRequest({
          method: 'setPreference',
          modelName: 'preferences',
          query: {
            data: {
              timeZone: value
            }
          }
        }).then(() => {
          this.set('_selected', value);
        }).catch(() => {
          Logger.error('Error updating preferences');
        });
      } else {
        this.set('_selected', value);
      }

      return value;
    }
  })

});
