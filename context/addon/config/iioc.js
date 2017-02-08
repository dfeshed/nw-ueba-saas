/**
 * @file Iioc level
 * @public
 */
import Ember from 'ember';

const { Object: EmberObject, computed } = Ember;

export default EmberObject.extend({
  'iiocLevel0': computed(() => []),
  'iiocLevel1': computed(() => []),
  'iiocLevel2': computed(() => []),
  'iiocLevel3': computed(() => [])
});