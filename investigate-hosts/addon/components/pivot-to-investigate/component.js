import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAllServices } from 'investigate-hosts/actions/data-creators/host';
import { navigateToInvestigateEventsAnalysis, navigateToInvestigateNavigate } from 'investigate-shared/utils/pivot-util';

const dispatchToActions = {
  getAllServices
};


@classic
@tagName('span')
@classNames('pivot-to-investigate')
class PivotToInvestigate extends Component {
  @service
  timezone;

  showAsRightClick = false;
  showServiceModal = false;
  metaName = null;
  metaValue = null;
  serviceList = null;
  item = null;
  size = 'small';
  investigateText = null;
  serviceId = null;

  init() {
    super.init(...arguments);
    this.timeRange = this.timeRange || { value: 2, unit: 'days' };
  }

  @computed
  get contextItems() {
    const cntx = this;
    return [
      {
        label: 'Analyze Events',
        action() {
          const serviceId = cntx.get('serviceId');
          cntx.send('pivotToInvestigateNavigate', serviceId);
        }
      }
    ];
  }

  _closeModal() {
    this.set('showServiceModal', false);
  }

  @action
  pivotToInvestigate() {
    const serviceId = this.get('serviceId');
    if (serviceId && serviceId !== '-1') {
      const {
        metaName,
        metaValue,
        item,
        timeRange
      } = this.getProperties('metaName', 'metaValue', 'item', 'timeRange');

      const { zoneId } = this.get('timezone.selected');
      navigateToInvestigateEventsAnalysis({ metaName, metaValue, itemList: [item] }, serviceId, timeRange, zoneId);
    } else {
      const serviceList = this.get('serviceList');
      if (!(serviceList && serviceList.length)) {
        this.send('getAllServices');
      }
      this.set('showServiceModal', true);
    }
  }

  @action
  onCancel() {
    this._closeModal();
  }

  @action
  pivotToInvestigateEventAnalysis(serviceId) {
    const {
      metaName,
      metaValue,
      item,
      timeRange
    } = this.getProperties('metaName', 'metaValue', 'item', 'timeRange');

    const { zoneId } = this.get('timezone.selected');
    this._closeModal();
    navigateToInvestigateEventsAnalysis({ metaName, metaValue, itemList: [item] }, serviceId, timeRange, zoneId);
  }

  @action
  pivotToInvestigateNavigate(serviceId) {
    const {
      metaName,
      metaValue,
      item,
      timeRange
    } = this.getProperties('metaName', 'metaValue', 'item', 'timeRange');

    const { zoneId } = this.get('timezone.selected');
    this._closeModal();
    navigateToInvestigateNavigate({ metaName, metaValue, itemList: [item] }, serviceId, timeRange, zoneId);
  }

  @action
  onModalClose() {
    this.set('showServiceModal', false);
  }
}

export default connect(undefined, dispatchToActions)(PivotToInvestigate);
