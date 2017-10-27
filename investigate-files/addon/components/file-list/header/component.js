import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';
import { connect } from 'ember-redux';
import { updateColumnVisibility } from 'investigate-files/actions/data-creators';

const dispatchToActions = {
  updateColumnVisibility
};
const tableHeader = RSADataTableHeader.extend({
  actions: {
    toggleColumn(column) {
      column.toggleProperty('selected');
      this.send('updateColumnVisibility', column);
    }
  }
});
export default connect(undefined, dispatchToActions)(tableHeader);