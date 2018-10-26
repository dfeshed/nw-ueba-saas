import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';
import { connect } from 'ember-redux';
import { updateCertificateColumnVisibility } from 'investigate-files/actions/certificate-data-creators';

const dispatchToActions = {
  updateCertificateColumnVisibility
};
const tableHeader = RSADataTableHeader.extend({
  actions: {
    toggleColumn(column) {
      column.toggleProperty('selected');
      const { field, selected } = column.getProperties('field', 'selected');
      this.send('updateCertificateColumnVisibility', { field, selected });
    }
  }
});
export default connect(undefined, dispatchToActions)(tableHeader);
