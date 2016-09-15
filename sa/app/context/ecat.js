/**
 * Created by khanm23 on 8/24/2016.
 * Ecat object class
 * @public
 */
import Ember from 'ember';

const { Object: EmberObject, computed } = Ember;

export default EmberObject.extend({
  host: null,
  modules: computed(() => []),
  iioc: computed(() => []),
  processes: computed(() => []),
  network: computed(() => []),
  modulesCount: 0,
  minIoc: 0
});