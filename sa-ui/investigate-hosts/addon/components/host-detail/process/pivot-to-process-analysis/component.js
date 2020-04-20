import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAllServices } from 'investigate-hosts/actions/data-creators/host';
import { next } from '@ember/runloop';
import { buildTimeRange } from 'investigate-shared/utils/time-util';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const dispatchToActions = {
  getAllServices
};

const stateToComputed = (state) => ({
  serverId: state.endpointQuery.serverId,
  serviceId: serviceId(state),
  timeRange: timeRange(state)
});

@classic
@tagName('span')
@classNames('pivot-to-process-analysis')
class PivotToPA extends Component {
  showServiceModal = false;

  @service
  timezone;

  @service
  eventBus;

  @service
  features;

  serviceList = null;
  agentId = null;
  osType = null;
  hostName = null;

  @computed('item')
  get disableAnalyzeButton() {
    const item = this.item || [];
    if (!item.length || item.length > 1 || this.get('osType') === 'linux') {
      return true;
    }
    return false;
  }

  @computed('isSortDescending')
  get iconName() {
    return this.isSortDescending ? 'arrow-down-7' : 'arrow-up-7';
  }

  contextItems() {
    const cntx = this;
    return [
      {
        label: 'Process Analysis',
        action([item]) {
          cntx.send('toggleServiceSelection', item);
        }
      }
    ];
  }

  _closeModal() {
    this.get('eventBus').trigger('rsa-application-modal-close-service-modal');
    this.set('showServiceModal', false);
  }

  /**
   * navigate to process analysis page
   * @public
   */
  @action
  navigateToProcessAnalysis(serviceId) {
    const { zoneId } = this.get('timezone.selected');
    const {
      item,
      agentId,
      osType,
      hostName,
      timeRange } = this.getProperties('item', 'agentId', 'osType', 'hostName', 'timeRange');
    const [{ name, checksumSha256, vpid }] = item;
    const { value, unit } = timeRange;
    const serverId = this.get('serverId');
    const range = buildTimeRange(value, unit, zoneId);
    const timeStr = `st=${range.startTime}&et=${range.endTime}`;
    const osTypeParam = `osType=${osType}&vid=${vpid}`;
    const queryParams = `checksum=${checksumSha256}&sid=${serviceId}&aid=${agentId}&hn=${hostName}&pn=${name}&${timeStr}&${osTypeParam}&serverId=${serverId}`;
    window.open(`${window.location.origin}/investigate/process-analysis?${queryParams}`, '_blank', 'width=1440,height=900');
    this._closeModal();
  }

  @action
  toggleServiceSelection(item) {
    const serviceId = this.get('serviceId');
    this.set('item', item);
    if (serviceId && serviceId !== '-1') {
      this.send('navigateToProcessAnalysis', serviceId);
    } else {
      this.set('showServiceModal', true);
      this.send('getAllServices');
      next(() => {
        this.get('eventBus').trigger('rsa-application-modal-open-service-modal');
      });
    }
  }

  @action
  onCancel() {
    this._closeModal();
  }

  @action
  onModalClose() {
    this.set('showServiceModal', false);
  }
}

export default connect(stateToComputed, dispatchToActions)(PivotToPA);
