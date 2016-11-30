import TreeTable from 'component-lib/components/rsa-content-accordion' ;
import computed from 'ember-computed-decorators';

export default TreeTable.extend({
  @computed('isCollapsed')
  arrowDirection: (arrowDirection) => arrowDirection ? 'right' : 'down'
});
