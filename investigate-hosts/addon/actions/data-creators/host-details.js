import { initializeHostsPreferences } from 'investigate-hosts/actions/data-creators/host';
import { fetchDataForSelectedTab } from 'investigate-hosts/actions/data-creators/details';
import { changeEndpointServer, setSelectedMachineServerId } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import { getServiceId } from 'investigate-shared/actions/data-creators/investigate-creators';
import { setSelectedHost, resetHostDownloadLink } from 'investigate-hosts/actions/ui-state-creators';
import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from '../types';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import { HostDetails, Machines } from '../api';
import { handleError } from '../creator-utils';
import { fetchHostContext, setFocusedHost } from './host';
import { getSubDirectories } from './downloads';

const changeDetailTab = (tabName, subTabName) => {
  return (dispatch) => {
    dispatch({ type: SHARED_ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: null });
    dispatch({ type: ACTION_TYPES.CHANGE_DETAIL_TAB, payload: { tabName, subTabName } });
  };
};

const _fetchPolicyDetails = (agentId) => ({
  type: ACTION_TYPES.FETCH_POLICY_DETAILS,
  promise: HostDetails.policyDetails({ agentId }),
  meta: { onFailure: (response) => handleError(ACTION_TYPES.FETCH_POLICY_DETAILS, response) }
});

const getMFTDetails = (mftName, mftFile) => {
  return async(dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_MFT_FILE_DATA });
    dispatch({ type: ACTION_TYPES.INIT_MFT_DATA, payload: { mftName, mftFile } });
    await dispatch(getSubDirectories());
  };
};

const getRequiredHostInformation = () => {
  return async(dispatch) => {
    await dispatch(getHostDetails());
    await dispatch(getMachineById());
    // Order matters bellow call must be after getMachineById()
    await dispatch(getPolicyDetails());
    await dispatch(getHostContext());
  };
};

const getPolicyDetails = () => {
  return async(dispatch, getState) => {
    const state = getState();
    const hostItem = state.endpoint.overview.hostOverview;
    const { agentId } = state.endpoint.detailsInput;
    const request = lookup('service:request');
    request.registerPersistentStreamOptions({ socketUrlPostfix: hostItem.serviceId, requiredSocketUrl: 'endpoint/socket' });
    dispatch(_fetchPolicyDetails(agentId));
  };
};

const getMachineById = () => {
  return async(dispatch, getState) => {
    const state = getState();
    const { agentId } = state.endpoint.detailsInput;
    const expressionList = [{ propertyName: 'id', restrictionType: 'IN', propertyValues: [{ value: agentId }] }];
    const { hostColumnSort } = state.endpoint.machines;
    await dispatch({
      type: ACTION_TYPES.FETCH_HOST_OVERVIEW,
      promise: Machines.getPageOfMachines(-1, hostColumnSort, expressionList)
    });
  };
};


const getHostContext = (host) => {
  return async(dispatch, getState) => {
    const hostItem = getState().endpoint.overview.hostOverview || host;
    dispatch(setSelectedHost(hostItem));
    dispatch(setFocusedHost(hostItem));
    dispatch(fetchHostContext(hostItem.machineIdentity.machineName));
  };
};


const getScanSnapshots = (agentId) => {
  return async(dispatch) => {
    await dispatch({
      type: ACTION_TYPES.FETCH_ALL_SNAP_SHOTS,
      promise: HostDetails.getAllSnapShots({ agentId }),
      meta: {
        onSuccess: ({ data }) => {
          const [scanTime] = data.length ? data : [{}];
          dispatch({ type: ACTION_TYPES.INITIALIZE_DATA, payload: { agentId, scanTime: scanTime ? scanTime.scanStartTime : null } });
          dispatch(setSelectedMachineServerId(scanTime.serviceId));
        }
      }
    });
  };
};

const getHostDetails = () => {
  return async(dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    await dispatch({
      type: ACTION_TYPES.FETCH_HOST_DETAILS,
      promise: HostDetails.getHostDetails({ agentId, scanTime }),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_HOST_DETAILS, response)
      }
    });
  };
};

const setDataForHostTab = (agentId, tabName, subTabName) => {
  return async(dispatch) => {
    dispatch(changeDetailTab(tabName, subTabName));
    dispatch(fetchDataForSelectedTab());
  };
};


const initializeHostDetailsPage = ({ sid, id: agentId }) => {
  return async(dispatch, getState) => {
    const id = sid || getState().endpointQuery.serverId;

    dispatch(getServiceId('MACHINE'));

    await dispatch(changeEndpointServer({ id }));
    // Wait for user preference to load
    await dispatch(initializeHostsPreferences());

    // get the snapshot
    await dispatch(getScanSnapshots(agentId));

    // get the host details
    await dispatch(getRequiredHostInformation(agentId));

    dispatch(resetHostDownloadLink());
  };
};


export {
  initializeHostDetailsPage,
  getScanSnapshots,
  getHostDetails,
  setDataForHostTab,
  getMFTDetails
};
