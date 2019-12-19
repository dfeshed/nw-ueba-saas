import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { certificatesCount, certificatesCountForDisplay } from 'investigate-files/reducers/certificates/selectors';

const stateToComputed = (state) => ({
  certificatesTotal: certificatesCountForDisplay(state), // Total number of certificates in search result
  certificatesIndex: certificatesCount(state),
  selections: state.certificate.list.selectedCertificateList || []
});

@classic
@tagName('section')
@classNames('certificates-pager')
class Pager extends Component {
  @computed('selections')
  get count() {
    return this.selections.length;
  }
}

export default connect(stateToComputed)(Pager);
