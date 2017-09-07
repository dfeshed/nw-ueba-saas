import { moduleForComponent } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('investigate-files-container', 'Integration | Component | investigate files container', {
  integration: true,
  resolver: engineResolverFor('investigate-files')
});
