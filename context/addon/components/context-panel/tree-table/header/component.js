import TreeTable from 'component-lib/components/rsa-content-accordion' ;
import computed from 'ember-computed-decorators';
import layout from './template';

export default TreeTable.extend({
  layout,

  @computed('isCollapsed')
  arrowDirection: (arrowDirection) => arrowDirection ? 'right' : 'down'
});
