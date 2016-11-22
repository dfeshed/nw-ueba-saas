import Ember from 'ember';

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

  enabled: computed({
    get() {
      return this.get('_enabled');
    },

    set(key, value) {
      this.get('request').promiseRequest({
        method: 'setPreference',
        modelName: 'preferences',
        query: {
          contextMenuEnabled: value
        }
      }).then(() => {
        this.set('_enabled', value);
      }).catch(() => {
        Logger.error('Error updating preferences');
      });

      return value;
    }
  })

});
