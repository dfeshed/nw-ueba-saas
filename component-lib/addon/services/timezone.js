import Ember from 'ember';

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

  options: null,

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          timeZone: value
        }
      }
    }).catch(() => {
      Logger.error('Error updating preferences');
    });
  },

  selected: computed({
    get() {
      return this.get('_selected');
    },

    set(key, value) {
      if (value.zoneId) {
        if (!isNone(this.get('_selected'))) {
          this.persist(value.zoneId);
        }
        this.set('_selected', value);
        return value;
      } else {
        if (!isNone(this.get('_selected'))) {
          this.persist(value);
        }
        this.set('_selected', this.get('options').findBy('zoneId', value));
        return this.get('_selected');
      }
    }
  })

});
